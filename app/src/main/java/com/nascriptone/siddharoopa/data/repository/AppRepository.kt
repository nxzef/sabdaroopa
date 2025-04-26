package com.nascriptone.siddharoopa.data.repository

import com.nascriptone.siddharoopa.data.local.dao.RestPropDao
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val generalSabdaDao: GeneralSabdaDao,
    private val specificSabdaDao: SpecificSabdaDao,
    private val restPropDao: RestPropDao
) {


    // General
    suspend fun getAllGeneralSabda(): List<Sabda> = generalSabdaDao.getAllSabda()
    suspend fun getGeneralFavoritesSabda(ids: List<Int>): List<Sabda> = generalSabdaDao.getFavoritesSabda(ids)

    // Specific
    suspend fun getAllSpecificSabda(): List<Sabda> = specificSabdaDao.getAllSabda()
    suspend fun getSpecificFavoritesSabda(ids: List<Int>): List<Sabda> = specificSabdaDao.getFavoritesSabda(ids)

    // Favorite
    fun getAllRestProp(): Flow<List<RestProp>> = restPropDao.getAllRestProp()

    suspend fun addFavoriteSabda(favoriteSabda: RestProp) {

    }

    suspend fun removeFavoriteSabda(id: Int, table: String) {

    }

}