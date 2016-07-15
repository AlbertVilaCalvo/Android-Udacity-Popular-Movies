package eu.albertvila.popularmovies.stage2.data.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;


/**
 * Created by albertvilacalvo on 20/10/15.
 */
public class MoviesResponse {

    // Gson tutorial:
    // https://github.com/codepath/android_guides/wiki/Leveraging-the-Gson-Library

    @SerializedName("results")
    List<Movie> movies;

    public MoviesResponse() {
        movies = new ArrayList<Movie>();
    }

    public List<Movie> getMovies() {
        return movies;
    }

}
