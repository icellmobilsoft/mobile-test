package com.korbacsk.movies

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.korbacsk.movies.util.NavigationUtil


class MainActivity : AppCompatActivity() {


    private lateinit var onConfigurationChangedListener: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            NavigationUtil.addMovieListFragment(this,null);
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (onConfigurationChangedListener != null) {
            onConfigurationChangedListener.invoke()
        }
    }

    fun setOnConfigurationChangedListener( onConfigurationChangedListener: () -> Unit) {
        this.onConfigurationChangedListener = onConfigurationChangedListener
    }



}
