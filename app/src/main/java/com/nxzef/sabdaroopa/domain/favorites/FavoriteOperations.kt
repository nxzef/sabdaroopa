package com.nxzef.sabdaroopa.domain.favorites

import com.nxzef.sabdaroopa.data.repository.AppRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all favorite-related operations
 * Returns Result for proper error handling
 */
@Singleton
class FavoriteOperations @Inject constructor(
    private val repository: AppRepository
) {
    suspend fun addToFavorites(ids: Set<Int>): Result<Int> = runCatching {
        repository.addItemsToFavorite(ids, System.currentTimeMillis())
    }

    suspend fun removeFromFavorites(ids: Set<Int>): Result<Int> = runCatching {
        repository.removeItemsFromFavorite(ids)
    }

    suspend fun toggleFavorite(id: Int): Result<Int> = runCatching {
        repository.toggleFavoriteAndGetState(id, System.currentTimeMillis())
    }

    fun formatSuccessMessage(count: Int, operation: FavoriteOperation): String {
        val item = if (count == 1) "item" else "items"
        return when (operation) {
            FavoriteOperation.ADD -> "$count $item added to favorites"
            FavoriteOperation.REMOVE -> "$count $item removed from favorites"
        }
    }

    fun getErrorMessage(operation: FavoriteOperation): String {
        return when (operation) {
            FavoriteOperation.ADD -> "Failed to add items to favorites"
            FavoriteOperation.REMOVE -> "Failed to remove items from favorites"
        }
    }
}

enum class FavoriteOperation {
    ADD, REMOVE
}