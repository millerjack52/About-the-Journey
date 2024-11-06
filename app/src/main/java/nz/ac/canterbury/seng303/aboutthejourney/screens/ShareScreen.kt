package nz.ac.canterbury.seng303.aboutthejourney.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.helpers.importexport.exportJourney
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel


/**
 * A composable function that displays the share screen.
 *
 * @param navController The NavController used for navigation.
 * @param journeyId The ID of the journey to be shared.
 * @param journeyViewModel The ViewModel used to interact with the journey data.
 */
@Composable
fun ShareScreen(
    navController: NavController,
    journeyId: String,
    journeyViewModel: JourneyViewModel
) {
    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()
    val journey: Journey? = selectedJourney

    val context = LocalContext.current

    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                exportJourney(journey!!, uri, context)
                Toast.makeText(
                    context,
                    context.getString(R.string.journey_exported_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate("Journey/${journeyId}")
            }
        }
    }

    fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        directoryPickerLauncher.launch(intent)
    }

    if (journey == null) {
        Toast.makeText(context, context.getString(R.string.journey_not_found), Toast.LENGTH_SHORT).show()
        navController.navigate("Home")
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        openDirectoryPicker() // Lets the user choose where the journey will be exported to
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.export_journey), fontSize = 16.sp)
                }
            }
        }
    }
}