package eu.albertvila.popularmovies.stage2.feature.movielist;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */
public interface MovieList {

    interface View {
        void showMovies(List<Movie> movies);
        void showProgress();
    }

    interface Presenter {
        void start(MovieList.View view);
        void stop();
        void setMovieType(@MovieRepository.MovieType int type);
    }

}
