package com.korbacsk.movies.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.korbacsk.movies.httpapi.MovieApiInterface
import com.korbacsk.movies.httpapi.MovieApiResponse
import com.korbacsk.movies.model.MovieModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesViewModel(finalMovieApiInterface: MovieApiInterface) : ViewModel() {
    // The final score
    var movieApiInterface = finalMovieApiInterface

    private var movies: MutableLiveData<List<MovieModel>>? = null;

    init {

    }

    fun getMovies(): MutableLiveData<List<MovieModel>> {
        if (movies == null) {
            movies = MutableLiveData<List<MovieModel>>()
        }

        return movies as MutableLiveData<List<MovieModel>>;
    }


    fun loadMovies(page: Int = 1, onError: () -> Unit) {
                   val call = movieApiInterface.getMovies(page = page);
            call.enqueue(object : Callback<MovieApiResponse> {
                override fun onResponse(call: Call<MovieApiResponse>, response: Response<MovieApiResponse>) {
                    val moviesResponse = response.body()

                    getMovies().setValue(moviesResponse?.movies);

                }

                override fun onFailure(call: Call<MovieApiResponse>, t: Throwable) {
                    call.cancel();
                    onError.invoke();
                }
            })

    }
}
