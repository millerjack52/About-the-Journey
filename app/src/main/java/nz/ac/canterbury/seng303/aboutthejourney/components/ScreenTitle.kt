package nz.ac.canterbury.seng303.aboutthejourney.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A title for a screen.
 * @param title The title of the screen.
 */
@Composable
fun ScreenTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        style = MaterialTheme.typography.headlineLarge
    )
}