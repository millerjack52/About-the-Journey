package nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.models.PointOfInterest
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel

enum class ActionMode {
    ADD, EDIT
}

/**
 * A composable function to display action buttons for adding or editing Points of Interest (POIs).
 *
 * @param navController The NavController to handle navigation.
 * @param journeyId The ID of the journey.
 * @param id The ID of the POI.
 * @param photos The list of image URIs.
 * @param description The description text.
 * @param timestamp The timestamp of the POI.
 * @param location The location of the POI.
 * @param journeyViewModel The ViewModel to handle data operations.
 * @param mode The mode indicating whether to add or edit the POI.
 */
@Composable
fun ActionButtonsSection(
    navController: NavController,
    journeyId: Int?,
    id: Int,
    photos: List<Uri>,
    description: String,
    timestamp: Long,
    location: LatLng,
    journeyViewModel: JourneyViewModel,
    mode: ActionMode // ADD or EDIT mode
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { navController.navigateUp() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC143C),
                contentColor = Color.White
            )
        ) {
            Text(context.getString(R.string.back), fontSize = 16.sp)
        }

        Button(
            onClick = {
                if (mode == ActionMode.ADD) {
                    // Handle Add logic
                    journeyViewModel.addPointOfInterestToJourney(journeyId, PointOfInterest(id, photos, description, timestamp, location))
                    Toast.makeText(context, context.getString(R.string.poi_added), Toast.LENGTH_SHORT).show()
                } else {
                    // Handle Edit logic
                    journeyViewModel.editPointOfInterestById(journeyId, id, PointOfInterest(id, photos, description, timestamp, location))
                    Toast.makeText(context, context.getString(R.string.poi_edited), Toast.LENGTH_SHORT).show()
                }
                navController.navigate("Journey/${journeyId}")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4F7942),
                contentColor = Color.White
            )
        ) {
            val buttonText = if (mode == ActionMode.ADD) context.getString(R.string.add) else context.getString(R.string.confirm_changes)
            Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}