package eu.albertvila.popularmovies.stage2.data.repository;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;

/**
 * Created by Albert Vila Calvo on 28/5/16.
 */
public interface MovieRepository {

    List<Movie> getMovies(String sortOrder);
}
