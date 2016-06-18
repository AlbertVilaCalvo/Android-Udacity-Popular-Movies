package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;
import eu.albertvila.popularmovies.stage2.misc.App;
import eu.albertvila.popularmovies.stage2.misc.recyclerview.AutofitGridLayoutManager;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 19/1/16.
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

        setHasOptionsMenu(true);

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

    // Menu item to change the movie type (most popular, best rated or favorites)

    // We make this fragment the target fragment of MovieTypeDialog, and we will receive the
    // selected position from MovieTypeDialog. See onOptionsItemSelected() below
    private static final int REQUEST_MOVIE_TYPE = 0;

    // MovieTypeDialog tag (used to identify the DialogFragment in the FragmentManager)
    private static final String MOVIE_TYPE_DIALOG_TAG = "MovieTypeDialogTag";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_movie_type) {
            // TODO Get the current movie type from the MovieRepository
            // Show Dialog
            MovieTypeDialog dialog = MovieTypeDialog.newInstance(MovieRepository.TYPE_MOST_POPULAR);
            dialog.setTargetFragment(this, REQUEST_MOVIE_TYPE);
            dialog.show(getFragmentManager(), MOVIE_TYPE_DIALOG_TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Receive the movie type from MovieTypeDialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_MOVIE_TYPE) {
            int selectedPosition = data.getIntExtra(MovieTypeDialog.EXTRA_SELECTED_POSITION, 0);
            Timber.d("MovieTypeDialog selected position: %d", selectedPosition);
            // TODO change the movie type
        }
    }

}
