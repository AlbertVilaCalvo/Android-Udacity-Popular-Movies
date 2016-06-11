package eu.albertvila.popularmovies.stage2.feature.movielist;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 20/1/16.
 */
public class MovieListPresenter implements MovieList.Presenter {

    private MovieList.View view;
    private MovieRepository movieRepository;

    public MovieListPresenter(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // MovieList.Presenter

    @Override
    public void setView(MovieList.View view) {
        this.view = view;
    }

    @Override
    public void getMovies() {
        Observable<List<Movie>> observable = movieRepository.getMovies(MovieDbService.SORT_BY_POPULARITY);

        observable.subscribe(new Action1<List<Movie>>() {
            @Override
            public void call(List<Movie> movies) {
                if (view != null) {
                    view.showMovies(movies);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "MovieListPresenter getMovies() onError()");
            }
        });
    }
}
