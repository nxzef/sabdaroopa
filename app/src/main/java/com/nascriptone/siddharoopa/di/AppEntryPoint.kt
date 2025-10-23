package com.nascriptone.siddharoopa.di

import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.domain.manager.FocusManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for accessing Singleton dependencies in Composable
 * that are not tied to a ViewModel lifecycle.
 *
 * Use this when you need to access app-wide state directly in @Composable functions.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {

    /**
     * Provides the FocusManager singleton instance
     */
    fun focusManager(): FocusManager

    /**
     * Provides the UserPreferencesRepository singleton instance
     */
    fun userPreferencesRepository(): UserPreferencesRepository
}
