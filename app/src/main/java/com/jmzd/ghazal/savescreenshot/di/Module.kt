package com.jmzd.ghazal.savescreenshot.di

import com.jmzd.ghazal.savescreenshot.model.Reserve
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Ghazal on 11/14/2022.
 */
@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    @Singleton
    fun provideReserve() = Reserve()
}