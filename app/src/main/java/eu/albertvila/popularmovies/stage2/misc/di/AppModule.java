package eu.albertvila.popularmovies.stage2.misc.di;

import android.app.Application;
import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.albertvila.popularmovies.stage2.data.api.ApiModule;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import eu.albertvila.popularmovies.stage2.data.repository.memory.MemoryMovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.data.repository.db.DbMovieRepository;
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
    public MovieList.Presenter provideMovieListPresenter(@Named("db") MovieRepository movieRepository) {
        return new MovieListPresenter(movieRepository);
    }

    // The default (initial) ShowMovieCriteria value used when the app starts
    @Provides
    public ShowMovieCriteria provideDefaultShowMovieCriteria() {
        return ShowMovieCriteria.MOST_POPULAR;
    }

    @Provides @Singleton @Named("memory")
    public MovieRepository provideMemoryMovieRepository(MovieDbService movieDbService, @Named(ApiModule.MOVIE_DB_API_KEY) String apiKey, ShowMovieCriteria defaultCriteria) {
         return new MemoryMovieRepository(movieDbService, apiKey, defaultCriteria);
    }

    @Provides @Singleton @Named("db")
    public MovieRepository provideDbMovieRepository(MovieDbService movieDbService, @Named(ApiModule.MOVIE_DB_API_KEY) String apiKey, ShowMovieCriteria defaultCriteria, BriteDatabase db) {
        return new DbMovieRepository(movieDbService, apiKey, defaultCriteria, db);
    }

}
