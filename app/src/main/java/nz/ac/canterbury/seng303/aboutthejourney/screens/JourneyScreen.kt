package nz.ac.canterbury.seng303.aboutthejourney.screens

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.models.PointOfInterest
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A composable function that displays the journey screen.
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param settingsViewModel The ViewModel that holds the settings data.
 * @param journeyId The ID of the journey to be displayed.
 */
@Composable
fun JourneyScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    settingsViewModel: SettingsViewModel,
    journeyId: String
) {
    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()
    val journey: Journey? = selectedJourney
    val context = LocalContext.current

    if (journey == null) {
        Toast.makeText(context,
            stringResource(R.string.error_getting_journey_returning_home), Toast.LENGTH_SHORT).show()
        navController.navigate("Home")
    }

    else {
        val pois = journey.pointsOfInterest
        val journeyDurationMode by settingsViewModel.journeyDurationMode.collectAsState()
        val durationInMillis = journey.duration(journeyDurationMode)
        val durationInMinutes = durationInMillis / 1000 / 60

        // State variables for POI selection
        var selectedPOI by remember { mutableStateOf<PointOfInterest?>(null) }
        var overlappingPOIs by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }
        var currentPOIIndex by remember { mutableStateOf(0) }

        // Camera position
        val cameraPositionState = rememberCameraPositionState {
            position = if (pois.isNotEmpty()) {
                CameraPosition.fromLatLngZoom(pois[0].location, 12f)
            } else {
                CameraPosition.fromLatLngZoom(
                    LatLng(-42.89857571692695, 171.82261852049788), 5f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            JourneyHeader(navController, durationInMinutes)
            JourneyTitle(journey.name, journey.status)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                JourneyMap(
                    pois = pois,
                    cameraPositionState = cameraPositionState,
                    onMarkerClick = { nearbyPOIs, currentIndex ->
                        overlappingPOIs = nearbyPOIs
                        currentPOIIndex = currentIndex
                        selectedPOI = nearbyPOIs[currentIndex]
                    }
                )

                if (selectedPOI != null && overlappingPOIs.isNotEmpty()) {
                    POIOverlay(
                        overlappingPOIs = overlappingPOIs,
                        currentPOIIndex = currentPOIIndex,
                        onPrevClick = {
                            currentPOIIndex = (currentPOIIndex - 1 + overlappingPOIs.size) % overlappingPOIs.size //modulo so it loops around
                            selectedPOI = overlappingPOIs[currentPOIIndex]
                        },
                        onNextClick = {
                            currentPOIIndex = (currentPOIIndex + 1) % overlappingPOIs.size //modulo so it loops around
                            selectedPOI = overlappingPOIs[currentPOIIndex]
                        },
                        onCloseClick = { selectedPOI = null },
                        onViewDetailsClick = { navController.navigate("Journey/${journeyId}/ViewPoi/${overlappingPOIs[currentPOIIndex].id}") }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                JourneyActionButtons(navController, journey.status, journey.id)
                JourneyFooterButtons(
                    journeyStatus = journey.status,
                    onFinishJourneyClick = {
                        journeyViewModel.finishJourneyById(journey.id, context)
                    },
                    onContinueJourneyClick = {
                        journeyViewModel.restartJourneyById(journey.id, context)
                    }
                )
            }
        }
    }
}

/**
 * A composable function that displays the header of the journey screen.
 *
 * @param navController The NavController used for navigation.
 * @param durationInMinutes The duration of the journey in minutes.
 */
@Composable
fun JourneyHeader(navController: NavController, durationInMinutes: Long) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { navController.navigate("Home") },
            modifier = Modifier.size(width = 50.dp, height = 60.dp)
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = stringResource(R.string.home),
                modifier = Modifier.size(width = 40.dp, height = 50.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.duration_minutes, durationInMinutes),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

/**
 * A composable function that displays the title of the journey.
 *
 * @param journeyName The name of the journey.
 * @param journeyStatus The status of the journey.
 */
@Composable
fun JourneyTitle(journeyName: String, journeyStatus: JourneyStatus) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = journeyName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        if (journeyStatus == JourneyStatus.COMPLETED) {
            Text(
                text = stringResource(R.string.completed),
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/**
 * A composable function that displays the map with points of interest (POIs) for the journey.
 *
 * @param pois The list of points of interest.
 * @param cameraPositionState The state of the camera position.
 * @param onMarkerClick The callback function to be invoked when a marker is clicked.
 */
@Composable
fun JourneyMap(
    pois: List<PointOfInterest>,
    cameraPositionState: CameraPositionState,
    onMarkerClick: (List<PointOfInterest>, Int) -> Unit
) {
    val polyLinePoints = mutableListOf<LatLng>()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        pois.forEach { poi ->
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val dateString = dateFormat.format(Date(poi.createdAt))

            Marker(
                state = rememberMarkerState(position = poi.location),
                title = if (poi.description.isEmpty()) stringResource(R.string.unnamed_poi) else poi.description,
                snippet = stringResource(R.string.created_at, dateString),
                tag = poi,
                onClick = {
                    // Find all POIs at or near this location
                    val nearbyPOIs = pois.filter { otherPOI ->
                        isLocationClose(poi.location, otherPOI.location)
                    }
                    val currentIndex = nearbyPOIs.indexOf(poi)
                    onMarkerClick(nearbyPOIs, currentIndex)
                    false
                }
            )

            polyLinePoints.add(poi.location)
        }

        Polyline(
            points = polyLinePoints,
            color = Color.Blue,
            width = 8f
        )
    }
}

/**
 * A composable function that displays an overlay with details of the selected point of interest (POI).
 *
 * @param overlappingPOIs The list of overlapping points of interest.
 * @param currentPOIIndex The index of the currently selected POI.
 * @param onPrevClick The callback function to be invoked when the previous button is clicked.
 * @param onNextClick The callback function to be invoked when the next button is clicked.
 * @param onCloseClick The callback function to be invoked when the close button is clicked.
 * @param onViewDetailsClick The callback function to be invoked when the view details button is clicked.
 */
@Composable
fun POIOverlay(
    overlappingPOIs: List<PointOfInterest>,
    currentPOIIndex: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onCloseClick: () -> Unit,
    onViewDetailsClick: () -> Unit
) {
    val poi = overlappingPOIs[currentPOIIndex]
    val context = LocalContext.current

    Box( // a background box to cover map
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // semi-transparent background to highlight the overlay
            .clickable { onCloseClick() }  //close when the user clicks out
    )

    // POI details card
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = stringResource(R.string.point_of_interest_details),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )


            if (poi.photos?.isNotEmpty() == true) { // Display the image if available
                val painter = rememberAsyncImagePainter(model = poi.photos[0])
                Image(
                    painter = painter,
                    contentDescription = stringResource(R.string.poi_image),
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }

            Text(
                text = if (poi.description.isEmpty()) context.getString(R.string.unnamed_poi) else poi.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.swipe_though_points_at_the_same_location),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Navigation Arrows
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onPrevClick,
                    enabled = overlappingPOIs.size > 1
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.previous))
                }

                Text(
                    stringResource(
                        R.string.NumberOfOverlappingPoi,
                        currentPOIIndex + 1,
                        overlappingPOIs.size
                    ))

                IconButton(
                    onClick = onNextClick,
                    enabled = overlappingPOIs.size > 1
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = stringResource(R.string.next))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.width(130.dp),
                    onClick = onViewDetailsClick) {
                    Text(stringResource(R.string.view_details))
                }


                Button(
                    modifier = Modifier.width(130.dp),
                    onClick = {
                        val uri = Uri.parse("google.navigation:q=${poi.location.latitude},${poi.location.longitude}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent) // Launch Google Maps
                    }
                ) {
                    Text(stringResource(R.string.start_route))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onCloseClick) {
                Text(stringResource(R.string.close))
            }
        }
    }
}

/**
 * A composable function that displays action buttons for the journey.
 *
 * @param navController The NavController used for navigation.
 * @param journeyStatus The status of the journey.
 * @param journeyId The ID of the journey.
 */
@Composable
fun JourneyActionButtons(
    navController: NavController,
    journeyStatus: JourneyStatus,
    journeyId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { navController.navigate("Journey/${journeyId}/Share") },
            modifier = Modifier.size(width = 50.dp, height = 50.dp)
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = stringResource(R.string.share_trip),
                modifier = Modifier.size(width = 40.dp, height = 60.dp)
            )
        }

        IconButton(
            onClick = { navController.navigate("Journey/${journeyId}/ListPoi") },
            modifier = Modifier.size(width = 40.dp, height = 60.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.List,
                contentDescription = stringResource(R.string.list_view),
                modifier = Modifier.size(width = 40.dp, height = 60.dp)
            )
        }

        if (journeyStatus == JourneyStatus.ONGOING) {
            IconButton(
                onClick = { navController.navigate("Journey/${journeyId}/AddPoi") },
                modifier = Modifier.size(width = 50.dp, height = 60.dp)
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_poi),
                    modifier = Modifier.size(width = 40.dp, height = 60.dp)
                )
            }
        } else if (journeyStatus == JourneyStatus.COMPLETED || journeyStatus == JourneyStatus.IMPORTED) {
            Button(
                onClick = { navController.navigate("Journey/${journeyId}/Summary") },
                modifier = Modifier
            ) {
                Text(text = stringResource(R.string.view_summary))
            }
        }
    }
}

/**
 * A composable function that displays footer buttons for the journey.
 *
 * @param journeyStatus The status of the journey.
 * @param onFinishJourneyClick The callback function to be invoked when the finish journey button is clicked.
 * @param onContinueJourneyClick The callback function to be invoked when the continue journey button is clicked.
 */
@Composable
fun JourneyFooterButtons(
    journeyStatus: JourneyStatus,
    onFinishJourneyClick: () -> Unit,
    onContinueJourneyClick: () -> Unit
) {
    if (journeyStatus == JourneyStatus.ONGOING) {
        Button(onClick = onFinishJourneyClick) {
            Text(text = stringResource(R.string.finish_journey))
        }
    } else if (journeyStatus == JourneyStatus.COMPLETED) {
        Button(onClick = onContinueJourneyClick) {
            Text(text = stringResource(R.string.continue_journey))
        }
    }
}

/**
 * Checks if two locations are close to each other within a specified threshold.
 *
 * @param location1 The first location.
 * @param location2 The second location.
 * @param thresholdMeters The distance threshold in meters.
 * @return True if the locations are within the threshold distance, false otherwise.
 */
fun isLocationClose(location1: LatLng, location2: LatLng, thresholdMeters: Double = 100.0): Boolean {
    val results = FloatArray(1)
    Location.distanceBetween(
        location1.latitude, location1.longitude,
        location2.latitude, location2.longitude,
        results
    )
    return results[0] <= thresholdMeters
}