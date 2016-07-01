package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.feature.FragmentListener;

/**
 * Created by Albert Vila Calvo on 25/6/16.
 */
public class MovieDetailActivity extends AppCompatActivity implements FragmentListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MovieDetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean isMasterDetail() {
        return false;
    }

}
