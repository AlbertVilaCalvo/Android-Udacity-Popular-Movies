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
    private @MovieType int movieType;

    public MemoryMovieRepository(MovieDbService movieDbService, String apiKey) {
        Timber.i("New MemoryMovieRepository created");
        this.movieDbService = movieDbService;
        this.apiKey = apiKey;

        // Default value
        this.movieType = TYPE_MOST_POPULAR;
    }

    @Override
    public void setMovieType(@MovieType int type) {
        this.movieType = type;
        Timber.i("MemoryMovieRepository setMovieType() to %d", type);
    }

    @Override
    public Observable<List<Movie>> observeMovies() {
        // TODO We are ignoring movieType for now -> fix
        Observable<DiscoverMoviesResponse> observable = movieDbService.discoverMoviesRx(apiKey, MovieDbService.SORT_BY_POPULARITY);

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
