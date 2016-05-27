package eu.albertvila.popularmovies.stage2.misc.recyclerview;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by albertvilacalvo on 1/11/15.
 */
public class AutofitGridLayoutManager extends GridLayoutManager {

    // A GRID LAYOUT MANAGER THAT AUTOMATICALLY SETS THE NUMBER OF COLUMNS (SPAN COUNT) BASED ON
    // A GIVEN MINIMUM COLUMN WIDTH

    // NOTE THAT NORMALLY THE ACTUAL COLUMN WIDTH WILL BE GREATER BECAUSE WE DROP THE DECIMALS WHEN
    // CALCULATING THE SPAN COUNT.

    // Inspired by http://stackoverflow.com/a/30256880/4034572

    private int mMinColumnWidth; // This is pixels (not dp).

    /**
     * Creates AutofitGridLayoutManager
     *
     * @param context Current context, will be used to access resources.
     * @param minColumnWidth Dimension resource id for the minimum width of the column, in dp
     *                       (density independent pixels). Normally the actual column width will be
     *                       greater because we drop the decimals when calculating the span count.
     */
    public AutofitGridLayoutManager(@NonNull Context context, @DimenRes int minColumnWidth) {
        super(context, 1);
        mMinColumnWidth = context.getResources().getDimensionPixelSize(minColumnWidth);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // totalSpace is in px (not dp). For a Moto G will be 720 or 1280.
        int totalSpace;
        if (getOrientation() == VERTICAL) {
            totalSpace = getWidth() - getPaddingLeft() - getPaddingRight();
        } else {
            totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
        }

        // When casting to int we drop any decimal. So 3.7 will become 3. This means that the actual
        // column width will be greater than mMinColumnWidth.
        int spanCount = (int) (totalSpace / mMinColumnWidth);
        // Span count should be at least 1 (otherwise it crashes)
        if (spanCount < 1) {
            spanCount = 1;
        }
        setSpanCount(spanCount);

        super.onLayoutChildren(recycler, state);

//        Timber.d("totalSpace " + totalSpace);
//        Timber.d("mMinColumnWidth " + mMinColumnWidth);
//        Timber.d("spanCount " + spanCount);
    }
}
