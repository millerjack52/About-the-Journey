package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.runtime.Composable

/**
 * A back button that runs the onClick function when clicked.
 * @param onClick                   the function to run when the user wants to go back
 * @param contentDescription        the content description of the button
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    SquareButton(
        onClick = onClick,
        contentDescription = contentDescription,
        icon = Icons.AutoMirrored.Outlined.ArrowBack
    )
}
