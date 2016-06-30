package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 20/1/16.
 */
public class MovieListPresenter implements MovieList.Presenter {

    private MovieList.View view;
    private MovieRepository movieRepository;
    private Subscription subscription;

    public MovieListPresenter(MovieRepository movieRepository) {
        Timber.i("New MovieListPresenter created");
        this.movieRepository = movieRepository;
    }

    // MovieList.Presenter

    @Override
    public void start(@NonNull MovieList.View view) {
        this.view = view;
        getMovies();
    }

    @Override
    public void stop() {
        this.view = null;
        unsubscribe();
    }

    @Override
    public void newShowMovieCriteriaSelected(ShowMovieCriteria newCriteria) {
        movieRepository.setShowMovieCriteria(newCriteria);
        // Re-subscribe to display movies in new criteria
        getMovies();
    }

    @Override
    public void menuItemShowMovieCriteriaClick() {
        if (view != null) {
            ShowMovieCriteria criteria = movieRepository.getShowMovieCriteria();
            view.showMovieCriteriaDialog(criteria);
        }
    }

    @Override
    public void movieSelected(Movie movie) {
        Timber.i("Selected movie: %s", movie.toString());
        movieRepository.setSelectedMovie(movie);
    }

    // TODO watch this videos
    // https://caster.io/episodes/retrofit2-with-rxjava/
    // https://caster.io/courses/rxjava/

    private void getMovies() {
        unsubscribe();

        Observable<List<Movie>> observable = movieRepository.observeMovies();

        subscription = observable
                .map(new Func1<List<Movie>, List<Movie>>() {
                    @Override
                    public List<Movie> call(List<Movie> movies) {
                        // If we have to show the favorite movies, don't sort them
                        if (movieRepository.getShowMovieCriteria() == ShowMovieCriteria.FAVORITES) {
                            return movies;
                        }

                        // Sort by popularity or by rating
                        List<Movie> sortedMovies = new ArrayList<Movie>(movies);
                        if (movieRepository.getShowMovieCriteria() == ShowMovieCriteria.MOST_POPULAR) {
                            Collections.sort(sortedMovies, new Comparator<Movie>() {
                                @Override
                                public int compare(Movie m1, Movie m2) {
                                    return m1.popularity() > m2.popularity() ? -1 : 1;
                                }
                            });
                        } else if (movieRepository.getShowMovieCriteria() == ShowMovieCriteria.BEST_RATED) {
                            Collections.sort(sortedMovies, new Comparator<Movie>() {
                                @Override
                                public int compare(Movie m1, Movie m2) {
                                    return m1.rating() > m2.rating() ? -1 : 1;
                                }
                            });
                        }
                        return sortedMovies;
                    }
                })
                // To debug sorting
                /*
                .doOnNext(new Action1<List<Movie>>() {
                    @Override
                    public void call(List<Movie> movies) {
                        for (Movie movie : movies) {
                            if (movieRepository.getShowMovieCriteria() == ShowMovieCriteria.BEST_RATED) {
                                Timber.i("rating %f\t\t\t%s", movie.rating(), movie.originalTitle());
                            }
                            if (movieRepository.getShowMovieCriteria() == ShowMovieCriteria.MOST_POPULAR) {
                                Timber.i("popularity %f\t\t\t%s", movie.popularity(), movie.originalTitle());
                            }
                        }
                    }
                })
                */
                .subscribe(new Subscriber<List<Movie>>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("MovieListPresenter getMovies() onCompleted()");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "MovieListPresenter getMovies() onError()");
                    }
                    @Override
                    public void onNext(List<Movie> movies) {
                        Timber.i("MovieListPresenter getMovies() onNext() - movies.size() %d", movies.size());
                        if (view != null) {
                            view.showMovies(movies);
                        } else {
                            Timber.d("MovieListPresenter getMovies() onNext() - view is null :(");
                        }
                    }
                });
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Timber.i("MovieListPresenter subscription.unsubscribe()");
        }
    }

}
