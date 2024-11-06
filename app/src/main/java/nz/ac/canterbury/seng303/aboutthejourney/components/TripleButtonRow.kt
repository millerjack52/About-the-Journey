package nz.ac.canterbury.seng303.aboutthejourney.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A row of three buttons that are evenly spaced
 * @param buttonOne         the first button
 * @param buttonTwo         the second button
 * @param buttonThree       the third button
 */
@Composable
fun TripleButtonRow(
    buttonOne: @Composable () -> Unit,
    buttonTwo: @Composable () -> Unit,
    buttonThree: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            buttonOne()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            buttonTwo()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            buttonThree()
        }
    }
}