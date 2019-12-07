package com.korbacsk.movies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.korbacsk.movies.R
import com.korbacsk.movies.config.Config
import com.korbacsk.movies.model.MovieModel

class MoviesAdapter(
    private var movies: MutableList<MovieModel>,
    private val onMovieClick: (movie: MovieModel) -> Unit

) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fragment_movies_item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun setMovies(movies: List<MovieModel>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    fun appendMovies(movies: List<MovieModel>) {
        this.movies.addAll(movies)
        notifyItemRangeInserted(
            this.movies.size,
            movies.size - 1
        )
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.ImageViewPoster)

        fun bind(movie: MovieModel) {
            Glide.with(itemView)
                .load(
                    Config.MOVIE_API_IMAGE_URL_POSTER + "" + movie.posterPath
                )
                .diskCacheStrategy(Config.GLIDE_DISK_CACHE_STRATEGY)
                .centerCrop()
                .into(poster)

            itemView.setOnClickListener { onMovieClick.invoke(movie) }
        }
    }
}