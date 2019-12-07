package com.korbacsk.movies.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife


import com.korbacsk.movies.adapter.MoviesAdapter
import com.korbacsk.movies.config.ServiceLocator
import com.korbacsk.movies.customui.MoviesItemDecoration
import com.korbacsk.movies.model.MovieModel
import com.korbacsk.movies.viewmodel.MoviesViewModel
import com.korbacsk.movies.viewmodel.MoviesViewModelFactory
import android.content.res.Configuration
import com.korbacsk.movies.MainActivity
import com.korbacsk.movies.R


class MoviesFragment : Fragment() {

    companion object {
        fun newInstance() = MoviesFragment()
    }

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var moviesViewModelFactory: MoviesViewModelFactory

    @BindView(R.id.swipeRefreshLayoutMovies)
    lateinit  var swipeRefreshLayoutMovies: SwipeRefreshLayout

    @BindView(R.id.recyclerViewMovies)
    lateinit  var recyclerViewMovies: RecyclerView

    private lateinit var moviesAdapter: MoviesAdapter
    lateinit var moviesLayoutManager: GridLayoutManager

    private var moviesPage = 1
    var moviesObserver: Observer<List<MovieModel>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view:View= inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this, view);

        initLayout();

        moviesViewModelFactory = MoviesViewModelFactory(ServiceLocator.getMovieApi());
        moviesViewModel = ViewModelProviders.of(this, moviesViewModelFactory)
            .get(MoviesViewModel::class.java);

        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)

        (activity as MainActivity).setOnConfigurationChangedListener(::onOrientationChanged);

        moviesObserver = Observer<List<MovieModel>> { movies ->
            Log.d("TestApp", "MoviesFragment - onActivityCreated, moviesObserver -> onChanged")
            moviesAdapter.appendMovies(movies)
            setMoviesOnScrollListener()

            if (swipeRefreshLayoutMovies.isRefreshing()) {
                swipeRefreshLayoutMovies.setRefreshing(false)
            }

            if(moviesPage==1){
                moviesAdapter.setMovies(movies);
            }

            moviesPage=moviesPage+1;
        }

        moviesViewModel.getMovies().observe(this, moviesObserver!!);
        loadMovies();

    }

    fun initLayout() {
        swipeRefreshLayoutMovies.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { reset() })

        setLayoutManager();



        recyclerViewMovies.addItemDecoration(MoviesItemDecoration(10, 2))



        moviesAdapter = MoviesAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }
        recyclerViewMovies.adapter = moviesAdapter

    }

    fun setLayoutManager(){
        println("MoviesFragment - setLayoutManager");
        var spanCount:Int=2;

        val orientation:Int = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount=4;
        }

        moviesLayoutManager =  GridLayoutManager(activity,spanCount)


        recyclerViewMovies.layoutManager = moviesLayoutManager
    }

    private fun setMoviesOnScrollListener() {
        recyclerViewMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = moviesLayoutManager.itemCount
                val visibleItemCount = moviesLayoutManager.childCount
                val firstVisibleItem = moviesLayoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    recyclerViewMovies.removeOnScrollListener(this);
                    moviesPage=moviesPage+1;
                    loadMovies();
                }
            }
        })
    }

    fun loadMovies() {
        moviesViewModel.loadMovies(moviesPage,::onError)
    }

    private fun onError() {
        Toast.makeText(activity, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun onOrientationChanged() {
        println("MoviesFragment - onOrientationChanged");
        setLayoutManager();
    }



    fun reset() {
        /*moviesAdapter.clear()

        loadMovies(true)

        swipeRefreshLayoutMovies.setRefreshing(false)*/

        moviesPage=1;
        loadMovies();
    }

    private fun showMovieDetails(movie: MovieModel) {
        /*val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MOVIE_BACKDROP, movie.backdropPath)
        intent.putExtra(MOVIE_POSTER, movie.posterPath)
        intent.putExtra(MOVIE_TITLE, movie.title)
        intent.putExtra(MOVIE_RATING, movie.rating)
        intent.putExtra(MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MOVIE_OVERVIEW, movie.overview)
        startActivity(intent)*/
    }

}
