package com.nascriptone.siddharoopa.data.repository

import com.nascriptone.siddharoopa.data.local.SabdaDao
import com.nascriptone.siddharoopa.data.model.Sabda
import javax.inject.Inject

class AppRepository @Inject constructor(private val dao: SabdaDao) {
    suspend fun getAllSabda(): List<Sabda> = dao.getAllSabda()
}