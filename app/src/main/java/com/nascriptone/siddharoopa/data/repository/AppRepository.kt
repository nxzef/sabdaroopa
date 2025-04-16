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

    // Specific
    suspend fun getAllSpecificSabda(): List<Sabda> = specificSabdaDao.getAllSabda()

    // Favorite
    suspend fun getAllFavoriteSabda(): List<FavoriteSabda> = favoriteSabdaDao.getAllSabda()

    suspend fun addFavoriteSabda(favoriteSabda: FavoriteSabda) {
        favoriteSabdaDao.addFavoriteSabda(favoriteSabda)
    }

    suspend fun removeFavoriteSabda(id: Int, category: String) {
        favoriteSabdaDao.removeFavoriteSabda(id, category)
    }

    suspend fun isFavoriteExists(id: Int, category: String): Boolean =
        favoriteSabdaDao.isFavoriteExists(id, category)

}