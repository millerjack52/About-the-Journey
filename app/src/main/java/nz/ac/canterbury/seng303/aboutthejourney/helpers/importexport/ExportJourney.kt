package nz.ac.canterbury.seng303.aboutthejourney.helpers.importexport

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exports a journey to a zip file in the given directory. The zip file will contain a JSON file
 * with the journey's data and a photos folder with all the photos.
 * @param journey The journey to export.
 * @param directory The path to the directory to export the journey to.
 * @param context The context to use to access the content resolver.
 */
fun exportJourney(journey: Journey, directory: Uri, context: Context) {
    val uniqueName = uniqueFileName(journey)
    val contentResolver = context.contentResolver

    val pickedDir = DocumentFile.fromTreeUri(context, directory)
    val journeyDir = pickedDir?.createDirectory(uniqueName) ?: return

    // Export JSON file
    val journeyJson = serializeJourney(journey)
    val jsonFile = journeyDir.createFile("application/json", "$uniqueName.json")
    jsonFile?.uri?.let { uri ->
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(journeyJson.toByteArray())
        }
    }

    // Export photos
    val photosDir = journeyDir.createDirectory("photos")
    val photos = journey.pointsOfInterest.flatMap { it.photos!! }
    exportPhotos(photos, photosDir, context)
}

/**
 * Serializes a journey to a JSON string.
 * @param journey The journey to serialize.
 * @return The JSON string representing the journey.
 */
private fun serializeJourney(journey: Journey): String {
    val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriAdapter())
        .create()
    return gson.toJson(journey)
}

/**
 * Export the photos associated with a list of points of interest to a given directory.
 * @param photos The list of photos to export.
 * @param directory The path to the directory to export the photos to.
 * @param context The context to use to access the content resolver.
 */
private fun exportPhotos(photos: List<Uri>, directory: DocumentFile?, context: Context) {
    val contentResolver = context.contentResolver
    directory?.let { dir ->
        photos.forEach { uri ->
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val photoFile = dir.createFile("image/jpeg", uri.lastPathSegment ?: "photo.jpg")
            photoFile?.uri?.let { photoUri ->
                contentResolver.openOutputStream(photoUri)?.use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            inputStream?.close()
        }
    }
}

/**
 * Creates a unique file name for a journey based on the name and the current time.
 * @param journey The journey to create a unique file name for.
 * @return The unique file name for the journey.
 */
fun uniqueFileName(journey: Journey): String {
    val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "journey_${journey.name}_$currentTime"
}