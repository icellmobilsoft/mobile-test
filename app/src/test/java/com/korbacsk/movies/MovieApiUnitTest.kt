package com.korbacsk.movies

import com.korbacsk.movies.config.ServiceLocator
import com.korbacsk.movies.httpapi.MovieApiResponse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MovieApiUnitTest {
    companion object {
        var moviesResponse: MovieApiResponse? = null
    }


    @Test
    fun movieApiTest() {
        val page = 1
        val movieApiInterface = ServiceLocator.getMovieApi()
        val call = movieApiInterface.getMovies(page = page)


        val countDownLatch = CountDownLatch(1)
        call.enqueue(object : Callback<MovieApiResponse> {
            override fun onResponse(
                call: Call<MovieApiResponse>,
                response: Response<MovieApiResponse>
            ) {
                moviesResponse = response.body()

                countDownLatch.countDown()

            }

            override fun onFailure(call: Call<MovieApiResponse>, t: Throwable) {
                call.cancel()

                countDownLatch.countDown()
            }
        })

        countDownLatch.await()

        assertNotNull(moviesResponse)
        assertNotNull(moviesResponse?.movies)
        assertTrue(
            "movies size>0",
            moviesResponse?.movies?.size!! > 0
        )
    }
}
