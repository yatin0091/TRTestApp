package com.webguru.trtest.data.network.di

import com.squareup.moshi.Moshi
import com.webguru.trtest.data.local.AppDatabase
import com.webguru.trtest.data.local.dao.PhotoDao
import com.webguru.trtest.data.local.dao.PhotoRemoteKeyDao
import com.webguru.trtest.data.network.datasource.PhotosPagingSource
import com.webguru.trtest.data.network.datasource.PhotosRemoteMediator
import com.webguru.trtest.data.network.retrofit.UnsplashNetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ApiBaseUrl

    @Provides
    @Singleton
    @ApiBaseUrl
    fun provideBaseUrl(): String = "https://api.unsplash.com/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
//            else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        // No KotlinJsonAdapterFactory when using KSP codegen
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApiBaseUrl baseUrl: String,
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashNetworkApi =
        retrofit.create(UnsplashNetworkApi::class.java)

    @Provides
    @Singleton
    fun providePhotoPagingSource(unsplashNetworkApi: UnsplashNetworkApi): PhotosPagingSource = PhotosPagingSource(unsplashNetworkApi)

    @Provides
    @Singleton
    fun providePhotoRemoteMediatorPagingSource(
        unsplashNetworkApi: UnsplashNetworkApi,
        appDatabase: AppDatabase,
        photoDao: PhotoDao,
        keyDao: PhotoRemoteKeyDao
    ): PhotosRemoteMediator =
        PhotosRemoteMediator(unsplashNetworkApi, appDatabase, photoDao, keyDao)

}