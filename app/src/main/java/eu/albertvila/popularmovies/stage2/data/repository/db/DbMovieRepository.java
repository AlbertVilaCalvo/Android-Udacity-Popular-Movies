package eu.albertvila.popularmovies.stage2.data.repository.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
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
    public Observable<List<Movie>> getMovies(final String sortOrder) {
        Observable<SqlBrite.Query> moviesQuery = db.createQuery(Movie.TABLE, "SELECT * FROM " + Movie.TABLE);
        return moviesQuery
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        fetchMovies(sortOrder);
                    }
                })
                .map(Movie.QUERY_TO_LIST_MAPPER)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void fetchMovies(String sortOrder) {
        Call<DiscoverMoviesResponse> call = movieDbService.discoverMovies(apiKey, sortOrder);
        call.enqueue(new Callback<DiscoverMoviesResponse>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResponse> call, Response<DiscoverMoviesResponse> response) {
                if (!response.isSuccessful()) {
                    // response.code() is not in range [200...3000)
                    Timber.d("fetchMovies() !response.isSuccessful()");
                    return;
                }

                List<Movie> movies = response.body().getMovies();
                Timber.d("fetchMovies() movies.size() %d", movies.size());

                if (movies.size() == 0) {
                    return;
                }

                // TODO do this in the background!
                // Save movies to db
                // We use transactions to prevent large changes to the data from spamming the subscriber
                BriteDatabase.Transaction transaction = db.newTransaction();
                try {
                    int susccessInsertCount = 0;
                    for (Movie movie : movies) {
                        // http://stackoverflow.com/questions/13311727/android-sqlite-insert-or-update
                        // https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#CONFLICT_REPLACE
                        ContentValues contentValues = Movie.buildContentValues(movie);
                        long id = db.insert(Movie.TABLE, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

                        if (id == -1) {
                            Timber.e("fetchMovies() insert() error on Movie with id %d", movie.id());
                        } else {
                            susccessInsertCount++;
                        }

                        // WE could also use db.executeAndTrigger(); with a raw query to update or insert
                        // http://stackoverflow.com/questions/4205181/insert-into-a-mysql-table-or-update-if-exists
                        // http://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace
                    }
                    Timber.i("fetchMovies() insert() success/total %d/%d", susccessInsertCount, movies.size());
                    transaction.markSuccessful();
                } finally {
                    transaction.end();
                }
            }

            @Override
            public void onFailure(Call<DiscoverMoviesResponse> call, Throwable t) {
                Timber.e(t, "fetchMovies() onFailure()");
            }
        });
    }

}
