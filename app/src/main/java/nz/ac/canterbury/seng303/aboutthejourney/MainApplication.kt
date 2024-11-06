package nz.ac.canterbury.seng303.aboutthejourney

import android.app.Application
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.aboutthejourney.datastore.dataAccessModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * The main application class for the About the Journey application.
 * Acknowledgement: This code is based on the MainApplication from the SENG 303 Lab 2 solution
 */
class MainApplication: Application() {
    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataAccessModule)
        }
    }
}