package eu.albertvila.popularmovies.stage2.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by albertvilacalvo on 20/10/15.
 */
public interface MovieDbService {

    String SORT_BY_POPULARITY = "popularity.desc";
    String SORT_BY_RATING = "vote_average.desc";

    // Retrofit tutorial:
    // https://github.com/codepath/android_guides/wiki/Consuming-APIs-with-Retrofit

    // URL example:
    // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=...

    @GET("discover/movie")
    Call<DiscoverMoviesResponse> discoverMovies(@Query("api_key") String key, @Query("sort_by") String sort);

    // RxJava version
    @GET("discover/movie")
    Observable<DiscoverMoviesResponse> discoverMoviesRx(@Query("api_key") String key, @Query("sort_by") String sort);

}
