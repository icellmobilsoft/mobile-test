package com.korbacsk.movies.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.gson.Gson
import com.korbacsk.movies.MainActivity
import com.korbacsk.movies.R
import com.korbacsk.movies.config.Config
import com.korbacsk.movies.model.MovieModel
import com.korbacsk.movies.util.NavigationUtil

const val KEY_MOVIE = "KEY_MOVIE"

class MovieDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = MovieDetailsFragment()
    }

    @BindView(R.id.imageViewBackdropPath)
    lateinit var imageViewBackdropPath: ImageView

    @BindView(R.id.imageViewPosterPath)
    lateinit var imageViewPosterPath: ImageView

    @BindView(R.id.textViewTitle)
    lateinit var textViewTitle: TextView

    @BindView(R.id.textViewReleaseDate)
    lateinit var textViewReleaseDate: TextView

    @BindView(R.id.textViewOverview)
    lateinit var textViewOverview: TextView

    @BindView(R.id.ratingBarRating)
    lateinit var ratingBarRating: RatingBar

    private var movieModel: MovieModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val arguments: Bundle? = arguments

        if (arguments != null) {
            val gson = Gson()
            movieModel = gson.fromJson(arguments.getString(KEY_MOVIE), MovieModel::class.java)
        }

        val view: View = inflater.inflate(R.layout.fragment_movie_details, container, false)

        ButterKnife.bind(this, view)

        initLayout()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setOnConfigurationChangedListener(::onOrientationChanged)
        (activity as MainActivity).setToolbar(
            getString(R.string.toolbar_title_movie_details),
            true
        )


    }


    override fun onResume() {
        super.onResume()

        if (movieModel == null) {
            NavigationUtil.goBack(activity as MainActivity)
        }

    }


    fun initLayout() {
        if (movieModel != null) {
            Glide.with(this)
                .load(Config.MOVIE_API_IMAGE_URL_BACKDROP + "" + movieModel?.backdropPath)
                .diskCacheStrategy(Config.GLIDE_DISK_CACHE_STRATEGY)
                .transform(CenterCrop())
                .into(imageViewBackdropPath)



            Glide.with(this)
                .load(Config.MOVIE_API_IMAGE_URL_POSTER + "" + movieModel?.posterPath)
                .diskCacheStrategy(Config.GLIDE_DISK_CACHE_STRATEGY)
                .transform(CenterCrop())
                .into(imageViewPosterPath)


            textViewTitle.text = movieModel?.title
            ratingBarRating.rating = movieModel?.rating!! / 2
            textViewReleaseDate.text = movieModel?.releaseDate
            textViewOverview.text = movieModel?.overview
        }
    }

    private fun onOrientationChanged() {
        initLayout()
    }


}
