package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.Favorite
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import kotlinx.coroutines.flow.Flow

@Dao
interface RestPropDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoriteToRestProp(restProp: RestProp)

    @Query("SELECT * FROM rest_props")
    fun getAllRestProp(): Flow<List<RestProp>>

    @Query("DELETE FROM rest_props WHERE favorite = :favorite")
    suspend fun removeFavoriteFromRestProp(favorite: Favorite)
//
////    @Query(
////        "SELECT EXISTS(SELECT 1 FROM rest_prop WHERE favSabdaId = :id AND favSabdaCategory = :table)"
////    )
//    suspend fun isFavoriteExists(id: Int, table: String): Boolean

}