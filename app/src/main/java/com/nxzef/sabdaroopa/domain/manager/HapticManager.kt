@file:Suppress("DEPRECATION")

package com.nxzef.sabdaroopa.domain.manager

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.nxzef.sabdaroopa.data.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    userPreferencesRepository: UserPreferencesRepository
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val isVibrationEnabled: StateFlow<Boolean> = userPreferencesRepository
        .userPreferencesFlow
        .map { it.isVibrationEnabled }
        .stateIn(scope, SharingStarted.Eagerly, true)

    fun submitAnswer() {
        if (!isVibrationEnabled.value) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            vibrator.vibrate(40)
        }
    }

    fun selectionMode() {
        if (!isVibrationEnabled.value) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 20, 20, 40),
                    intArrayOf(0, 120, 0, 180),
                    -1
                )
            )
        } else {
            vibrator.vibrate(longArrayOf(0, 20, 20, 40), -1)
        }
    }
}