package nz.ac.canterbury.seng303.aboutthejourney.screens


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.ActionButtonsSection
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.ActionMode
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.DescriptionSection
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.ImagesSection
import nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions.LocationDetailsHeader
import nz.ac.canterbury.seng303.aboutthejourney.helpers.arePermissionsGranted
import nz.ac.canterbury.seng303.aboutthejourney.helpers.getLocation
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel


/**
 * A composable function that displays the screen to edit POI details.
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param journeyId The ID of the journey to which the POI belongs.
 * @param poiId The ID of the POI to be edited.
 * @param imageCount The number of images to be edited.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EditPoiScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    journeyId: String,
    poiId: String,
    imageCount: Int
) {
    journeyViewModel.getPointOfInterestById(poiId.toIntOrNull(), journeyId.toIntOrNull())
    val poi by journeyViewModel.selectedPointOfInterest.collectAsState()

    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()

    val photos = rememberSaveable(saver = androidx.compose.runtime.saveable.listSaver(
        save = { it.toList() },
        restore = { it.toMutableStateList() }
    )) { mutableStateListOf<Uri>() }

    var description by rememberSaveable { mutableStateOf("") }

    var location by rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }

    val maxId = (selectedJourney?.pointsOfInterest?.maxOfOrNull { it.id } ?: 0)
    var newId = maxId + 1

    val permissionsGranted = arePermissionsGranted()
    if (permissionsGranted) {
        location = getLocation()
    }


    if (poi != null) {
        LaunchedEffect(poi) {
            photos.clear()
            photos.addAll(poi!!.photos ?: emptyList())
            description = poi!!.description
        }

        location = poi!!.location
        newId = poi!!.getIdentifier()

    }


    // Scroll state for the entire area
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState) // Scrolling for page
                .clip(RoundedCornerShape(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            LocationDetailsHeader(stringResource(R.string.edit_location_details))
            Spacer(modifier = Modifier.height(40.dp))

            if (permissionsGranted) { //check permissions before allowing the addition of POI

                // Images Section
                ImagesSection(photos, imageCount, stringResource(R.string.edit_your_images_here))

                // Description Section
                Spacer(modifier = Modifier.height(25.dp))
                DescriptionSection(description) { description = it }


                // Action Buttons Section
                Spacer(modifier = Modifier.height(25.dp))
                ActionButtonsSection(
                    navController,
                    selectedJourney?.getIdentifier(),
                    newId,
                    photos,
                    description,
                    System.currentTimeMillis(),
                    location,
                    journeyViewModel,
                    ActionMode.EDIT
                )

            } else {

                Text(
                    stringResource(R.string.permission_warning),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        // Open app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                )
                {
                    Text(context.getString(R.string.open_settings), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

            }
        }
    }

}






