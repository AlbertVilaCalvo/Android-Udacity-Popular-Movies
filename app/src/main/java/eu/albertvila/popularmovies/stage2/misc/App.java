package eu.albertvila.popularmovies.stage2.misc;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import eu.albertvila.popularmovies.stage2.BuildConfig;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 29/5/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    // Add the line number
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });

            // TODO add Stetho

            LeakCanary.install(this);
        }
    }
}
