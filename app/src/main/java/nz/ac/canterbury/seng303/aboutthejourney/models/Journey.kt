package nz.ac.canterbury.seng303.aboutthejourney.models

import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyDurationMode
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus

/**
 * A journey that contains a list of points of interest.
 * @param id The id of the journey.
 * @param name The name of the journey.
 * @param pointsOfInterest The points of interest in the journey.
 */
data class Journey(
    val id: Int,
    val name: String,
    val pointsOfInterest: List<PointOfInterest>,
    val status: JourneyStatus,
    val createdAt: Long,
    val finishedAt: Long? = null): Identifiable
{

    companion object{
        fun getJourneys(): List<Journey> {
            return listOf(
                Journey(1, "Christchurch", PointOfInterest.getPOIs(),
                    JourneyStatus.ONGOING, System.currentTimeMillis(), System.currentTimeMillis())
                )
        }
    }

    override fun getIdentifier(): Int {
        return id
    }

    /**
     * Get the journey's points of interest in the order they were created from oldest to newest.
     * @return The points of interest in the order they were created from oldest to newest.
     */
    fun oldestToNewestPointsOfInterest(): List<PointOfInterest> {
        return pointsOfInterest.sortedBy { it.createdAt }
    }

    /**
     * Get the duration of the journey in milliseconds with the given duration setting.
     * @param durationSetting The duration setting to use.
     * @return The duration of the journey in milliseconds.
     */
    fun duration(durationSetting: JourneyDurationMode): Long {
        return when (durationSetting) {
            JourneyDurationMode.BASED_ON_POINTS -> durationBasedOnPointsOfInterest()
            JourneyDurationMode.BASED_ON_JOURNEY -> durationBasedOnJourneyStatus()
        }
    }

    /**
     * Gets the duration of the journey in milliseconds based on the points of interest.
     * @return The duration of the journey in milliseconds.
     */
    private fun durationBasedOnPointsOfInterest(): Long {
        if(pointsOfInterest.isEmpty()){
            return 0
        } else {
            return oldestToNewestPointsOfInterest().last().createdAt - oldestToNewestPointsOfInterest().first().createdAt
        }
    }

    /**
     * Gets the duration of the journey in milliseconds based on the journey status.
     * @return The duration of the journey in milliseconds.
     */
    private fun durationBasedOnJourneyStatus(): Long {
        if (status == JourneyStatus.COMPLETED) {
            return finishedAt!! - createdAt
        } else {
            return System.currentTimeMillis() - createdAt
        }
    }
}