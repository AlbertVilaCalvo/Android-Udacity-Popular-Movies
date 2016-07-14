package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.model.Video;

/**
 * Created by Albert Vila Calvo on 26/6/16.
 */
public interface MovieDetail {

    interface View {
        void showMovie(Movie movies);
        void showVideos(List<Video> videos);
    }

    interface Presenter {
        void start(MovieDetail.View view);
        void stop();
        void favoriteButtonClick();
    }

}
