package com.mstoica.dogoapp

import com.mstoica.dogoapp.di.AppInjector
import com.mstoica.dogoapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class BaseApplication: DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()
}