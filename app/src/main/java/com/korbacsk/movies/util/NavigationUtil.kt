package com.korbacsk.movies.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.korbacsk.movies.R
import com.korbacsk.movies.fragment.MoviesFragment

object NavigationUtil {
    fun addMovieListFragment(activity: AppCompatActivity, args: Bundle?) {
        var fragment: Fragment = MoviesFragment.newInstance();
        fragment.arguments=args;
        activity.replaceFragment(fragment, R.id.frameLayoutContent)
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }


    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction{replace(frameId, fragment)}
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }
}