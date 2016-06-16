package eu.albertvila.popularmovies.stage2.data.repository.memory;

import java.util.List;

import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 28/5/16.
 */
public class MemoryMovieRepository implements MovieRepository {

    private MovieDbService movieDbService;
    private String apiKey;

    public MemoryMovieRepository(MovieDbService movieDbService, String apiKey) {
        Timber.i("New MemoryMovieRepository created");
        this.movieDbService = movieDbService;
        this.apiKey = apiKey;
    }

    @Override
    public Observable<List<Movie>> getMovies(String sortOrder) {
        Observable<DiscoverMoviesResponse> observable = movieDbService.discoverMoviesRx(apiKey, sortOrder);

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<DiscoverMoviesResponse, List<Movie>>() {
                    @Override
                    public List<Movie> call(DiscoverMoviesResponse discoverMoviesResponse) {
                        return discoverMoviesResponse.getMovies();
                    }
                });
    }

}
