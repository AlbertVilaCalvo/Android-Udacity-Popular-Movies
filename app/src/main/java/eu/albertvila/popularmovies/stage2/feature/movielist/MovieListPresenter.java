package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.support.annotation.NonNull;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
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
    }

    @Override
    public void menuItemShowMovieCriteriaClick() {
        if (view != null) {
            ShowMovieCriteria criteria = movieRepository.getShowMovieCriteria();
            view.showMovieCriteriaDialog(criteria);
        }
    }

    // TODO watch this videos
    // https://caster.io/episodes/retrofit2-with-rxjava/
    // https://caster.io/courses/rxjava/

    public void getMovies() {
        Observable<List<Movie>> observable = movieRepository.observeMovies();

        subscription = observable.subscribe(new Subscriber<List<Movie>>() {
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

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Timber.i("MovieListPresenter subscription.unsubscribe()");
        }
    }

}
