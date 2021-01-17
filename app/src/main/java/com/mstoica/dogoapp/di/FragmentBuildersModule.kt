package com.mstoica.dogoapp.di

import com.mstoica.dogoapp.view.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): FragmentHome

    @ContributesAndroidInjector
    abstract fun contributeWelcomeHomeFragment(): FragmentWelcomeHome

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragment(): FragmentFavourites

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): FragmentSearch

}