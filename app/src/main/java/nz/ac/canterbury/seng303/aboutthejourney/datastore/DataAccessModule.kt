package nz.ac.canterbury.seng303.aboutthejourney.datastore

import SettingsStorage
import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * A module that provides the data access layer for the application.
 * Acknowledgement: This code is based on the DataAccessModule from the SENG 303 Lab 2 solution
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "journeys_data")

@FlowPreview
val dataAccessModule = module {
    single<Storage<Journey>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<Journey>>(){}.type,
            preferenceKey = stringPreferencesKey("journeys"),
            dataStore = androidContext().dataStore
        )
    }

    single {
        SettingsStorage(androidContext().dataStore)
    }

    val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter()) // Register custom Uri adapter
        .create()

    single { gson }

    viewModel {
        JourneyViewModel(
            journeyStorage = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsStorage = get()
        )
    }
}