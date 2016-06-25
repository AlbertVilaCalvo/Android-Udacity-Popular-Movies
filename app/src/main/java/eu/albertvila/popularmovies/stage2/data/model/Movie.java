package eu.albertvila.popularmovies.stage2.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by Albert Vila Calvo on 19/1/16.
 */

@AutoValue
public abstract class Movie {

    public static final String TABLE = "movie";

    // http://stackoverflow.com/questions/3192064/about-id-field-in-android-sqlite
    // http://stackoverflow.com/questions/4313987/do-i-have-to-use-id-as-a-sqlite-primary-key-and-does-it-have-to-be-an-int-an
    public static final String ID = "_id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";
    public static final String POPULARITY = "popularity";
    public static final String RATING = "rating";

    public abstract long id();

    @SerializedName("original_title")
    public abstract String originalTitle();

    @Nullable
    @SerializedName("poster_path")
    abstract String posterPath(); // sometimes is null!

    // http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    public String posterUrl() {
        return "http://image.tmdb.org/t/p/w185" + posterPath();
    }

    public abstract float popularity();

    @SerializedName("vote_average")
    public abstract float rating();

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
                    float popularity = cursor.getFloat(cursor.getColumnIndexOrThrow(Movie.POPULARITY));
                    float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(Movie.RATING));
                    Movie movie = new AutoValue_Movie(id, title, posterPath, popularity, rating);
                    movies.add(movie);
                }
                return movies;
            } finally {
                cursor.close();
            }
        }
    };

    public static ContentValues buildContentValues(Movie movie) {
        return buildContentValues(movie.id(), movie.originalTitle(), movie.posterPath(), movie.popularity(), movie.rating());
    }

    public static ContentValues buildContentValues(long id, String originalTitle, String posterPath, float popularity, float rating) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(ORIGINAL_TITLE, originalTitle);
        contentValues.put(POSTER_PATH, posterPath);
        contentValues.put(POPULARITY, popularity);
        contentValues.put(RATING, rating);
        return contentValues;
    }

}
