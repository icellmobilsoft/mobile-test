package com.korbacsk.movies

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.korbacsk.movies.util.NavigationUtil


class MainActivity : AppCompatActivity() {


    private var onConfigurationChangedListener: (() -> Unit?)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            NavigationUtil.addMoviesFragment(this, null)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (onConfigurationChangedListener != null) {
            onConfigurationChangedListener?.invoke()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.getItemId() === android.R.id.home) {
            NavigationUtil.goBack(this)
            true
        } else super.onOptionsItemSelected(item)
    }

    fun setOnConfigurationChangedListener( onConfigurationChangedListener: () -> Unit) {
        this.onConfigurationChangedListener = onConfigurationChangedListener
    }

    fun setToolbar(title: String?, showBack: Boolean?) {
        supportActionBar?.title = title ?: getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack ?: false)
    }



}
