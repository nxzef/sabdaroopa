package com.nascriptone.siddharoopa.data.repository

import com.nascriptone.siddharoopa.data.local.dao.FavoriteSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val generalSabdaDao: GeneralSabdaDao,
    private val specificSabdaDao: SpecificSabdaDao,
    private val favoriteSabdaDao: FavoriteSabdaDao
) {


    // General
    suspend fun getAllGeneralSabda(): List<Sabda> = generalSabdaDao.getAllSabda()
    suspend fun getGeneralFavoritesSabda(ids: List<Int>): List<Sabda> = generalSabdaDao.getFavoritesSabda(ids)

    // Specific
    suspend fun getAllSpecificSabda(): List<Sabda> = specificSabdaDao.getAllSabda()
    suspend fun getSpecificFavoritesSabda(ids: List<Int>): List<Sabda> = specificSabdaDao.getFavoritesSabda(ids)

    // Favorite
    suspend fun getAllFavoriteSabda(): List<FavoriteSabda> = favoriteSabdaDao.getAllSabda()

    suspend fun addFavoriteSabda(favoriteSabda: FavoriteSabda) {
        favoriteSabdaDao.addFavoriteSabda(favoriteSabda)
    }

    suspend fun removeFavoriteSabda(id: Int, table: String) {
        favoriteSabdaDao.removeFavoriteSabda(id, table)
    }

    suspend fun isFavoriteExists(id: Int, table: String): Boolean =
        favoriteSabdaDao.isFavoriteExists(id, table)

}