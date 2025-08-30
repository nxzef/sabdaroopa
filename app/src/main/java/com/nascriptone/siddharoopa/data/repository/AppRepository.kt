package com.nascriptone.siddharoopa.data.repository

import com.nascriptone.siddharoopa.data.local.dao.SabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val sabdaDao: SabdaDao
) {
    fun getAllSabda(): Flow<List<Sabda>> = sabdaDao.getAllSabda()

    suspend fun toggleFavorite(id: Int, timeStamp: Long?) = sabdaDao.toggleFavorite(id, timeStamp)

    suspend fun removeItemsFromFavorite(ids: Set<Int>) = sabdaDao.removeItemsFromFavorite(ids)

    fun findSabdaById(id: Int): Flow<Sabda?> = sabdaDao.findSabdaById(id)
}