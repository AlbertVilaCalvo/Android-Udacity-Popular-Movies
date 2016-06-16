package eu.albertvila.popularmovies.stage2.misc.di;

import javax.inject.Singleton;

import dagger.Component;
import eu.albertvila.popularmovies.stage2.data.api.ApiModule;
import eu.albertvila.popularmovies.stage2.data.repository.db.DbModule;
import eu.albertvila.popularmovies.stage2.feature.movielist.MovieListFragment;

/**
 * Created by Albert Vila Calvo on 9/6/16.
 */

@Singleton
@Component(modules = { AppModule.class, ApiModule.class, DbModule.class })
public interface AppComponent {

    void inject(MovieListFragment target);

}
