package nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A composable function to display a box with an image loaded using Coil.
 *
 * @param imageUri The URI of the image to display.
 */
@Composable
fun ImageBox(imageUri: Uri?) {
    if (imageUri != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = stringResource(R.string.selected_image),
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_image),
            modifier = Modifier.size(48.dp)
        )
    }
}