package eu.albertvila.popularmovies.stage2.data.repository.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.BuildConfig;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 12/6/16.
 */

@Module
public class DbModule {

    @Provides @Singleton
    SQLiteOpenHelper provideOpenHelper(Context context) {
        return new DbOpenHelper(context);
    }

    @Provides @Singleton
    SqlBrite provideSqlBrite() {
        return SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("Database").v(message);
            }
        });
    }

    // We must re-use the same BriteDatabase object. All insert, update, or delete operations must
    // go through this object in order to correctly notify subscribers
    @Provides @Singleton
    BriteDatabase provideBriteDatabase(SqlBrite sqlBrite, SQLiteOpenHelper sqLiteOpenHelper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
        if (BuildConfig.DEBUG) {
            db.setLoggingEnabled(true);
        }
        return db;
    }

}
