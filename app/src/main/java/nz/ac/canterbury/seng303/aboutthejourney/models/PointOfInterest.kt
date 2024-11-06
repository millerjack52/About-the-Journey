package nz.ac.canterbury.seng303.aboutthejourney.models

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

/**
 * A point of interest that contains a description, photos, and is tied to a location.
 * @param id The id of the point of interest.
 * @param photos The photos of the point of interest.
 * @param description The description of the point of interest.
 * @param createdAt The time the point of interest was created.
 * @param location The location of the point of interest.
 */
data class PointOfInterest(
    val id: Int,
    val photos: List<Uri>?,
    val description: String,
    val createdAt: Long,
    val location: LatLng): Identifiable
{
    companion object{
        fun getPOIs(): List<PointOfInterest> {
            return listOf(
                PointOfInterest(1,  null, "Beautiful UC", System.currentTimeMillis(), LatLng(-43.52263923023967, 172.5812638645879)),
                PointOfInterest(2, null, "My favourite liquor store", System.currentTimeMillis(), LatLng(-43.51629320229493, 172.5719393921473)),
                PointOfInterest(3, null, "Jack's House!", System.currentTimeMillis(), LatLng(-43.52736832334052, 172.58732338464878)),
                )
        }
    }

    override fun getIdentifier(): Int {
        return id
    }
}
