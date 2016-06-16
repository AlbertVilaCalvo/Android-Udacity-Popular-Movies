package eu.albertvila.popularmovies.stage2.data.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */

@AutoValue
public abstract class Movie {

    public static final String TABLE = "movie";

    public static final String ID = "_id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";

    public abstract long id();

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

    public static Func1<SqlBrite.Query, List<Movie>> QUERY_TO_LIST_MAPPER = new Func1<SqlBrite.Query, List<Movie>>() {
        @Override
        public List<Movie> call(SqlBrite.Query query) {
            Cursor cursor = query.run();
            try {
                List<Movie> movies = new ArrayList<Movie>(cursor.getCount());
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(Movie.ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(Movie.ORIGINAL_TITLE));
                    String posterPath = cursor.getString(cursor.getColumnIndexOrThrow(Movie.POSTER_PATH));
                    Movie movie = new AutoValue_Movie(id, title, posterPath);
                    movies.add(movie);
                }
                return movies;
            } finally {
                cursor.close();
            }
        }
    };

    public static ContentValues buildContentValues(long id, String originalTitle, String posterPath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(ORIGINAL_TITLE, originalTitle);
        contentValues.put(POSTER_PATH, posterPath);
        return contentValues;
    }

}
