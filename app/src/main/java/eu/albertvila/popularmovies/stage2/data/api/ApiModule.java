package eu.albertvila.popularmovies.stage2.data.api;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.albertvila.popularmovies.stage2.BuildConfig;
import eu.albertvila.popularmovies.stage2.R;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Albert Vila Calvo on 10/6/16.
 */

@Module
public class ApiModule {

    public static final String MOVIE_DB_API_KEY = "movie_db_api_key";

    @Provides
    @Singleton
    @Named(MOVIE_DB_API_KEY)
    public String apiKey(Context context) {
        return context.getString(R.string.movie_db_api_key);
    }

    @Provides
    @Singleton
    public MovieDbService provideMovieDbService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        builder.addInterceptor(httpLoggingInterceptor);

        if (BuildConfig.DEBUG) {
            builder.networkInterceptors().add(new StethoInterceptor());
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(MovieDbService.class);
    }

}
