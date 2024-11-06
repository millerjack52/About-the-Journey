package nz.ac.canterbury.seng303.aboutthejourney.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.maps.model.LatLng
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.ThreeSectionLayout
import nz.ac.canterbury.seng303.aboutthejourney.components.TripleButtonRow
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.BackButton
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.DeleteButton
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.EditButton
import nz.ac.canterbury.seng303.aboutthejourney.components.dialogs.CustomDialog
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A composable function that displays the screen for viewing a point of interest (POI).
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param journeyId The ID of the journey to which the POI belongs.
 * @param poiId The ID of the point of interest to be displayed.
 */
@Composable
fun ViewPoiScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    journeyId: String,
    poiId: String
) {
    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()

    journeyViewModel.getPointOfInterestById(poiId.toIntOrNull(), journeyId.toIntOrNull())
    val poi by journeyViewModel.selectedPointOfInterest.collectAsState()


    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val deleteDialog = rememberSaveable { mutableStateOf(false) }
    val openConfirmDelete = { deleteDialog.value = true }
    val closeConfirmDelete = { deleteDialog.value = false }

    if (poi != null) {
        ThreeSectionLayout(
            topContent = {DisplayPoiDescription(if (poi!!.description.isEmpty()) context.getString(R.string.unnamed_poi) else poi!!.description)},

            middleContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState) // Scrolling for page
                        .clip(RoundedCornerShape(32.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (poi!!.photos?.isNotEmpty() == true) {
                        Text(
                            text = stringResource(R.string.swipe_through_the_images),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        poi!!.photos?.let { ImageSlideshow(photos = it) }
                    }
                    else {
                        Text(
                            text = stringResource(R.string.no_images_available),
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    DisplayPoiDate(poi!!.createdAt)
                    DisplayPoiLocation(poi!!.location) }},

            bottomContent = {
                selectedJourney?.let { DisplayNavButtons(navController, openConfirmDelete, it.status, journeyId, poiId) }
            }
        )

        DeleteConfirmation(navController, journeyViewModel, journeyId = selectedJourney!!.getIdentifier(), poi!!.id, deleteDialog.value, closeConfirmDelete)

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState) // Scrolling for page
                .clip(RoundedCornerShape(32.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.point_of_interest_not_found),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Back Button
                Button(onClick = { navController.navigateUp() }) {
                    Text(context.getString(R.string.back))
                }

                // Home Button
                Button(onClick = {
                    navController.navigate("Home")
                }) {
                    Text(context.getString(R.string.home))
                }
            }
        }
    }
}

/**
 * A composable function that displays the description of a point of interest (POI).
 *
 * @param description The description of the POI.
 */
@Composable
fun DisplayPoiDescription(description: String){
    if(description.isEmpty()){
        Text(
            text = stringResource(R.string.point_of_interest),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
    } else {
        Text(
            text = description,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
    }

}

/**
 * A composable function that displays a slideshow of images.
 *
 * @param photos The list of photo URIs to be displayed.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlideshow(photos: List<Uri>) {
    val pagerState = rememberPagerState()
    val context = LocalContext.current

    HorizontalPager(
        count = photos.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) { page ->
        Image(
            painter = rememberAsyncImagePainter(model = photos[page]),
            contentDescription = context.getString(R.string.poi_image),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * A composable function that displays the creation date of a point of interest (POI).
 *
 * @param createdAt The timestamp of when the POI was created.
 */
@Composable
fun DisplayPoiDate(createdAt: Long){
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(createdAt))
    val context = LocalContext.current
    Text(
        text = context.getString(R.string.created_at, dateString),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    )
}

/**
 * A composable function that displays the location of a point of interest (POI).
 *
 * @param location The LatLng object representing the location of the POI.
 */
@Composable
fun DisplayPoiLocation(location: LatLng){
    Text(
        text = stringResource(R.string.location, location.latitude, location.longitude),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    )
}

/**
 * A composable function that displays navigation buttons for the POI screen.
 *
 * @param navController The NavController used for navigation.
 * @param openDeleteDialog The callback function to open the delete confirmation dialog.
 * @param journeyStatus The status of the journey.
 * @param journeyId The ID of the journey to which the POI belongs.
 * @param poiId The ID of the point of interest.
 */
@Composable
fun DisplayNavButtons(
    navController: NavController,
    openDeleteDialog: () -> Unit,
    journeyStatus: JourneyStatus,
    journeyId: String,
    poiId: String
) {
    val context = LocalContext.current

    if (journeyStatus == JourneyStatus.ONGOING) {
        TripleButtonRow(
            { BackButton(onClick = { navController.navigateUp() }, contentDescription = "Back") },
            {
                EditButton(
                    onClick = { navController.navigate("Journey/${journeyId}/EditPoi/${poiId}") },
                    contentDescription = stringResource(R.string.edit)
                )
            },
            { DeleteButton(onClick = { openDeleteDialog() }, contentDescription = "Delete") }
        )
    } else {
        TripleButtonRow(
            buttonOne = {},
            buttonTwo = {
                BackButton(
                    onClick = { navController.navigateUp() },
                    contentDescription = context.getString(R.string.back)
                )
            },
            buttonThree = {}
        )
    }

}

/**
 * A composable function that displays a delete confirmation dialog.
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param journeyId The ID of the journey to which the POI belongs.
 * @param poiId The ID of the point of interest to be deleted.
 * @param deleteDialog Whether the delete dialog is visible.
 * @param closeConfirmDelete The callback function to close the delete confirmation dialog.
 */
@Composable
fun DeleteConfirmation(navController: NavController, journeyViewModel: JourneyViewModel, journeyId: Int, poiId: Int, deleteDialog: Boolean, closeConfirmDelete: () -> Unit){
    val context = LocalContext.current
    if (deleteDialog) {
        CustomDialog(
            title = stringResource(R.string.confirm_deletion),
            content = {
                Text(stringResource(R.string.are_you_sure_you_want_to_delete_this_point_of_interest_this_action_cannot_be_undone))
            },
            confirmButtonText = stringResource(R.string.delete),
            onConfirm = {
                // Call delete function from ViewModel
                journeyViewModel.deletePointOfInterestById(
                    journeyId = journeyId,
                    pointOfInterestId = poiId
                )
                closeConfirmDelete()
                Toast.makeText(
                    context,
                    context.getString(R.string.poi_deleted),
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigateUp() // Navigate back after deletion
            },
            onCancel = {
                closeConfirmDelete()
            }
        )
    }
}
