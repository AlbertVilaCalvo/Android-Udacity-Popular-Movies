package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.misc.App;
import eu.albertvila.popularmovies.stage2.misc.recyclerview.AutofitGridLayoutManager;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */
public class MovieListFragment extends Fragment implements MovieList.View {

    @Inject MovieList.Presenter presenter;

    private List<Movie> movies = new ArrayList<>();
    private MoviesAdapter adapter;

    private Unbinder unbinder;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.progressWheel) ProgressWheel progressWheel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);

        unbinder = ButterKnife.bind(this, v);

        // ((App) getActivity().getApplication()).getAppComponent().inject(this);
        App.getComponent(getActivity()).inject(this);

        adapter = new MoviesAdapter(movies);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new AutofitGridLayoutManager(getActivity(), R.dimen.movie_grid_min_column_width));
        recyclerView.setAdapter(adapter);

        return v;
    }

    // MovieList.View

    @Override
    public void showMovies(List<Movie> movies) {
        progressWheel.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        this.movies.clear();
        this.movies.addAll(movies);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        recyclerView.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
    }

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
}
