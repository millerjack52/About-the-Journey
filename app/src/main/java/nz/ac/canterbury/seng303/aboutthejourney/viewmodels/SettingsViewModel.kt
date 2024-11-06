package nz.ac.canterbury.seng303.aboutthejourney.viewmodels

import SettingsStorage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyDurationMode

/**
 * A ViewModel that manages the settings data.
 *
 * @param settingsStorage The storage that holds the settings data.
 */
class SettingsViewModel(private val settingsStorage: SettingsStorage) : ViewModel() {

    /**
     * A StateFlow that represents the dark mode setting.
     */
    val darkMode: StateFlow<Boolean> = settingsStorage.darkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    /**
     * A StateFlow that represents the maximum number of photos setting.
     */
    val maxPhotos: StateFlow<Int> = settingsStorage.maxPhotos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 4
    )

    /**
     * A StateFlow that represents the journey duration mode setting.
     */
    val journeyDurationMode: StateFlow<JourneyDurationMode> = settingsStorage.journeyDurationMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = JourneyDurationMode.BASED_ON_JOURNEY
    )

    /**
     * Updates the dark mode setting.
     *
     * @param enabled Whether dark mode is enabled.
     */
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsStorage.setDarkMode(enabled)
        }
    }

    /**
     * Updates the maximum number of photos setting.
     *
     * @param maxPhotos The maximum number of photos.
     */
    fun setMaxPhotos(maxPhotos: Int) {
        viewModelScope.launch {
            settingsStorage.setMaxPhotos(maxPhotos)
        }
    }

    /**
     * Updates the journey duration mode setting.
     *
     * @param mode The journey duration mode.
     */
    fun setJourneyDurationMode(mode: JourneyDurationMode) {
        viewModelScope.launch {
            settingsStorage.setJourneyDurationMode(mode)
        }

    }
}