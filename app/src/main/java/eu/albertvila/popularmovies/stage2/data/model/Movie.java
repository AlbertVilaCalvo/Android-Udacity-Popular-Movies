package eu.albertvila.popularmovies.stage2.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */

@AutoValue
public abstract class Movie {

    @SerializedName("original_title")
    public abstract String originalTitle();

    @SerializedName("poster_path")
    abstract String posterPath();

    // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    public String posterUrl() {
        return "http://image.tmdb.org/t/p/w185" + posterPath();
    }


    // https://github.com/rharter/auto-value-gson
    // The public static method returning a TypeAdapter<Foo> is what
    // tells auto-value-gson to create a TypeAdapter for Foo.
    public static TypeAdapter<Movie> typeAdapter(Gson gson) {
        return new AutoValue_Movie.GsonTypeAdapter(gson);
    }

}
