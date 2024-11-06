package nz.ac.canterbury.seng303.aboutthejourney.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A square button that displays an icon and calls the given function when clicked.
 * @param onClick               The function to call when the button is clicked.
 * @param contentDescription    The content description of the button.
 * @param icon                  The icon to display on the button.
 */
@Composable
fun SquareButton(
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector
) {
    Icon(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        imageVector = icon,
        contentDescription = contentDescription,
    )
}