package eu.albertvila.popularmovies.stage2.feature.movielist;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 20/1/16.
 */
public class MovieListPresenter implements MovieList.Presenter {

    private MovieList.View view;
    private MovieRepository movieRepository;

    public MovieListPresenter(MovieRepository movieRepository) {
        Timber.i("New MovieListPresenter created");
        this.movieRepository = movieRepository;
    }

    // MovieList.Presenter

    @Override
    public void setView(MovieList.View view) {
        this.view = view;
    }

    // TODO watch this videos
    // https://caster.io/episodes/retrofit2-with-rxjava/
    // https://caster.io/courses/rxjava/

    @Override
    public void getMovies() {
        Observable<List<Movie>> observable = movieRepository.getMovies(MovieDbService.SORT_BY_POPULARITY);

        // TODO unsubscribe:
        // if (subscription != null && !subscription.isUnsubscribed()) {
        //    subscription.unsubscribe();
        // }
        observable.subscribe(new Subscriber<List<Movie>>() {
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
                }
            }
        });
    }
}
