package eu.albertvila.popularmovies.stage2.data.model;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.squareup.sqldelight.RowMapper;

import eu.albertvila.popularmovies.stage2.VideoModel;

/**
 * Created by Albert Vila Calvo on 7/7/16.
 */

@AutoValue
public abstract class Video implements VideoModel {

    public static final Factory<Video> FACTORY = new Factory<>(new Creator<Video>() {
        @Override
        public Video create(@NonNull String id, long movie_id, @NonNull String name, @NonNull String key) {
            return new AutoValue_Video(id, movie_id, name, key);
        }
    });

    public static final RowMapper<Video> MAPPER = FACTORY.for_movieMapper();

    public static TypeAdapter<Video> typeAdapter(Gson gson) {
        return new AutoValue_Video.GsonTypeAdapter(gson);
    }

}
