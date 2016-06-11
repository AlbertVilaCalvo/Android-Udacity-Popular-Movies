package eu.albertvila.popularmovies.stage2.misc.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.albertvila.popularmovies.stage2.data.api.ApiModule;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.repository.MemoryMovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.feature.movielist.MovieList;
import eu.albertvila.popularmovies.stage2.feature.movielist.MovieListPresenter;

/**
 * Created by Albert Vila Calvo on 9/6/16.
 */
@Module
public class AppModule {

    private Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides @Singleton
    public Context provideContext() {
        return app;
    }

    @Provides
    public MovieList.Presenter provideMovieListPresenter(MovieRepository movieRepository) {
        return new MovieListPresenter(movieRepository);
    }

    @Provides @Singleton
    public MovieRepository provideMovieRepository(MovieDbService movieDbService, @Named(ApiModule.MOVIE_DB_API_KEY) String apiKey) {
        return new MemoryMovieRepository(movieDbService, apiKey);
    }

}
