package eu.albertvila.popularmovies.stage2.data.repository;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import eu.albertvila.popularmovies.stage2.BuildConfig;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Albert Vila Calvo on 28/5/16.
 */
public class InMemoryMovieRepository implements MovieRepository {
    private MovieDbService movieDbService;
    private String apiKey;

    public InMemoryMovieRepository(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.networkInterceptors().add(new StethoInterceptor());
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        this.movieDbService = retrofit.create(MovieDbService.class);

        this.apiKey = context.getString(R.string.movie_db_api_key);
    }

    @Override
    public Observable<List<Movie>> getMoviesRx(String sortOrder) {
        Observable<DiscoverMoviesResponse> observable = movieDbService.discoverMoviesRx(apiKey, sortOrder);

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<DiscoverMoviesResponse, List<Movie>>() {
                    @Override
                    public List<Movie> call(DiscoverMoviesResponse discoverMoviesResponse) {
                        return discoverMoviesResponse.getMovies();
                    }
                })
                .asObservable();
    }

}
