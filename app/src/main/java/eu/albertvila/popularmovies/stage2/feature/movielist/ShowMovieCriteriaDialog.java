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
import eu.albertvila.popularmovies.stage2.data.repository.ShowMovieCriteria;

/**
 * Created by Albert Vila Calvo on 4/1/16.
 */
public class ShowMovieCriteriaDialog extends DialogFragment {

    // This dialog receives current ShowMovieCriteria as a fragment argument from the Fragment that instantiates this dialog
    private static final String ARG_CURRENT_SHOW_MOVIE_CRITERIA = "current_show_movie_criteria";

    // Used to pass selected ShowMovieCriteria back to fragment that crated the dialog
    public static final String EXTRA_SELECTED_SHOW_MOVIE_CRITERIA = "eu.albertvila.popularmovies.stage2.selected_show_movie_criteria";

    public static ShowMovieCriteriaDialog newInstance(ShowMovieCriteria currentCriteria) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CURRENT_SHOW_MOVIE_CRITERIA, currentCriteria);

        ShowMovieCriteriaDialog fragment = new ShowMovieCriteriaDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ShowMovieCriteria currentCriteria = (ShowMovieCriteria) getArguments().getSerializable(ARG_CURRENT_SHOW_MOVIE_CRITERIA);
        int checkedItem = 0; // ShowMovieCriteria.MOST_POPULAR
        if (currentCriteria.equals(ShowMovieCriteria.BEST_RATED)) {
            checkedItem = 1;
        } else if (currentCriteria.equals(ShowMovieCriteria.FAVORITES)) {
            checkedItem = 2;
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_movie_type)
                .setSingleChoiceItems(R.array.movie_type_options, checkedItem, null)
                .setNegativeButton(R.string.button_negative, null)
                .setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Send result back to fragment. Note that we must call
                        // dialog.setTargetFragment(this, REQUEST_SHOW_MOVIE_CRITERIA); in fragment
                        if (getTargetFragment() == null) {
                            return;
                        }
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        ShowMovieCriteria criteria;
                        if (selectedPosition == 0) {
                            criteria = ShowMovieCriteria.MOST_POPULAR;
                        } else if (selectedPosition == 1) {
                            criteria = ShowMovieCriteria.BEST_RATED;
                        } else {
                            criteria = ShowMovieCriteria.FAVORITES;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_SELECTED_SHOW_MOVIE_CRITERIA, criteria);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                })
                .create();
    }
}
