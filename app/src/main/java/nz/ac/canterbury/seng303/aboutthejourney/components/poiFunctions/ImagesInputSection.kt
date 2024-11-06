package nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nz.ac.canterbury.seng303.aboutthejourney.helpers.ImageInput

/**
 * A composable function to display a section for adding and managing images.
 *
 * @param photos The list of image URIs.
 * @param imageCount The maximum number of images allowed.
 * @param headerText The header text for the section.
 */
@Composable
fun ImagesSection(photos: MutableList<Uri>, imageCount: Int, headerText: String) {
    Text(
        text = headerText,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth(Alignment.Start)
            .fillMaxWidth()
    )

    Column {
        photos.forEachIndexed { index, uri ->
            ImageInput(
                imageUri = uri,
                onImagePicked = { },
                onEdit = { newUri -> photos[index] = newUri },
                onRemove = { photos.removeAt(index) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (photos.size < imageCount) {
            ImageInput(onImagePicked = { uri -> if (photos.size < imageCount) photos.add(uri) })
        }
    }
}