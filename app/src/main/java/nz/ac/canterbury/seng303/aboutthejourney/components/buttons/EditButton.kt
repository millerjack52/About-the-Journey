package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable

/**
 * An edit button that runs the onClick function when clicked.
 * @param onClick                   the function to run when the user wants to edit something
 * @param contentDescription        the content description of the button
 */
@Composable
fun EditButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    SquareButton(
        onClick = onClick,
        contentDescription = contentDescription,
        icon = Icons.Default.Edit
    )
}
