package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable

/**
 * An add button that runs the onClick function when clicked.
 * @param onClick                   the function to run when the user wants to add something
 * @param contentDescription        the content description of the button
 */
@Composable
fun AddButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    SquareButton(
        onClick = onClick,
        contentDescription = contentDescription,
        icon = Icons.Default.Add
    )
}
