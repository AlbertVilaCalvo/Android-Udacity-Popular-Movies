package eu.albertvila.popularmovies.stage2.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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


    // http://docs.themoviedb.apiary.io/#reference/movies/movieidvideos/get?console=1
    // Response example:
    // {"id":368596,
    // "results":[
    //      {"id":"570d876f92514124ef00028c",
    //      "iso_639_1":"en",
    //      "iso_3166_1":"US",
    //      "key":"FDz4WFjJ1zA",
    //      "name":"Video 1",
    //      "site":"YouTube",
    //      "size":1080,
    //      "type":"Video"}
    // ]}

    @GET("movie/{id}/videos")
    Call<VideosResponse> getVideosForMovie(@Path("id") long movieId, @Query("api_key") String key);

    @GET("movie/{id}/videos")
    Observable<VideosResponse> getVideosForMovieRx(@Path("id") long movieId, @Query("api_key") String key);


    // http://docs.themoviedb.apiary.io/#reference/movies/movieidsimilar/get?console=1
    // Response example:
    // {"id":368596,"page":1,"results":[],"total_pages":0,"total_results":0}
    // Response example:
    // {"id":47933,
    // "page":1,
    // "results":[
    //      {"id":"5769f7afc3a3683726001772",
    //      "author":"Screen-Space",
    //      "content":"\"Independence Day: Resurgence entertains like few Hollywood blockbusters have of late, largely by
    //          foregoing pretension on every level and drilling down on the basic tenets of popcorn moviemaking...\"\r\n\r\nRead
    //          the full review here: http://screen-space.squarespace.com/reviews/2016/6/22/independence-day-resurgence.html",
    //      "url":"https://www.themoviedb.org/review/5769f7afc3a3683726001772"}],
    // "total_pages":1,
    // "total_results":1}

    @GET("movie/{id}/reviews")
    Observable<ReviewsResponse> getReviewsForMovieRx(@Path("id") long movieId, @Query("api_key") String key);

}
