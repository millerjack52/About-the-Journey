package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable

/**
 * A delete button that runs the onClick function when clicked.
 * @param onClick                   the function to run when the user wants to delete
 * @param contentDescription        the content description of the button
 */
@Composable
fun DeleteButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    SquareButton(
        onClick = onClick,
        contentDescription = contentDescription,
        icon = Icons.Outlined.Delete
    )
}
