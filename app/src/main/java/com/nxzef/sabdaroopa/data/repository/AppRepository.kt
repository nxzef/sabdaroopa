package com.nxzef.sabdaroopa.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nxzef.sabdaroopa.data.local.dao.SabdaDao
import com.nxzef.sabdaroopa.data.model.entity.Sabda
import com.nxzef.sabdaroopa.ui.state.Filter
import com.nxzef.sabdaroopa.utils.helpers.SearchQueryHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val sabdaDao: SabdaDao
) {

    suspend fun getEntireList(): List<Sabda> = sabdaDao.getEntireList()

    fun getSabdaWithFilters(
        query: String?,
        filter: Filter
    ): Flow<PagingData<Sabda>> {
        val ftsQuery = query?.takeIf { it.isNotBlank() }?.let {
            SearchQueryHelper.prepareFtsQuery(it)
        }
        val exactMatch = query?.takeIf { it.isNotBlank() }?.let {
            SearchQueryHelper.prepareExactMatch(it)
        }
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                sabdaDao.getSabdaWithFilters(
                    query = ftsQuery,
                    exactMatch = exactMatch,
                    category = filter.category,
                    sound = filter.sound,
                    gender = filter.gender
                )
            }
        ).flow
    }

    fun getFavoriteList(): Pager<Int, Sabda> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                sabdaDao.getFavoriteList()
            }
        )
    }

    suspend fun trackVisit(sabdaId: Int) {
        sabdaDao.incrementVisitCount(sabdaId, System.currentTimeMillis())
    }

    fun getFavoriteIds(): Flow<Set<Int>> = sabdaDao.getFavoriteIds().map { it.toSet() }
    suspend fun toggleFavoriteAndGetState(id: Int, timeStamp: Long?): Int {
        sabdaDao.toggleFavoriteInternal(id, timeStamp)
        return sabdaDao.getFavoriteState(id)
    }

    suspend fun removeItemsFromFavorite(ids: Set<Int>): Int = sabdaDao.removeItemsFromFavorite(ids)

    suspend fun addItemsToFavorite(ids: Set<Int>, timeStamp: Long): Int =
        sabdaDao.addItemsToFavorite(ids, timeStamp)

    fun findSabdaById(id: Int): Flow<Sabda?> = sabdaDao.findSabdaById(id)

    suspend fun getWords(ids: Set<Int>): Set<String> = sabdaDao.getWords(ids).toSet()

    suspend fun getSabdaListByFilter(filter: Filter): List<Sabda> = sabdaDao.getSabdaListByFilter(
        category = filter.category,
        sound = filter.sound,
        gender = filter.gender
    )

    suspend fun getSabdaListByIdSet(ids: Set<Int>): List<Sabda> = sabdaDao.getSabdaListByIdSet(ids)

    fun hasAnyNonFavoriteFlow(ids: Set<Int>): Flow<Boolean> = sabdaDao.hasAnyNonFavorite(ids)
}