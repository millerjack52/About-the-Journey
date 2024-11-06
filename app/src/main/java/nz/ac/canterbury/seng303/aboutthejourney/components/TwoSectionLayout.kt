package nz.ac.canterbury.seng303.aboutthejourney.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A layout with two sections, main and bottom.
 * The main section has a weight of 5 and the bottom section has a weight of 1.
 * @param mainContent           the content of the middle section that takes up 5/6 of the screen
 * @param bottomContent         the content of the bottom section that takes up 1/6 of the screen
 */
@Composable
fun TwoSectionLayout(
    mainContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
        ) {
            mainContent()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            bottomContent()
        }
    }
}