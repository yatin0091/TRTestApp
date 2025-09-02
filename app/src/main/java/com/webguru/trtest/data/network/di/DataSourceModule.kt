package com.webguru.trtest.data.network.di

import com.webguru.trtest.data.network.datasource.PhotoNetworkDataSource
import com.webguru.trtest.data.network.datasource.PhotoNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Binds
    @Singleton
    fun bindPhotoNetworkDataSource(photoNetworkDataSourceImpl: PhotoNetworkDataSourceImpl): PhotoNetworkDataSource
}