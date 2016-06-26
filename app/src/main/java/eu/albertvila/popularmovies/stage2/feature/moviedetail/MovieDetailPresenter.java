package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 26/6/16.
 */
public class MovieDetailPresenter implements MovieDetail.Presenter {

    private MovieRepository movieRepository;
    private MovieDetail.View view;

    public MovieDetailPresenter(MovieRepository movieRepository) {
        Timber.i("New MovieDetailPresenter created");
        this.movieRepository = movieRepository;
    }

    @Override
    public void start(MovieDetail.View view) {
        this.view = view;
    }

    @Override
    public void stop() {
        this.view = null;
    }

    @Override
    public void favoriteButtonClick() {

    }

}
