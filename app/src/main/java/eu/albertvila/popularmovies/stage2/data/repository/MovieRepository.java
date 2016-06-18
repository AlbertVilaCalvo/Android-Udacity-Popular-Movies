package eu.albertvila.popularmovies.stage2.data.repository;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import eu.albertvila.popularmovies.stage2.data.model.Movie;
import rx.Observable;

/**
 * Created by Albert Vila Calvo on 28/5/16.
 */
public interface MovieRepository {

    int TYPE_MOST_POPULAR = 0;
    int TYPE_MOST_RATED = 1;
    int TYPE_FAVORITES = 2;

    @IntDef({TYPE_MOST_POPULAR, TYPE_MOST_RATED, TYPE_FAVORITES})
    @Retention(RetentionPolicy.SOURCE)
    @interface MovieType {}

    void setMovieType(@MovieType int type);

    Observable<List<Movie>> observeMovies();

}
