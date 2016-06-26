package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import eu.albertvila.popularmovies.stage2.data.model.Movie;

/**
 * Created by Albert Vila Calvo on 26/6/16.
 */
public interface MovieDetail {

    interface View {
        void showMovie(Movie movies);
    }

    interface Presenter {
        void start(MovieDetail.View view);
        void stop();
        void favoriteButtonClick();
    }

}
