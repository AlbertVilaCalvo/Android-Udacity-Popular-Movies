package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;
import eu.albertvila.popularmovies.stage2.misc.App;
import eu.albertvila.popularmovies.stage2.misc.recyclerview.AutofitGridLayoutManager;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 19/1/16.
 */
public class MovieListFragment extends Fragment implements MovieList.View, MoviesAdapter.Listener {

    @Inject MovieList.Presenter presenter;

    private List<Movie> movies = new ArrayList<>();
    private MoviesAdapter adapter;

    private Unbinder unbinder;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.progressWheel) ProgressWheel progressWheel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);

        unbinder = ButterKnife.bind(this, v);

        // ((App) getActivity().getApplication()).getAppComponent().inject(this);
        App.getComponent(getActivity()).inject(this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        adapter = new MoviesAdapter(this, movies);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new AutofitGridLayoutManager(getActivity(), R.dimen.movie_grid_min_column_width));
        recyclerView.setAdapter(adapter);

        return v;
    }

    // MoviesAdapter.Listener

    @Override
    public void onMovieClick(Movie movie) {
        presenter.movieSelected(movie);
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

    // Menu item to change the movie type (most popular, best rated or favorites)

    // We make this fragment the target fragment of ShowMovieCriteriaDialog, and we will receive the
    // selected movie criteria from ShowMovieCriteriaDialog. See onOptionsItemSelected() below
    private static final int REQUEST_SHOW_MOVIE_CRITERIA = 0;

    // Tag used to identify the DialogFragment ShowMovieCriteriaDialog in the FragmentManager
    private static final String SHOW_MOVIE_CRITERIA_DIALOG_TAG = "ShowMovieCriteriaDialogTag";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_movie_criteria) {
            presenter.menuItemShowMovieCriteriaClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMovieCriteriaDialog(ShowMovieCriteria criteria) {
        ShowMovieCriteriaDialog dialog = ShowMovieCriteriaDialog.newInstance(criteria);
        dialog.setTargetFragment(this, REQUEST_SHOW_MOVIE_CRITERIA);
        dialog.show(getFragmentManager(), SHOW_MOVIE_CRITERIA_DIALOG_TAG);
    }

    // Receive the movie type from ShowMovieCriteriaDialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SHOW_MOVIE_CRITERIA) {
            ShowMovieCriteria newCriteria = (ShowMovieCriteria) data.getSerializableExtra(ShowMovieCriteriaDialog.EXTRA_SELECTED_SHOW_MOVIE_CRITERIA);
            Timber.i("ShowMovieCriteriaDialog selected show movie criteria: %s", newCriteria);
            presenter.newShowMovieCriteriaSelected(newCriteria);
        }
    }

}
