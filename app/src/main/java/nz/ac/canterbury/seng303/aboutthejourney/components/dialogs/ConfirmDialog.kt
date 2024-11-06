package nz.ac.canterbury.seng303.aboutthejourney.components.dialogs

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A dialog that asks the user to confirm an action.
 * @param title             The title of the dialog.
 * @param message           The message of the dialog.
 * @param onConfirm         The action to take when the user confirms.
 * @param onCancel          The action to take when the user cancels.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    CustomDialog(
        title = title,
        content = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        onConfirm = { onConfirm(); onCancel() },
        onCancel = onCancel,
        confirmButtonText = context.getString(R.string.confirm)
    )
}
