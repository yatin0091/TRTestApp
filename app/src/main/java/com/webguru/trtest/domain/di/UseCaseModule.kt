package com.webguru.trtest.domain.di

import com.webguru.trtest.data.SearchRepo
import com.webguru.trtest.domain.SearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @Provides
    fun getSearchUseCase(
        repo: SearchRepo,
        @DispatcherModule.IoDispatcher dispatcher: CoroutineDispatcher
    ): SearchUseCase = SearchUseCase(repo, dispatcher)
}