package eu.albertvila.popularmovies.stage2.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import eu.albertvila.popularmovies.stage2.BuildConfig;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.api.DiscoverMoviesResponse;
import eu.albertvila.popularmovies.stage2.data.api.MovieDbService;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 28/5/16.
 */
public class InMemoryMovieRepository implements MovieRepository {

    private static volatile InMemoryMovieRepository instance;

    public static InMemoryMovieRepository get(@NonNull Context context) {
        InMemoryMovieRepository temp = instance;
        if (temp == null) {
            synchronized (InMemoryMovieRepository.class) {
                temp = instance;
                if (temp == null) {
                    instance = temp = new InMemoryMovieRepository(context);
                }
            }
        }
        return temp;
    }

    private Context context;
    private MovieDbService movieDbService;
    private String apiKey;

    private InMemoryMovieRepository(Context context) {
        this.context = context;

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
    public List<Movie> getMovies(String sortOrder) {
        Call<DiscoverMoviesResponse> call = movieDbService.discoverMovies(apiKey, sortOrder);

        call.enqueue(new Callback<DiscoverMoviesResponse>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResponse> call, Response<DiscoverMoviesResponse> response) {
                Timber.d("response.raw(): %s", response.raw().toString());
                if (response.isSuccessful()) {
                    DiscoverMoviesResponse discoverMoviesResponse = response.body();
                    // return discoverMoviesResponse.getMovies();
                } else {
                    // The network request failed -> show the text 'Try again'
                    // showErrorView();
                }
            }

            @Override
            public void onFailure(Call<DiscoverMoviesResponse> call, Throwable t) {
                Timber.e(t, "onFailure");
                t.printStackTrace();
                // The network request failed -> show the text 'Try again'
                // showErrorView();
            }
        });

        return null;
    }

    public Observable<List<Movie>> getMoviesRx(String sortOrder) {
        Observable<DiscoverMoviesResponse> observable = movieDbService.discoverMoviesRx(apiKey, sortOrder);

        return observable.subscribeOn(Schedulers.io())
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
