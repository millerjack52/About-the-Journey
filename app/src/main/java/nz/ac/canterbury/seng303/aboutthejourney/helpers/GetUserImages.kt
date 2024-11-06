package nz.ac.canterbury.seng303.aboutthejourney.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.ImageBox


/**
 * Input box for an image, creates a button to call functions for Intents.
 * @param onImagePicked Callback when an image is selected.
 * @param onRemove Callback when an image is removed.
 * @param onEdit Callback when an image is edited (re-selected).
 */
@Composable
fun ImageInput(
    imageUri: Uri? = null,
    onImagePicked: (Uri) -> Unit,
    onRemove: (() -> Unit)? = null,
    onEdit: ((Uri) -> Unit)? = null
) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current as Activity

    // Set up gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                onImagePicked(uri)
                onEdit?.invoke(uri) //editing,so update the image
            }
        }
    }

    // Set up camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            capturedImageUri?.let { uri ->
                onImagePicked(uri)
                onEdit?.invoke(uri) //editing,so update the image
            }
        }
    }

    // Main Box where everything happens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(240.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .clickable {
                    if (imageUri == null) { // shows options for image selection only if there's no image selected
                        val options =
                            arrayOf(
                                context.getString(R.string.take_photo),
                                context.getString(R.string.choose_from_gallery)
                            )
                        AlertDialog
                            .Builder(context)
                            .apply {
                                setTitle(context.getString(R.string.select_image_source))
                                setItems(options) { _, which ->
                                    when (which) {
                                        0 -> openCamera(context, cameraLauncher) { uri ->
                                            capturedImageUri = uri
                                        } // Take photo
                                        1 -> openGallery(galleryLauncher) // Pick from gallery
                                    }
                                }
                                show()
                            }
                    }
                }
        ) {
            ImageBox(imageUri) // display the image or "+" button if no image is selected
        }
        if (imageUri != null) { // if an image is selected, display edit and remove buttons
            ActionButtons(
                context = context,
                onEdit = onEdit,
                onRemove = onRemove,
                cameraLauncher = cameraLauncher,
                galleryLauncher = galleryLauncher
            )
        }
    }
}




/**
 * Action buttons for editing or removing an image.
 *
 * @param context The current activity context.
 * @param onEdit Callback when an image is edited.
 * @param onRemove Callback when an image is removed.
 * @param cameraLauncher The launcher for the camera intent.
 * @param galleryLauncher The launcher for the gallery intent.
 */
@Composable
fun ActionButtons(
    context: Activity,
    onEdit: ((Uri) -> Unit)?,
    onRemove: (() -> Unit)?,
    cameraLauncher: ActivityResultLauncher<Intent>,
    galleryLauncher: ActivityResultLauncher<Intent>
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentWidth(Alignment.Start)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Edit button
        onEdit?.let {
            IconButton(onClick = {
                val options = arrayOf(context.getString(R.string.take_photo), context.getString(R.string.choose_from_gallery))
                AlertDialog.Builder(context).apply {
                    setTitle(context.getString(R.string.edit_image))
                    setItems(options) { _, which ->
                        when (which) {
                            0 -> openCamera(context, cameraLauncher) { uri -> onEdit(uri) }
                            1 -> openGallery(galleryLauncher)
                        }
                    }
                    show()
                }
            },
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_image), tint = Color.Blue)
            }
        }

        // Remove button
        onRemove?.let {
            IconButton(onClick = {
                onRemove()
            },
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_image), tint = Color.Red)
            }
        }
    }
}


/**
 * Function for using the camera through an Intent, takes photo and stores in media store.
 *
 * @param context The current activity context.
 * @param cameraLauncher The launcher for the camera intent.
 * @param onCaptureUri Callback when the image is captured.
 */
fun openCamera(context: Activity, cameraLauncher: ActivityResultLauncher<Intent>, onCaptureUri: (Uri) -> Unit) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, context.getString(R.string.new_picture))
        put(MediaStore.Images.Media.DESCRIPTION, context.getString(R.string.from_camera))
    }

    val capturedImageUri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    )

    capturedImageUri?.let { onCaptureUri(it) }

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
    }

    cameraLauncher.launch(cameraIntent)
}


/**
 * Function for using the gallery to get images through an Intent.
 *
 * @param galleryLauncher The launcher for the gallery intent.
 */
fun openGallery(galleryLauncher: ActivityResultLauncher<Intent>) {
    val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "image/*"
        addCategory(Intent.CATEGORY_OPENABLE)
        // Request persistable read permission
        flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    galleryLauncher.launch(galleryIntent)
}