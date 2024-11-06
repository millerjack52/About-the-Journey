package nz.ac.canterbury.seng303.aboutthejourney.datastore

import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * A custom `TypeAdapter` for serializing and deserializing `Uri` objects.
 */
class UriTypeAdapter : TypeAdapter<Uri>() {
    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value?.toString()) // Serialize Uri as a String
    }

    override fun read(input: JsonReader): Uri? {
        val uriString = input.nextString()
        return Uri.parse(uriString) // Deserialize String back into Uri
    }
}