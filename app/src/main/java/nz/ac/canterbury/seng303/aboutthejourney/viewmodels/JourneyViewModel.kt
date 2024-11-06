package nz.ac.canterbury.seng303.aboutthejourney.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.aboutthejourney.datastore.Storage
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus
import nz.ac.canterbury.seng303.aboutthejourney.helpers.workers.retireJourneyNotificationWorker
import nz.ac.canterbury.seng303.aboutthejourney.helpers.workers.scheduleJourneyNotificationWorker
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.models.PointOfInterest
import kotlin.random.Random

/**
 * The view model for the journey view.
 * @param journeyStorage The storage for journeys.
 */
class JourneyViewModel(private val journeyStorage: Storage<Journey>): ViewModel() {
    private val _journeys = MutableStateFlow<List<Journey>>(emptyList())
    val journeys: StateFlow<List<Journey>> get() = _journeys

    private val _selectedJourney = MutableStateFlow<Journey?>(null)
    val selectedJourney: StateFlow<Journey?> get() = _selectedJourney

    private val _selectedPointOfInterest = MutableStateFlow<PointOfInterest?>(null)
    val selectedPointOfInterest: StateFlow<PointOfInterest?> get() = _selectedPointOfInterest

    private val _pois = MutableStateFlow<List<PointOfInterest>>(emptyList())
    val pois: StateFlow<List<PointOfInterest>> get() = _pois

    /**
     * Get all journeys.
     */
    fun getJourneys() = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Getting all journeys")
        journeyStorage.getAll()
            .catch { Log.e("JOURNEY_VIEW_MODEL", it.toString()) }
            .collect { _journeys.emit(it) }
    }

    /**
     * Get a journey by its id.
     * @param journeyId The id of the journey to get.
     */
    fun getJourneyById(journeyId: Int?) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Getting journey by id")
        if (journeyId != null) {
            _selectedJourney.emit(journeyStorage.get { it.getIdentifier() == journeyId }.first())
        } else {
            _selectedJourney.emit(null)
        }
    }

    /**
     * Create a journey with the given name and an empty list of points of interest.
     * Also schedules a daily notification for the journey to remind the user to add a point of interest.
     * @param name The name of the journey.
     * @param context The context to schedule the notification in.
     */
    fun createJourney(name: String, context: Context) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Creating journey")
        val journey = Journey(
            id = Random.nextInt(0, Int.MAX_VALUE),
            name = name,
            pointsOfInterest = emptyList(),
            status = JourneyStatus.ONGOING,
            createdAt = System.currentTimeMillis()
        )

        journeyStorage.insert(journey)
            .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not insert journey") }
            .collect()
        getJourneys()

        scheduleJourneyNotificationWorker(context, journey.id, journey.name)
    }

    /**
     * Edit the name of a journey with the given id.
     * @param journeyId The id of the journey to edit.
     * @param newName The new name of the journey.
     */
    fun editJourneyNameById(journeyId: Int?, newName: String) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Editing journey name")
        if (journeyId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            journeyStorage.edit(journeyId, journey.copy(name = newName))
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not edit journey name") }
                .collect()
            getJourneyById(journeyId)
            getJourneys()
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not edit journey name: journeyId is null")
        }
    }

    /**
     * Delete a journey with the given id.
     * Also cancels the daily notification for the journey to add a point of interest.
     * @param journeyId The id of the journey to delete.
     */
    fun deleteJourneyById(journeyId: Int?, context: Context) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Deleting journey")
        if (journeyId != null) {
            journeyStorage.delete(journeyId)
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not delete journey") }
                .collect()
            getJourneys()

            retireJourneyNotificationWorker(context, journeyId)
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not delete journey: journeyId is null")
        }
    }

    /**
     * Finish a journey with the given id.
     * Also cancels the daily notification for the journey to add a point of interest.
     * @param journeyId The id of the journey to finish.
     * @param context The context to cancel the notification in.
     */
    fun finishJourneyById(journeyId: Int?, context: Context) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Finishing journey")
        if (journeyId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            journeyStorage.edit(journeyId, journey.copy(status = JourneyStatus.COMPLETED, finishedAt = System.currentTimeMillis()))
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not finish journey") }
                .collect()
            getJourneyById(journeyId)
            getJourneys()

            retireJourneyNotificationWorker(context, journeyId)
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not finish journey: journeyId is null")
        }
    }

    /**
     * Restart a journey with the given id.
     * Also schedules a daily notification for the journey to remind the user to add a point of interest.
     * @param journeyId The id of the journey to restart.
     * @param context The context to schedule the notification in.
     */
    fun restartJourneyById(journeyId: Int?, context: Context) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Restarting journey")
        if (journeyId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            journeyStorage.edit(journeyId, journey.copy(status = JourneyStatus.ONGOING, finishedAt = null))
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not restart journey") }
                .collect()
            getJourneyById(journeyId)
            getJourneys()

            scheduleJourneyNotificationWorker(context, journeyId, journey.name)
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not restart journey: journeyId is null")
        }
    }

    /**
     * Add a point of interest to a journey with the given id.
     * @param journeyId The id of the journey to add the point of interest to.
     * @param pointOfInterest The point of interest to add.
     */
    fun addPointOfInterestToJourney(journeyId: Int?, pointOfInterest: PointOfInterest) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Adding point of interest to journey")
        if (journeyId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            val pointsOfInterest = journey.pointsOfInterest.toMutableList()
            pointsOfInterest.add(pointOfInterest)
            journeyStorage.edit(journeyId, journey.copy(pointsOfInterest = pointsOfInterest))
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not add point of interest to journey") }
                .collect()
            Log.i("JOURNEY_VIEW_MODEL", pointsOfInterest.toString())
            getJourneyById(journeyId)
            Log.i("JOURNEY_VIEW_MODEL", journey.pointsOfInterest.toString())
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not add point of interest to journey: journeyId is null")
        }
    }

    /**
     * View a point of interest in a journey with the given id.
     * @param journeyId The id of the journey to edit the point of interest in.
     * @param pointOfInterestId The id of the point of interest to edit.
     */
    fun getPointOfInterestById(pointOfInterestId: Int?, journeyId: Int?) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Getting journey by id")
        val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
        if (journeyId != null && pointOfInterestId != null) {
            val pointsOfInterest = journey.pointsOfInterest.toMutableList()
            val poi = pointsOfInterest.find { it.getIdentifier() == pointOfInterestId }
            _selectedPointOfInterest.emit(poi)
        } else {
            _selectedPointOfInterest.emit(null)
        }
    }

    /**
     * Get all points of interest by journey id.
     * @param journeyId The id of the journey whose points of interest we want to fetch.
     */
    fun getAllPointOfInterestsByJourneyId(journeyId: Int?) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Getting journey point by id")
        val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
        if (journeyId != null) {
            val pointsOfInterest = journey.pointsOfInterest.toMutableList()
            _pois.emit(pointsOfInterest)
        } else {
            _selectedPointOfInterest.emit(null)
        }
    }


    /**
     * Edit a point of interest in a journey with the given id.
     * @param journeyId The id of the journey to edit the point of interest in.
     * @param pointOfInterestId The id of the point of interest to edit.
     * @param newPointOfInterest The edited point of interest.
     */
    fun editPointOfInterestById(journeyId: Int?, pointOfInterestId: Int?, newPointOfInterest: PointOfInterest) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Editing point of interest")
        if (journeyId != null && pointOfInterestId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            val pointsOfInterest = journey.pointsOfInterest.toMutableList()
            val index = pointsOfInterest.indexOfFirst { it.getIdentifier() == pointOfInterestId }
            if (index != -1) {
                pointsOfInterest[index] = newPointOfInterest
                journeyStorage.edit(journeyId, journey.copy(pointsOfInterest = pointsOfInterest))
                    .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not edit point of interest") }
                    .collect()
                getJourneyById(journeyId)
                getJourneys()
            } else {
                Log.e("JOURNEY_VIEW_MODEL", "Could not edit point of interest: pointOfInterestId not found")
            }
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not edit point of interest: journeyId or pointOfInterestId is null")
        }
    }

    /**
     * Delete a point of interest from a journey with the given id.
     * @param journeyId The id of the journey to delete the point of interest from.
     * @param pointOfInterestId The id of the point of interest to delete.
     */
    fun deletePointOfInterestById(journeyId: Int?, pointOfInterestId: Int?) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Deleting point of interest")
        if (journeyId != null && pointOfInterestId != null) {
            val journey = journeyStorage.get { it.getIdentifier() == journeyId }.first()
            val pointsOfInterest = journey.pointsOfInterest.toMutableList()
            pointsOfInterest.removeIf { it.getIdentifier() == pointOfInterestId }
            journeyStorage.edit(journeyId, journey.copy(pointsOfInterest = pointsOfInterest))
                .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not delete point of interest") }
                .collect()
            getJourneyById(journeyId)
            getJourneys()
            getAllPointOfInterestsByJourneyId(journeyId)
        } else {
            Log.e("JOURNEY_VIEW_MODEL", "Could not delete point of interest: journeyId or pointOfInterestId is null")
        }
    }

    /**
     * Import a journey.
     * @param journey The journey to import.
     */
    fun importJourney(journey: Journey) = viewModelScope.launch {
        Log.i("JOURNEY_VIEW_MODEL", "Importing journey")
        journeyStorage.insert(journey.copy(id = Random.nextInt(0, Int.MAX_VALUE), status = JourneyStatus.IMPORTED))
            .catch { Log.e("JOURNEY_VIEW_MODEL", "Could not import journey") }
            .collect()

        getJourneys()
    }
}

private fun <T> MutableStateFlow<T>.emit(value: MutableList<PointOfInterest>) {

}

