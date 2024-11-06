package nz.ac.canterbury.seng303.aboutthejourney.helpers.importexport

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.google.gson.GsonBuilder
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import java.io.InputStream

/**
 * Imports a journey from a folder and updates the photo URIs.
 * @param folderUri The Uri of the folder containing the journey data.
 * @param context The context to use to access the content resolver.
 * @return The imported journey with updated photo URIs.
 */
fun importJourney(folderUri: Uri, context: Context): Journey? {
    val contentResolver = context.contentResolver

    // Validate the URI
    if (folderUri == null) {
        Log.e("importJourney", "Invalid folder URI")
        return null
    }

    val folder = DocumentFile.fromTreeUri(context, folderUri)
    if (folder == null || !folder.isDirectory) {
        Log.e("importJourney", "Folder not found or URI does not point to a directory")
        return null
    }

    // Find the JSON file
    val jsonFile = folder.listFiles().find { it.name?.endsWith(".json") == true }
    if (jsonFile == null || !jsonFile.isFile) {
        Log.e("importJourney", "JSON file not found in the folder")
        return null
    }

    // Find the photos directory
    val photosDir = folder.listFiles().find { it.isDirectory && it.name == "photos" }
    if (photosDir == null || !photosDir.isDirectory) {
        Log.e("importJourney", "Photos directory not found in the folder")
        return null
    }

    // Attempt to deserialize the journey
    return try {
        val journey = deserializeJourney(jsonFile, contentResolver)
        updatePhotoUris(journey, photosDir)
    } catch (e: Exception) {
        Log.e("importJourney", "Error deserializing journey: ${e.message}")
        null
    }
}

/**
 * Deserializes a journey from a JSON file.
 * @param file The JSON file to deserialize.
 * @param contentResolver The content resolver to use to read the file.
 * @return The deserialized journey.
 */
private fun deserializeJourney(file: DocumentFile, contentResolver: ContentResolver): Journey {
    val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriAdapter())
        .create()
    val inputStream: InputStream? = contentResolver.openInputStream(file.uri)
    val journeyJson = inputStream?.bufferedReader().use { it?.readText() } ?: throw IllegalArgumentException("Failed to read JSON file")
    return gson.fromJson(journeyJson, Journey::class.java)
}

/**
 * Updates the photo URIs in a journey to point to the photos in the given directory.
 * @param journey The journey to update.
 * @param photosDir The directory containing the photos.
 */
private fun updatePhotoUris(
    journey: Journey,
    photosDir: DocumentFile
): Journey {
    val updatedPointsOfInterest = journey.pointsOfInterest.map { poi ->
        val updatedPhotos = poi.photos?.map { uri ->
            val photoFile = photosDir.listFiles().find { it.name == uri.lastPathSegment }
            photoFile?.uri ?: uri
        }
        poi.copy(photos = updatedPhotos)
    }
    return journey.copy(pointsOfInterest = updatedPointsOfInterest)
}