package eu.albertvila.popularmovies.stage2.data.repository.db;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 12/6/16.
 */
public class DbMovieRepository implements MovieRepository {

    private MovieDbService movieDbService;
    private String apiKey;
    private BriteDatabase db;

    public DbMovieRepository(MovieDbService movieDbService, String apiKey, BriteDatabase db) {
        Timber.i("New DbMovieRepository created");
        this.movieDbService = movieDbService;
        this.apiKey = apiKey;
        this.db = db;
    }

    @Override
    public Observable<List<Movie>> getMovies(String sortOrder) {
        Observable<SqlBrite.Query> moviesQuery = db.createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE);
        return moviesQuery.map(Movie.QUERY_TO_LIST_MAPPER).observeOn(AndroidSchedulers.mainThread());
    }
}
