package com.nascriptone.siddharoopa.data.repository

import com.nascriptone.siddharoopa.data.local.dao.RestPropDao
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Favorite
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
    // Specific
    suspend fun getAllSpecificSabda(): List<Sabda> = specificSabdaDao.getAllSabda()
    // RestProps
    fun getAllRestProp(): Flow<List<RestProp>> = restPropDao.getAllRestProp()

    suspend fun addRestProp(restProp: RestProp) = restPropDao.addFavoriteToRestProp(restProp)

    suspend fun removeRestProp(favorite: Favorite) = restPropDao.removeFavoriteFromRestProp(favorite)

}