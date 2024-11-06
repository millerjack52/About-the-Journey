package nz.ac.canterbury.seng303.aboutthejourney.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A dialog that allows the user to create a new journey or edit an existing one.
 * @param title                 The title of the dialog.
 * @param name                  The name of the journey.
 * @param onNameChange          The action to take when the user changes the name of the journey.
 * @param onSaveJourney            The action to take when the user saves the journey.
 * @param closeJourneyDialog       The action to take when the user closes the dialog.
 */
@Composable
fun JourneyDialog(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    onSaveJourney: (String) -> Unit,
    closeJourneyDialog: () -> Unit
) {
    val context = LocalContext.current


    CustomDialog(
        title = title,
        content = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { onNameChange(it) },
                    label = { Text(context.getString(R.string.name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onConfirm = { onSaveJourney(name); closeJourneyDialog() },
        onCancel = closeJourneyDialog,
        confirmButtonText = context.getString(R.string.save)
    )
}