package eu.albertvila.popularmovies.stage2.misc.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by albertvilacalvo on 1/11/15.
 */
public class AspectRatioImageView extends ImageView {

    // AN ImageView THAT WILL KEEP AN ASPECT RATIO OF 1:0.675 (height:width)

    // Inspired by https://gist.github.com/unosk/7b8a1c060e121978cd76
    // and http://stackoverflow.com/a/20427215/4034572

    double aspectRatioWidth = 0.657;
    double aspectRatioHeight = 1.0;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (int) (width * aspectRatioHeight / aspectRatioWidth);
        setMeasuredDimension(width, height);
    }
}
