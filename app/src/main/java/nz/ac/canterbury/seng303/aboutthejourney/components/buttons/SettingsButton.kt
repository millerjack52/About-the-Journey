package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable

/**
 * A settings button that runs the onClick function when clicked.
 * @param onClick                   the function to run when the user wants to go to the settings
 * @param contentDescription        the content description of the button
 */
@Composable
fun SettingsButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    SquareButton(
        onClick = onClick,
        contentDescription = contentDescription,
        icon = Icons.Default.Settings
    )
}
