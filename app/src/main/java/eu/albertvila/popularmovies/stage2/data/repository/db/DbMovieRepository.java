package eu.albertvila.popularmovies.stage2.data.repository.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
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
                    int susccessInsertCount = 0;
                    for (Movie movie : movies) {
                        // http://stackoverflow.com/questions/13311727/android-sqlite-insert-or-update
                        // https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#CONFLICT_REPLACE
                        ContentValues contentValues = Movie.buildContentValues(movie);
                        long id = db.insert(Movie.TABLE, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

                        if (id == -1) {
                            Timber.e("fetchMovies() insert() error on Movie with id %d", movie.id());
                        } else {
                            susccessInsertCount++;
                        }

                        // WE could also use db.executeAndTrigger(); with a raw query to update or insert
                        // http://stackoverflow.com/questions/4205181/insert-into-a-mysql-table-or-update-if-exists
                        // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace
                    }
                    Timber.i("fetchMovies() insert() success/total %d/%d", susccessInsertCount, movies.size());
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

    private Movie selectedMovie;

    // A BehaviourSubject emits the most recently emitted Movie when an observer subscribes to it
    private BehaviorSubject<Movie> movieSubject = BehaviorSubject.create();
    private Subscription selectedMovieSubscription;

    @Override
    public void setSelectedMovie(Movie movie) {
        selectedMovie = movie;

        if (selectedMovieSubscription != null && !selectedMovieSubscription.isUnsubscribed()) {
            selectedMovieSubscription.unsubscribe();
            Timber.i("DbMovieRepository selectedMovieSubscription.unsubscribe()");
        }

        Observable<Movie> selectedMovieObservable = db
                .createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE + " WHERE " + Movie.ID  + " = ?", String.valueOf(selectedMovie.id()))
                .map(Movie.QUERY_TO_ITEM_MAPPER)
                .observeOn(AndroidSchedulers.mainThread());

        selectedMovieSubscription = selectedMovieObservable.subscribe(new Subscriber<Movie>() {
            @Override
            public void onCompleted() {
                Timber.i("DbMovieRepository setSelectedMovie() onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "DbMovieRepository setSelectedMovie() onError()");
            }

            @Override
            public void onNext(Movie movie) {
                Timber.i("DbMovieRepository setSelectedMovie() onNext() - movie: %s", movie.toString());
                movieSubject.onNext(movie);
            }
        });
    }

    @Override
    public Observable<Movie> observeSelectedMovie() {
//        Observable<SqlBrite.Query> selectedMovieQuery = db.createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE + " WHERE _id = " + selectedMovieId);
//        return selectedMovieQuery.map(Movie.QUERY_TO_ITEM_MAPPER);
        return movieSubject;
    }

}
