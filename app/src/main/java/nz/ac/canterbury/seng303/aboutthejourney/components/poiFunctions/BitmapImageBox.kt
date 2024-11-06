package nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * Displays the image if it's selected or the "+" button to add an image.
 *
 * THIS VERSION IS BUGGY, IT IS BETTER TO USE COIL FOR THIS, TRY THE CoilImageBox.kt FILE
 */
@Composable
fun BitmapImageBox(imageUri: Uri?) {
    val context = LocalContext.current
    if (imageUri != null) { // show the selected image
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = stringResource(R.string.selected_image),
                modifier = Modifier.fillMaxSize()
            )
        }
    } else { // show the "+" button to add an image
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_image),
            modifier = Modifier.size(48.dp)
        )
    }
}