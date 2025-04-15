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
    suspend fun getAllGeneralSabda(): List<Sabda> = generalSabdaDao.getAllSabda()
    suspend fun getAllSpecificSabda(): List<Sabda> = specificSabdaDao.getAllSabda()
    suspend fun getAllFavoriteSabda(): List<FavoriteSabda> = favoriteSabdaDao.getAllSabda()
}