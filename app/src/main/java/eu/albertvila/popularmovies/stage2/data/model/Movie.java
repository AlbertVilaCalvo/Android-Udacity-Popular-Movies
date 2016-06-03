package eu.albertvila.popularmovies.stage2.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */
public class Movie {

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("poster_path")
    private String posterPath;

    // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    public String getPosterUrl() {
        return "http://image.tmdb.org/t/p/w185" + posterPath;
    }

}
