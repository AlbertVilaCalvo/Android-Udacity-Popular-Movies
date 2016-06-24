package eu.albertvila.popularmovies.stage2.data.repository.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.albertvila.popularmovies.stage2.data.model.Movie;

/**
 * Created by Albert Vila Calvo on 12/6/16.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    public static final int VERSION = 1;

    private static final String CREATE_MOVIE_TABLE = ""
            + "CREATE TABLE " +Movie.TABLE + "("
            + Movie.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + Movie.ORIGINAL_TITLE + " TEXT NOT NULL,"
            + Movie.POSTER_PATH + " TEXT NOT NULL,"
            + Movie.POPULARITY + " REAL NOT NULL"
            + ")";

//    private static final String CREATE_MOVIE_ID_INDEX =
//            "CREATE INDEX movie_id ON " + Movie.TABLE + " (" + Movie.ID + ")";

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE_TABLE);
        // TODO add index?

        // Uncomment to add some test items
        // ContentValues contentValues = Movie.buildContentValues(22, "The title", "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");
        // db.insert(Movie.TABLE, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
