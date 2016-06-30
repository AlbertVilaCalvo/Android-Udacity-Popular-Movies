package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.misc.App;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 25/6/16.
 */
public class MovieDetailFragment extends Fragment implements MovieDetail.View {

    @Inject MovieDetail.Presenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.movie_detail_date) TextView date;
    @BindView(R.id.movie_detail_rating) TextView rating;
    @BindView(R.id.movie_detail_plot) TextView plot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("New MovieDetailFragment created");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        unbinder = ButterKnife.bind(this, v);

        App.getComponent(getActivity()).inject(this);

        return v;
    }

    // MovieDetail.View

    @Override
    public void showMovie(Movie movie) {
        Timber.d("MovieDetailFragment showMovie() %s", movie);
//        date.setText(movie.);
        rating.setText(String.valueOf(movie.rating()));
//        plot.setText(movie.);
    }

    // Lifecycle

    @Override
    public void onResume() {
        super.onResume();

        presenter.start(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // LeakCanary
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
