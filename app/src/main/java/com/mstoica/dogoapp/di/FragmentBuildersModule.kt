package com.mstoica.dogoapp.di

import com.mstoica.dogoapp.FragmentFavourites
import com.mstoica.dogoapp.FragmentSearch
import com.mstoica.dogoapp.FragmentWelcomeHome
import com.mstoica.dogoapp.databinding.FragmentSearchBinding
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFavouritesFragment(): FragmentFavourites

    @ContributesAndroidInjector
    abstract fun contributeWelcomeHomeFragment(): FragmentWelcomeHome

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): FragmentSearch

}