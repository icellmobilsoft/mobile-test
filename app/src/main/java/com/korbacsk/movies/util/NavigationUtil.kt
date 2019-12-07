package com.korbacsk.movies.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.korbacsk.movies.MainActivity
import com.korbacsk.movies.R
import com.korbacsk.movies.fragment.MovieDetailsFragment
import com.korbacsk.movies.fragment.MoviesFragment


object NavigationUtil {
    fun addMoviesFragment(activity: MainActivity, args: Bundle?) {
        val fragment: Fragment = MoviesFragment.newInstance()
        fragment.arguments = args
        activity.replaceFragment(fragment, R.id.frameLayoutContent)
    }

    fun addMovieDetailsFragment(activity: MainActivity, args: Bundle?) {
        val fragment: Fragment = MovieDetailsFragment.newInstance()
        fragment.arguments = args
        activity.addFragment(fragment, R.id.frameLayoutContent)
    }

    fun goBack(activity: MainActivity) {
        val fragmentManager = activity.supportFragmentManager
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack()
        }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction(fragment.javaClass.simpleName,
            { replace(frameId, fragment) })
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction(fragment.javaClass.simpleName,
            { replace(frameId, fragment) })
    }

    inline fun FragmentManager.inTransaction(
        backStackName: String?,
        func: FragmentTransaction.() -> FragmentTransaction
    ) {
        val transaction: FragmentTransaction = beginTransaction()

        if (backStackName != null) {
            transaction.addToBackStack(backStackName)
        }

        transaction.func()
        transaction.commit()
    }
}