package eu.albertvila.popularmovies.stage2.data.repository.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.api.ReviewsResponse;
import eu.albertvila.popularmovies.stage2.data.api.VideosResponse;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.model.Review;
import eu.albertvila.popularmovies.stage2.data.model.Video;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 12/6/16.
 */
public class DbMovieRepository implements MovieRepository {

    private MovieDbService movieDbService;
    private String apiKey;
    private BriteDatabase db;
    private ShowMovieCriteria showMovieCriteria;

    public DbMovieRepository(MovieDbService movieDbService, String apiKey, ShowMovieCriteria defaultCriteria, BriteDatabase db) {
        Timber.i("New DbMovieRepository created");
        this.movieDbService = movieDbService;
        this.apiKey = apiKey;
        this.showMovieCriteria = defaultCriteria;
        this.db = db;
    }

    @Override
    public void setShowMovieCriteria(ShowMovieCriteria criteria) {
        this.showMovieCriteria = criteria;
        Timber.i("DbMovieRepository setShowMovieCriteria() to %s", criteria);
    }

    @Override
    public ShowMovieCriteria getShowMovieCriteria() {
        return showMovieCriteria;
    }

    @Override
    public Observable<List<Movie>> observeMovies() {
        Observable<SqlBrite.Query> moviesQuery = db.createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE);
        return moviesQuery
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        fetchMovies();
                    }
                })
                // We could also use mapToList
                .map(Movie.QUERY_TO_LIST_MAPPER)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void fetchMovies() {
        // Note: if showMovieCriteria is ShowMovieCriteria.FAVORITES, we fetch the movies by popularity
        String sortOrder = MovieDbService.SORT_BY_POPULARITY;
        if (showMovieCriteria == ShowMovieCriteria.BEST_RATED) {
            sortOrder = MovieDbService.SORT_BY_RATING;
        }

        Call<DiscoverMoviesResponse> call = movieDbService.discoverMovies(apiKey, sortOrder);
        call.enqueue(new Callback<DiscoverMoviesResponse>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResponse> call, Response<DiscoverMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    // response.code() is not in range [200...3000)
                    Timber.d("fetchMovies() !response.isSuccessful()");
                    return;
                }

                List<Movie> movies = response.body().getMovies();
                Timber.d("fetchMovies() movies.size() %d", movies.size());

                if (movies.size() == 0) {
                    return;
                }

                // TODO do this in the background!
                // Save movies to db
                // We use transactions to prevent large changes to the data from spamming the subscriber
                BriteDatabase.Transaction transaction = db.newTransaction();
                try {
                    int successInsertCount = 0;
                    for (Movie movie : movies) {
                        ContentValues contentValues = Movie.buildContentValuesWithoutFavorite(movie);

                        // http://stackoverflow.com/questions/13311727/android-sqlite-insert-or-update
                        // https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#CONFLICT_REPLACE
                        // If we do this:
                        // long id = db.insert(Movie.TABLE, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                        // We replace the entire row if already exists and we loose the 'favorite' value! :(

                        // http://stackoverflow.com/a/20568176/4034572
                        // We insert and, if it fails (because the column already exists), then we update.
                        // Note that we do NOT update 'favorite' in order to preserve it's value!
                        long id = db.insert(Movie.TABLE, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                        if (id == -1) {
                            id = db.update(Movie.TABLE, contentValues, Movie.ID  + " = ?", String.valueOf(movie.id()));
                        }

                        if (id == -1) {
                            Timber.e("fetchMovies() insert() error on Movie with id %d", movie.id());
                        } else {
                            successInsertCount++;
                        }

                        // WE could also use db.executeAndTrigger(); with a raw query to update or insert
                        // http://stackoverflow.com/questions/4205181/insert-into-a-mysql-table-or-update-if-exists
                        // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace
                    }
                    Timber.i("fetchMovies() insert() success/total %d/%d", successInsertCount, movies.size());
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }
            }

            @Override
            public void onFailure(Call<DiscoverMoviesResponse> call, Throwable t) {
                Timber.e(t, "fetchMovies() onFailure()");
            }
        });
    }


    // SELECTED MOVIE

    // A BehaviourSubject emits the most recently emitted Movie when an observer subscribes to it.
    // We can retrieve the current selected movie with selectedMovieSubject.getValue()
    private BehaviorSubject<Movie> selectedMovieSubject = BehaviorSubject.create();

    private Subscription selectedMovieSubscription; // to unsubscribe

    // We can't use a Subscriber<Movie> because a Subscriber can't be reused once unsubscribed:
    // http://stackoverflow.com/a/30222908/4034572
    // http://stackoverflow.com/questions/27664221/what-is-the-difference-between-an-observer-and-a-subscriber
    private Observer<Movie> selectedMovieObserver = new Observer<Movie>() {
        @Override
        public void onCompleted() {
            Timber.i("DbMovieRepository selectedMovieObserver onCompleted()");
        }
        @Override
        public void onError(Throwable e) {
            Timber.e(e, "DbMovieRepository selectedMovieObserver onError()");
        }
        @Override
        public void onNext(Movie movie) {
            Timber.i("DbMovieRepository selectedMovieObserver onNext() - movie: %s", movie.toString());
            selectedMovieSubject.onNext(movie);
        }
    };

    @Override
    public void setSelectedMovie(Movie movie) {
        // Check if it's the same as the current selected movie
        if (movie.equals(selectedMovieSubject.getValue())) {
            return;
        }

        subscribeToSelectedMovie(movie.id());

        subscribeToVideosForSelectedMovie(movie.id());
        subscribeToReviewsForSelectedMovie(movie.id());

        getVideosForMovie(movie.id());
        getReviewsForMovie(movie.id());
    }

    private void subscribeToSelectedMovie(long movieId) {
        if (selectedMovieSubscription != null && !selectedMovieSubscription.isUnsubscribed()) {
            selectedMovieSubscription.unsubscribe();
            Timber.i("DbMovieRepository selectedMovieSubscription.unsubscribe()");
        }

        Observable<Movie> selectedMovieObservable = db
                .createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE + " WHERE " + Movie.ID  + " = ?", String.valueOf(movieId))
                .map(Movie.QUERY_TO_ITEM_MAPPER)
                .observeOn(AndroidSchedulers.mainThread());

        selectedMovieSubscription = selectedMovieObservable.subscribe(selectedMovieObserver);

//        selectedMovieSubscription = selectedMovieObservable.subscribe(new Subscriber<Movie>() {
//            @Override
//            public void onCompleted() {
//                Timber.i("DbMovieRepository setSelectedMovie() onCompleted()");
//            }
//            @Override
//            public void onError(Throwable e) {
//                Timber.e(e, "DbMovieRepository setSelectedMovie() onError()");
//            }
//            @Override
//            public void onNext(Movie movie) {
//                Timber.i("DbMovieRepository setSelectedMovie() onNext() - movie: %s", movie.toString());
//                selectedMovieSubject.onNext(movie);
//            }
//        });
    }

    @Override
    public Observable<Movie> observeSelectedMovie() {
        return selectedMovieSubject;
    }

    @Override
    public void favoriteButtonClick() {
        // Get current selected movie from subject
        Movie selectedMovie = selectedMovieSubject.getValue();
        // Toggle favorite
        int newFavoriteValue = selectedMovie.isFavorite() ? 0 : 1;
        // Update DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(Movie.FAVORITE, newFavoriteValue);
        db.update(Movie.TABLE, contentValues, Movie.ID  + " = ?", String.valueOf(selectedMovie.id()));
        Timber.i("Update movie '%s' - set favorite to %d", selectedMovie.originalTitle(), newFavoriteValue);
    }


    // VIDEOS

    private void getVideosForMovie(long movieId) {
        Observable<VideosResponse> observable = movieDbService.getVideosForMovieRx(movieId, apiKey);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                // TODO filter videos that are not from site "YouTube"
                .subscribe(new Subscriber<VideosResponse>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("getVideosForMovie() onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "getVideosForMovie() onError()");
                    }

                    @Override
                    public void onNext(VideosResponse videosResponse) {
                        // Timber.i("getVideosForMovie() onNext() thread: %s", Thread.currentThread().getName());
                        Timber.i("getVideosForMovie() onNext() videosResponse %s", videosResponse);
                        // Save to the DB
                        BriteDatabase.Transaction transaction = db.newTransaction();
                        try {
                            int successInsertCount = 0;
                            long movieId = videosResponse.id;
                            for (Video video : videosResponse.results) {
                                long id = db.insert(Video.TABLE_NAME,
                                        Video.FACTORY.marshal()
                                            .id(video.id())
                                            .movie_id(movieId)
                                            .name(video.name())
                                            .key(video.key())
                                            .asContentValues(),
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                if (id != -1) successInsertCount++;
                            }
                            Timber.i("getVideosForMovie() insert() success/total %d/%d", successInsertCount, videosResponse.results.size());
                            transaction.markSuccessful();
                        } finally {
                            transaction.end();
                        }
                    }
                });
    }

    private BehaviorSubject<List<Video>> videosForSelectedMovieSubject = BehaviorSubject.create();
    private Subscription videosForSelectedMovieSubscription; // to unsubscribe
    private Observer<List<Video>> videosForSelectedMovieObserver = new Observer<List<Video>>() {
        @Override
        public void onCompleted() {
            Timber.i("DbMovieRepository videosForSelectedMovieObserver onCompleted()");
        }
        @Override
        public void onError(Throwable e) {
            Timber.e(e, "DbMovieRepository videosForSelectedMovieObserver onError()");
        }
        @Override
        public void onNext(List<Video> videos) {
            Timber.i("DbMovieRepository videosForSelectedMovieObserver onNext() - movie: %s", videos.toString());
            videosForSelectedMovieSubject.onNext(videos);
        }
    };

    private void subscribeToVideosForSelectedMovie(long movieId) {
        if (videosForSelectedMovieSubscription != null && !videosForSelectedMovieSubscription.isUnsubscribed()) {
            videosForSelectedMovieSubscription.unsubscribe();
            Timber.i("DbMovieRepository videosForSelectedMovieSubscription.unsubscribe()");
        }

        Observable<List<Video>> videosForSelectedMovieObservable = db
                .createQuery(Video.TABLE_NAME, Video.FOR_MOVIE, String.valueOf(movieId))
                .map(new Func1<SqlBrite.Query, List<Video>>() {
                    @Override
                    public List<Video> call(SqlBrite.Query query) {
                        Timber.i("subscribeToVideosForSelectedMovie map thread: %s", Thread.currentThread().getName());
                        Cursor cursor = query.run();
                        try {
                            List<Video> videos = new ArrayList<>(cursor.getCount());
                            while (cursor.moveToNext()) {
                                Video video = Video.MAPPER.map(cursor);
                                videos.add(video);
                            }
                            return videos;
                        } finally {
                            cursor.close();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        videosForSelectedMovieSubscription = videosForSelectedMovieObservable.subscribe(videosForSelectedMovieObserver);
    }

    @Override
    public Observable<List<Video>> observeVideosForSelectedMovie() {
        return videosForSelectedMovieSubject;
    }


    // REVIEWS

    private void getReviewsForMovie(long movieId) {
        Observable<ReviewsResponse> observable = movieDbService.getReviewsForMovieRx(movieId, apiKey);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Subscriber<ReviewsResponse>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("getReviewsForMovie() onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "getReviewsForMovie() onError()");
                    }

                    @Override
                    public void onNext(ReviewsResponse reviewsResponse) {
                        // Timber.i("getReviewsForMovie() onNext() thread: %s", Thread.currentThread().getName());
                        Timber.i("getReviewsForMovie() onNext() reviewsResponse %s", reviewsResponse);
                        // Save to the DB
                        BriteDatabase.Transaction transaction = db.newTransaction();
                        try {
                            int successInsertCount = 0;
                            long movieId = reviewsResponse.id;
                            for (Review review : reviewsResponse.results) {
                                long id = db.insert(Review.TABLE_NAME,
                                        Review.FACTORY.marshal()
                                                .id(review.id())
                                                .movie_id(movieId)
                                                .author(review.author())
                                                .content(review.content())
                                                .url(review.url())
                                                .asContentValues(),
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                if (id != -1) successInsertCount++;
                            }
                            Timber.i("getReviewsForMovie() insert() success/total %d/%d", successInsertCount, reviewsResponse.results.size());
                            transaction.markSuccessful();
                        } finally {
                            transaction.end();
                        }
                    }
                });
    }

    private BehaviorSubject<List<Review>> reviewsForSelectedMovieSubject = BehaviorSubject.create();
    private Subscription reviewsForSelectedMovieSubscription; // to unsubscribe
    private Observer<List<Review>> reviewsForSelectedMovieObserver = new Observer<List<Review>>() {
        @Override
        public void onCompleted() {
            Timber.i("DbMovieRepository reviewsForSelectedMovieObserver onCompleted()");
        }
        @Override
        public void onError(Throwable e) {
            Timber.e(e, "DbMovieRepository reviewsForSelectedMovieObserver onError()");
        }
        @Override
        public void onNext(List<Review> reviews) {
            Timber.i("DbMovieRepository reviewsForSelectedMovieObserver onNext() - movie: %s", reviews.toString());
            reviewsForSelectedMovieSubject.onNext(reviews);
        }
    };

    private void subscribeToReviewsForSelectedMovie(long movieId) {
        if (reviewsForSelectedMovieSubscription != null && !reviewsForSelectedMovieSubscription.isUnsubscribed()) {
            reviewsForSelectedMovieSubscription.unsubscribe();
            Timber.i("DbMovieRepository reviewsForSelectedMovieSubscription.unsubscribe()");
        }

        Observable<List<Review>> reviewsForSelectedMovieObservable = db
                .createQuery(Review.TABLE_NAME, Review.FOR_MOVIE, String.valueOf(movieId))
                .map(new Func1<SqlBrite.Query, List<Review>>() {
                    @Override
                    public List<Review> call(SqlBrite.Query query) {
                        Timber.i("subscribeToReviewsForSelectedMovie map thread: %s", Thread.currentThread().getName());
                        Cursor cursor = query.run();
                        try {
                            List<Review> reviews = new ArrayList<>(cursor.getCount());
                            while (cursor.moveToNext()) {
                                Review review = Review.MAPPER.map(cursor);
                                reviews.add(review);
                            }
                            return reviews;
                        } finally {
                            cursor.close();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        reviewsForSelectedMovieSubscription = reviewsForSelectedMovieObservable.subscribe(reviewsForSelectedMovieObserver);
    }

    @Override
    public Observable<List<Review>> observeReviewsForSelectedMovie() {
        return reviewsForSelectedMovieSubject;
    }

}
