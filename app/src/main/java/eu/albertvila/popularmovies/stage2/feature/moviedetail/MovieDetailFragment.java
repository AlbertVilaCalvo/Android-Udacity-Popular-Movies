package eu.albertvila.popularmovies.stage2.feature.moviedetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;
import eu.albertvila.popularmovies.stage2.data.model.Review;
import eu.albertvila.popularmovies.stage2.data.model.Video;
import eu.albertvila.popularmovies.stage2.feature.FragmentListener;
import eu.albertvila.popularmovies.stage2.misc.App;
import timber.log.Timber;

/**
 * Created by Albert Vila Calvo on 25/6/16.
 */
public class MovieDetailFragment extends Fragment implements MovieDetail.View {

    @Inject MovieDetail.Presenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton favoriteButton;

    @BindView(R.id.movie_detail_image) ImageView image;
    @BindView(R.id.movie_detail_title) TextView title;
    @BindView(R.id.movie_detail_date) TextView date;
    @BindView(R.id.movie_detail_rating) TextView rating;
    @BindView(R.id.movie_detail_overview) TextView overview;
    @BindView(R.id.movie_detail_videos_header) TextView videosHeader;
    @BindView(R.id.movie_detail_videos_layout) LinearLayout videosLayout;
    @BindView(R.id.movie_detail_reviews_header) TextView reviewsHeader;
    @BindView(R.id.movie_detail_reviews_layout) LinearLayout reviewsLayout;

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

        // Show back arrow <- if not master detail
        FragmentListener listener = (FragmentListener) getActivity();
        if (!listener.isMasterDetail()) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        return v;
    }

    // MovieDetail.View

    @Override
    public void showMovie(Movie movie) {
        Timber.d("MovieDetailFragment showMovie() %s", movie);

        Glide.with(getActivity()).load(movie.posterUrl()).crossFade().into(image);

        title.setText(movie.originalTitle());
        date.setText(movie.releaseDate());
        rating.setText(movie.rating() + "/10");
        overview.setText(movie.overview());

        if (movie.isFavorite()) {
            favoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        favoriteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVideos(List<Video> videos) {
        if (videos.size() == 0) {
            videosHeader.setVisibility(View.GONE);
        } else {
            videosHeader.setVisibility(View.VISIBLE);
        }
        videosLayout.removeAllViews();
        for (final Video video : videos) {
            TextView text = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_video, videosLayout, false);
            text.setText(video.name());
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.key())));
                }
            });
            videosLayout.addView(text);
        }
    }

    @Override
    public void showReviews(List<Review> reviews) {
        if (reviews.size() == 0) {
            reviewsHeader.setVisibility(View.GONE);
        } else {
            reviewsHeader.setVisibility(View.VISIBLE);
        }
        reviewsLayout.removeAllViews();
        for (Review review : reviews) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_review, reviewsLayout, false);
            TextView author = (TextView) layout.findViewById(R.id.list_item_review_author);
            author.setText(review.author());
            TextView content = (TextView) layout.findViewById(R.id.list_item_review_content);
            content.setText(review.content());
            reviewsLayout.addView(layout);
        }
    }

    @OnClick(R.id.fab)
    public void favoriteButtonClick() {
        presenter.favoriteButtonClick();
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
