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
 * A layout with three sections, top, middle and bottom.
 * The top and bottom sections have a weight of 1 and the middle section has a weight of 4.
 * @param topContent            the content of the top section that takes up 1/6 of the screen
 * @param middleContent         the content of the middle section that takes up 2/3 of the screen
 * @param bottomContent         the content of the bottom section that takes up 1/6 of the screen
 */
@Composable
fun ThreeSectionLayout(
    topContent: @Composable () -> Unit,
    middleContent: @Composable () -> Unit,
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
                .weight(1f)
        ) {
            topContent()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
        ) {
            middleContent()
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