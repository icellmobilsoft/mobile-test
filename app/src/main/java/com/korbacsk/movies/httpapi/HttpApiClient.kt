package com.korbacsk.movies.httpapi

import com.korbacsk.movies.config.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object HttpApiClient {

    fun getMovieClient(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Config.MOVIE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        //api = retrofit.create(Api::class.java)

        return retrofit;
    }

    /*fun getClient(): Retrofit {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()


        retrofit = Retrofit.Builder()
            .baseUrl(Config.SERVER_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()


        return retrofit
    }*/
}