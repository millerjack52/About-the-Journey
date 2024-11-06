package nz.ac.canterbury.seng303.aboutthejourney.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A column of buttons for creating or importing a journey.
 *
 * @param onCreateJourneyClick Callback when the create journey button is clicked.
 * @param onImportJourneyClick Callback when the import journey button is clicked.
 */
@Composable
fun ButtonsColumn(
    onCreateJourneyClick: () -> Unit,
    onImportJourneyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingActionButton(
                onClick = onCreateJourneyClick,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(text = stringResource(R.string.create_journey), style = MaterialTheme.typography.bodyMedium)
                }
            }
            FloatingActionButton(
                onClick = onImportJourneyClick
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(text = stringResource(R.string.import_journey), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}