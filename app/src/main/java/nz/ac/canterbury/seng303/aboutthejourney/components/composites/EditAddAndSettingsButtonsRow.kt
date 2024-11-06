package nz.ac.canterbury.seng303.aboutthejourney.components.composites

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.TripleButtonRow
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.AddButton
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.EditButton
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.SettingsButton

/**
 * Buttons row for editing, adding, and navigating to the app settings.
 * @param showEditingMode       the action to take when the user wants to edit
 * @param showCreateMode        the action to take when the user wants to create a new journey
 */
@Composable
fun EditAddAndSettingsButtonsRow(
    showEditingMode: () -> Unit,
    showCreateMode: () -> Unit,
    showSettings: () -> Unit
) {
    val context = LocalContext.current

    TripleButtonRow(
        buttonOne = {
            EditButton(
                onClick = showEditingMode,
                contentDescription = context.getString(R.string.edit_button_description)
            )
        },
        buttonTwo = {
            AddButton(
                onClick = showCreateMode,
                contentDescription = context.getString(R.string.add_button_description)
            )
        },
        buttonThree = {
            SettingsButton(
                onClick = showSettings,
                contentDescription = context.getString(R.string.settings_button_description)
            )
        }
    )
}
