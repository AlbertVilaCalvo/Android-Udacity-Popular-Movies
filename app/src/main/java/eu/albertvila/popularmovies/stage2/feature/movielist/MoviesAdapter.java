package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;

/**
 * Created by Albert Vila Calvo on 19/1/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    public interface Listener {
        void onMovieClick(Movie movie);
    }

    private Listener listener;
    private List<Movie> movies;

    public MoviesAdapter(@NonNull Listener listener, @NonNull List<Movie> movies) {
        this.listener = listener;
        this.movies = movies;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Movie movie;

        @BindView(R.id.grid_item_image) ImageView image;
        @BindView(R.id.grid_item_favorite) ImageView favoriteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            this.movie = movie;
            if (movie.isFavorite()) {
                favoriteIcon.setImageResource(R.drawable.ic_star_yellow_24dp);
                favoriteIcon.setVisibility(View.VISIBLE);
            } else {
                favoriteIcon.setVisibility(View.INVISIBLE);
            }
            Glide.with(image.getContext()).load(movie.posterUrl()).crossFade().into(image);
        }

        @Override
        public void onClick(View view) {
            listener.onMovieClick(movie);
        }
    }

}