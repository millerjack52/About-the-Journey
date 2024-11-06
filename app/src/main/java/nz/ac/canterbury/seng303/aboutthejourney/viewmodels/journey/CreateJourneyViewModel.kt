package nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * The view model for creating a journey.
 */
class CreateJourneyViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    fun clearName() {
        name = ""
    }
}