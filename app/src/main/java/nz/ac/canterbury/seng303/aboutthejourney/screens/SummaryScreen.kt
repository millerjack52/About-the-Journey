import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.ScreenTitle
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.models.PointOfInterest
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel


/**
 * A composable function that displays the map with points of interest (POIs) for the journey summary.
 *
 * @param pois The list of points of interest.
 * @param cameraPositionState The state of the camera position.
 */
@Composable
fun JourneySummaryMap(
    pois: List<PointOfInterest>,
    cameraPositionState: CameraPositionState,
) {
    val context = LocalContext.current
    val polyLinePoints = mutableListOf<LatLng>()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        pois.forEach { poi ->

            Marker(
                state = rememberMarkerState(position = poi.location),
                title = if (poi.description.isEmpty()) context.getString(R.string.unnamed_poi) else poi.description,
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
 * A composable function that displays the journey summary screen.
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param settingsViewModel The ViewModel that holds the settings data.
 * @param journeyId The ID of the journey to be displayed.
 */
@Composable
fun JourneySummaryScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    settingsViewModel: SettingsViewModel,
    journeyId: String
) {
    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()
    val pois: List<PointOfInterest> = selectedJourney!!.pointsOfInterest;
    var imageCounter = 0


    LaunchedEffect(selectedJourney?.id) {
        selectedJourney?.id?.let { journeyId ->
            journeyViewModel.getAllPointOfInterestsByJourneyId(journeyId)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = if (pois.isNotEmpty()) {
            CameraPosition.fromLatLngZoom(pois[0].location, 12f)
        } else {
            CameraPosition.fromLatLngZoom(
                LatLng(-42.89857571692695, 171.82261852049788), 5f
            )
        }
    }



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            ScreenTitle(title = stringResource(R.string.journey_summary))
        }

        item{Spacer(modifier = Modifier.height(20.dp))}

        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                JourneySummaryMap(pois = pois, cameraPositionState)
            }

        }
        item {
            selectedJourney?.let { JourneyStatsCard(pois = pois, selectedJourney!!,settingsViewModel) }
        }

        itemsIndexed(pois) { index, poi ->
            POIDetailSection(poi)
            Spacer(modifier = Modifier.height(16.dp))


            if (index != pois.size - 1){
                AlternatingImageDisplay(imageCounter)
                imageCounter = (imageCounter + 1) % 2
            } else{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                }
            }
        }
    }
}


/**
 * A composable function that displays the details of a point of interest (POI).
 *
 * @param poi The point of interest to be displayed.
 */
@Composable
fun POIDetailSection(poi: PointOfInterest) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            poi.photos?.let { PhotoGrid(photos = it) }


            Text(
                text = poi.description,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * A composable function that displays a grid of photos.
 *
 * @param photos The list of photo URIs to be displayed.
 */
@Composable
fun PhotoGrid(photos: List<Uri>) {
    Column() {
        photos.chunked(2).forEach { rowPhotos ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for (photoUrl in rowPhotos) {
                    Image(
                        painter = rememberImagePainter(data = photoUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * A composable function that displays an alternating image.
 *
 * @param counter The counter to determine which image to display.
 */
@Composable
fun AlternatingImageDisplay(counter: Int) {
    val imageList = listOf(
        R.drawable.c,
        R.drawable.cf
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageList[counter]),
            contentDescription = stringResource(R.string.alternating_image),
            modifier = Modifier.size(200.dp)
        )
    }
}

/**
 * A composable function that displays the journey statistics card.
 *
 * @param pois The list of points of interest.
 * @param journey The journey to which the POIs belong.
 * @param settingsViewModel The ViewModel that holds the settings data.
 */
@Composable
fun JourneyStatsCard(pois: List<PointOfInterest>, journey: Journey, settingsViewModel: SettingsViewModel) {
    val poiCount = pois.size

    val journeyDurationMode by settingsViewModel.journeyDurationMode.collectAsState()
    val durationInMillis = journey.duration(journeyDurationMode)
    val durationInDays = (durationInMillis / 1000 / 60) / 24

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.journey_statistics),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.you_visited_points_of_interest, poiCount),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.your_journey_was_days_long, durationInDays),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}









