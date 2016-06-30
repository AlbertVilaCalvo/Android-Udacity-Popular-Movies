package eu.albertvila.popularmovies.stage2.feature.moviedetail;


import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 26/6/16.
 */
public class MovieDetailPresenter implements MovieDetail.Presenter {

    private MovieRepository movieRepository;
    private MovieDetail.View view;
    private Subscription subscription;

    public MovieDetailPresenter(MovieRepository movieRepository) {
        Timber.i("New MovieDetailPresenter created");
        this.movieRepository = movieRepository;
    }

    @Override
    public void start(MovieDetail.View view) {
        this.view = view;
        getMovie();
    }

    @Override
    public void stop() {
        this.view = null;
        unsubscribe();
    }

    @Override
    public void favoriteButtonClick() {

    }

    private void getMovie() {
        Observable<Movie> observable = movieRepository.observeSelectedMovie();

        subscription = observable.subscribe(new Subscriber<Movie>() {
            @Override
            public void onCompleted() {
                Timber.i("MovieDetailPresenter getMovie() onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "MovieDetailPresenter getMovie() onError()");
            }

            @Override
            public void onNext(Movie movie) {
                Timber.i("MovieDetailPresenter getMovies() onNext() - movie: %s", movie.toString());
                if (view != null) {
                    view.showMovie(movie);
                }
            }
        });
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Timber.i("MovieDetailPresenter subscription.unsubscribe()");
        }
    }

}
