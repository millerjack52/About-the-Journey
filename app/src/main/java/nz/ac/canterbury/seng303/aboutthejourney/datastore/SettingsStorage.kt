import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyDurationMode

/**
 * A storage class for managing settings using DataStore.
 *
 * @param dataStore The DataStore instance for storing preferences.
 */
class SettingsStorage(private val dataStore: DataStore<Preferences>) {

    // Keys for the preferences
    companion object {
        /**
         * Key for the dark mode setting.
         */
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        /**
         * Key for the maximum number of photos per POI setting.
         */
        val MAX_PHOTOS_KEY = intPreferencesKey("max_photos_per_poi")
        /**
         * Key for the journey duration mode setting.
         */
        val DURATION_MODE_KEY  = stringPreferencesKey("duration_mode")
    }

    /**
     * Flow representing the dark mode setting.
     */
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false // Default to light mode (false)
    }

    /**
     * Flow representing the maximum number of photos per POI setting.
     */
    val maxPhotos: Flow<Int> = dataStore.data.map { preferences ->
        preferences[MAX_PHOTOS_KEY] ?: 4 // Default to 5 photos
    }

    /**
     * Flow representing the journey duration mode setting.
     */
    val journeyDurationMode: Flow<JourneyDurationMode> = dataStore.data.map { preferences ->
        val modeString = preferences[DURATION_MODE_KEY] ?: JourneyDurationMode.BASED_ON_JOURNEY.name // Default to BASED_ON_JOURNEY
        JourneyDurationMode.valueOf(modeString) // Convert stored String back to Enum
    }

    /**
     * Sets the dark mode setting.
     *
     * @param enabled Whether dark mode is enabled.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Sets the maximum number of photos per POI setting.
     *
     * @param maxPhotos The maximum number of photos.
     */
    suspend fun setMaxPhotos(maxPhotos: Int) {
        dataStore.edit { preferences ->
            preferences[MAX_PHOTOS_KEY] = maxPhotos
        }
    }

    /**
     * Sets the journey duration mode setting.
     *
     * @param mode The journey duration mode.
     */
    suspend fun setJourneyDurationMode(mode: JourneyDurationMode) {
        dataStore.edit { preferences ->
            preferences[DURATION_MODE_KEY] = mode.name // Store the enum as a String
        }
    }
}