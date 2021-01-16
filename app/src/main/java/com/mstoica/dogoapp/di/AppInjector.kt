package com.mstoica.dogoapp.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.mstoica.dogoapp.BaseApplication
import dagger.android.support.AndroidSupportInjection

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 */
object AppInjector {

    fun init(baseApplication: BaseApplication) {

        baseApplication.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    handleActivity(activity)
                }

                override fun onActivityStarted(activity: Activity) {
                    // do nothing
                }

                override fun onActivityResumed(activity: Activity) {
                    // do nothing
                }

                override fun onActivityPaused(activity: Activity) {
                    // do nothing
                }

                override fun onActivityStopped(activity: Activity) {
                    // do nothing
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
                    // do nothing
                }

                override fun onActivityDestroyed(activity: Activity) {
                    // do nothing
                }
            })
    }

    private fun handleActivity(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fragmentManage: FragmentManager,
                        fragment: Fragment,
                        savedInstanceState: Bundle?
                    ) {
                        if (fragment is Injectable) {
                            AndroidSupportInjection.inject(fragment)
                        }
                    }
                }, true
            )
        }
    }
}