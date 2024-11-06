package nz.ac.canterbury.seng303.aboutthejourney.helpers.importexport

import android.net.Uri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Serializes and deserializes URIs to and from JSON.
 * Acknowledgement: This code is based on the UriAdapter class from the following link:
 * https://gist.github.com/ypresto/3607f395ac4ef2921a8de74e9a243629
 */
class UriAdapter : JsonDeserializer<Uri?>, JsonSerializer<Uri?> {
    override fun deserialize(json: JsonElement, type: Type?, context: JsonDeserializationContext?): Uri = runCatching {
        Uri.parse(json.asString)
    }.getOrDefault(Uri.EMPTY)

    override fun serialize(src: Uri?, type: Type?, context: JsonSerializationContext?): JsonElement =
        JsonPrimitive(src.toString())
}