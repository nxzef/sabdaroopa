package com.nxzef.sabdaroopa.di

import com.nxzef.sabdaroopa.data.repository.UserPreferencesRepository
import com.nxzef.sabdaroopa.domain.manager.FocusManager
import com.nxzef.sabdaroopa.domain.manager.HapticManager
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

    fun focusManager(): FocusManager

    fun userPreferencesRepository(): UserPreferencesRepository

    fun hapticManager(): HapticManager
}
