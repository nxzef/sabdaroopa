package com.nxzef.sabdaroopa.domain.quiz

import com.nxzef.sabdaroopa.data.repository.AppRepository
import com.nxzef.sabdaroopa.domain.DataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prepares quiz data for transfer between screens
 */
@Singleton
class QuizDataPreparer @Inject constructor(
    private val repository: AppRepository
) {
    suspend fun prepareDataSource(
        selectedIds: Set<Int>,
        dataSourceFactory: (Set<Int>, String) -> DataSource
    ): Result<DataSource> = runCatching {
        val words = repository.getWords(selectedIds)
        val display = words.joinToString(", ")
        dataSourceFactory(selectedIds, display)
    }
}