package nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * The view model for editing a journey. This is also used for when the user is deleting a journey.
 */
class EditJourneyViewModel: ViewModel() {
    var id by mutableStateOf(-1)
        private set

    fun updateId(newId: Int) {
        id = newId
    }

    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    fun clearModel() {
        id = -1
        name = ""
    }
}