package eu.albertvila.popularmovies.stage2.feature.movielist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.albertvila.popularmovies.stage2.R;
import eu.albertvila.popularmovies.stage2.data.model.Movie;

/**
 * Created by Albert Vila Calvo (vilacalvo.albert@gmail.com) on 19/1/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Movie> movies;

    public MoviesAdapter(@NonNull List<Movie> movies) {
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Movie movie;

        @BindView(R.id.grid_item_image) ImageView imageView;
        @BindView(R.id.grid_item_title) TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Movie movie) {
            this.movie = movie;
            textView.setText(movie.originalTitle());
            Glide.with(imageView.getContext()).load(movie.posterUrl()).crossFade().into(imageView);
        }
    }

}