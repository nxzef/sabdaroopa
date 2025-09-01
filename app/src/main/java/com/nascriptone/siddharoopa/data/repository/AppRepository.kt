package com.nascriptone.siddharoopa.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nascriptone.siddharoopa.data.local.dao.SabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.screen.category.Filter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val sabdaDao: SabdaDao
) {
    fun getAllSabda(): Flow<List<Sabda>> = sabdaDao.getAllSabda()

    suspend fun toggleFavorite(id: Int, timeStamp: Long?) = sabdaDao.toggleFavorite(id, timeStamp)

    suspend fun removeItemsFromFavorite(ids: Set<Int>) = sabdaDao.removeItemsFromFavorite(ids)

    fun findSabdaById(id: Int): Flow<Sabda?> = sabdaDao.findSabdaById(id)

    fun getFilteredList(filter: Filter): Pager<Int, Sabda> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                sabdaDao.getFilteredList(
                    category = filter.category,
                    sound = filter.sound,
                    gender = filter.gender
                )
            }
        )
    }
}