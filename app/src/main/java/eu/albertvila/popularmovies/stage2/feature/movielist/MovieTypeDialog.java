package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.repository.MovieRepository;

/**
 * Created by Albert Vila Calvo on 4/1/16.
 */
public class MovieTypeDialog extends DialogFragment {

    // This dialog receives current sort order as fragment arguments
    private static final String ARG_CURRENT_MOVIE_TYPE = "current_movie_type";

    // Used to pass selected position back to fragment
    public static final String EXTRA_SELECTED_POSITION = "eu.albertvila.popularmovies.stage2.selected_position";

    public static MovieTypeDialog newInstance(@MovieRepository.MovieType int currentMovieType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CURRENT_MOVIE_TYPE, currentMovieType);

        MovieTypeDialog fragment = new MovieTypeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int currentMovieType = getArguments().getInt(ARG_CURRENT_MOVIE_TYPE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_movie_type)
                .setSingleChoiceItems(R.array.movie_type_options, currentMovieType, null)
                .setNegativeButton(R.string.button_negative, null)
                .setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Send result back to fragment. Note that we must call
                        // dialog.setTargetFragment(this, REQUEST_SELECTED_POSITION); in fragment
                        if (getTargetFragment() == null) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_SELECTED_POSITION, selectedPosition);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                })
                .create();
    }
}
