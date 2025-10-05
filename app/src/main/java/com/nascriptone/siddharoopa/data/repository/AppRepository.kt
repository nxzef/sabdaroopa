package com.nascriptone.siddharoopa.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nascriptone.siddharoopa.data.local.dao.SabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.state.Filter
import com.nascriptone.siddharoopa.utils.helpers.SearchQueryHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val sabdaDao: SabdaDao
) {

    suspend fun getEntireList(): List<Sabda> = sabdaDao.getEntireList()

    fun searchSabda(query: String): Flow<List<Sabda>> {
        val ftsQuery = SearchQueryHelper.prepareFtsQuery(query)
        val exactMatch = SearchQueryHelper.prepareExactMatch(query)
        return sabdaDao.searchSabda(ftsQuery, exactMatch)
    }

    fun getRecentlyVisited(limit: Int = 20): Flow<List<Sabda>> = sabdaDao.getRecentlyVisited(limit)
    fun getFavoriteList(): Pager<Int, Sabda> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                sabdaDao.getFavoriteList()
            }
        )
    }

    fun getFavoriteIds(): Flow<Set<Int>> = sabdaDao.getFavoriteIds().map { it.toSet() }
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

    suspend fun getWords(ids: Set<Int>): Set<String> = sabdaDao.getWords(ids).toSet()

    suspend fun getSabdaListByFilter(filter: Filter): List<Sabda> = sabdaDao.getSabdaListByFilter(
        category = filter.category,
        sound = filter.sound,
        gender = filter.gender
    )

    suspend fun getSabdaListByIdSet(ids: Set<Int>): List<Sabda> = sabdaDao.getSabdaListByIdSet(ids)
}