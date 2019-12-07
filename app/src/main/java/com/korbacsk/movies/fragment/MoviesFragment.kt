package com.korbacsk.movies.fragment


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.korbacsk.movies.MainActivity
import com.korbacsk.movies.R
import com.korbacsk.movies.adapter.MoviesAdapter
import com.korbacsk.movies.config.ServiceLocator
import com.korbacsk.movies.customui.MoviesItemDecoration
import com.korbacsk.movies.model.MovieModel
import com.korbacsk.movies.util.NavigationUtil
import com.korbacsk.movies.viewmodel.MoviesViewModel
import com.korbacsk.movies.viewmodel.MoviesViewModelFactory


class MoviesFragment : Fragment() {

    companion object {
        fun newInstance() = MoviesFragment()
    }

    private var moviesViewModel: MoviesViewModel? = null
    private var moviesViewModelFactory: MoviesViewModelFactory? = null

    @BindView(R.id.swipeRefreshLayoutMovies)
    lateinit  var swipeRefreshLayoutMovies: SwipeRefreshLayout

    @BindView(R.id.recyclerViewMovies)
    lateinit  var recyclerViewMovies: RecyclerView

    private var moviesAdapter: MoviesAdapter? = null
    lateinit var moviesLayoutManager: GridLayoutManager

    private var moviesPage = 1
    var moviesObserver: Observer<List<MovieModel>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_movies, container, false)

        ButterKnife.bind(this, view)

        initLayout()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).setOnConfigurationChangedListener(::onOrientationChanged)
        (activity as MainActivity).setToolbar(getString(R.string.toolbar_title_movies), false)
        if (moviesViewModel == null) {
            moviesViewModelFactory = MoviesViewModelFactory(ServiceLocator.getMovieApi())
            moviesViewModel = ViewModelProviders.of(this, moviesViewModelFactory)
                .get(MoviesViewModel::class.java)

            moviesObserver = Observer<List<MovieModel>> { movies ->
                moviesAdapter?.appendMovies(movies)
                setMoviesOnScrollListener()

                if (swipeRefreshLayoutMovies.isRefreshing()) {
                    swipeRefreshLayoutMovies.setRefreshing(false)
                }

                if (moviesPage == 1) {
                    moviesAdapter?.setMovies(movies)
                }

                moviesPage = moviesPage + 1
            }

            moviesViewModel!!.getMovies().observe(this, moviesObserver!!)
        }

    }

    override fun onResume() {
        super.onResume()

        if (moviesAdapter?.getItemCount() == 0) {
            moviesPage = 1
            loadMovies()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    fun initLayout() {
        swipeRefreshLayoutMovies.setOnRefreshListener { loadMoviesFromBegin() }

        setLayoutManager()
        recyclerViewMovies.addItemDecoration(MoviesItemDecoration(10, 2))

        if (moviesAdapter == null) {
            moviesAdapter =
                MoviesAdapter(mutableListOf()) { movie -> addMovieDetailsFragment(movie) }
        }

        recyclerViewMovies.adapter = moviesAdapter


    }

    fun setLayoutManager(){
        var spanCount = 2

        val orientation:Int = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4
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
                    recyclerViewMovies.removeOnScrollListener(this)
                    moviesPage = moviesPage + 1
                    loadMovies()
                }
            }
        })
    }

    fun loadMovies() {
        moviesViewModel?.loadMovies(moviesPage, ::onError)
    }

    private fun onError() {
        Toast.makeText(activity, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun onOrientationChanged() {
        setLayoutManager()
    }


    fun loadMoviesFromBegin() {
        moviesPage = 1
        loadMovies()
    }

    private fun addMovieDetailsFragment(movie: MovieModel) {
        val gson = Gson()
        val movieJsonString = gson.toJson(movie)
        val args = Bundle()
        args.putString(KEY_MOVIE, movieJsonString)

        NavigationUtil.addMovieDetailsFragment(activity as MainActivity, args)

    }

}
