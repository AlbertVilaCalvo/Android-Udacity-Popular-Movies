package eu.albertvila.popularmovies.stage2.misc;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import eu.albertvila.popularmovies.stage2.BuildConfig;
import eu.albertvila.popularmovies.stage2.misc.di.AppComponent;
import eu.albertvila.popularmovies.stage2.misc.di.AppModule;
import eu.albertvila.popularmovies.stage2.misc.di.DaggerAppComponent;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 29/5/16.
 */
public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Notice that we are not calling '.apiModule(new ApiModule())'. It works without this
        // because the ApiModule doesn't have constructor arguments.
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    // Add the line number
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });

            Timber.plant(new StethoTree());

            LeakCanary.install(this);
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
