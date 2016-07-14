package eu.albertvila.popularmovies.stage2.data.model;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

import eu.albertvila.popularmovies.stage2.ReviewModel;


/**
 * Created by Albert Vila Calvo on 14/7/16.
 */

@AutoValue
public abstract class Review implements ReviewModel {

    public static final Factory<Review> FACTORY = new Factory<>(new Creator<Review>() {
        @Override
        public Review create(@NonNull String id, long movie_id, @NonNull String author, @NonNull String content, @NonNull String url) {
            return new AutoValue_Review(id, movie_id, author, content, url);
        }
    });

    public static final RowMapper<Review> MAPPER = FACTORY.for_movieMapper();

    public static TypeAdapter<Review> typeAdapter(Gson gson) {
        return new AutoValue_Review.GsonTypeAdapter(gson);
    }

}
