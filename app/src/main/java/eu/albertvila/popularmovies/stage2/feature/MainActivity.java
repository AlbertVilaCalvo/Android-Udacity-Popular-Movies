package eu.albertvila.popularmovies.stage2.feature;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.feature.moviedetail.MovieDetailFragment;
import eu.albertvila.popularmovies.stage2.feature.movielist.MovieListFragment;

public class MainActivity extends AppCompatActivity implements FragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new MovieListFragment();
            fm.beginTransaction()
                    // The layout ID is used as a unique identifier for a fragment in the
                    // FragmentManager's list
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        // Tablet / master-detail
        if (isMasterDetail()) {
            Fragment detailFragment = fm.findFragmentById(R.id.detail_fragment_container);
            if (detailFragment == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_fragment_container, new MovieDetailFragment())
                        .commit();
            }
        }
    }

    @Override
    public boolean isMasterDetail() {
        return findViewById(R.id.detail_fragment_container) != null;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
