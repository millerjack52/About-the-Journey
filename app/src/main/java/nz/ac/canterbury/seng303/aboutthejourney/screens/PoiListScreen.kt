package nz.ac.canterbury.seng303.aboutthejourney.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.ScreenTitle
import nz.ac.canterbury.seng303.aboutthejourney.components.ThreeSectionLayout
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.BackButton
import nz.ac.canterbury.seng303.aboutthejourney.components.dialogs.ConfirmDialog
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.models.PointOfInterest
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel

/**
 * A composable function that displays the screen to view the list of POIs.
 *
 * @param navController The NavController used for navigation.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param journeyId The ID of the journey whose POIs are to be displayed.
 */
@Composable
fun ViewPoiListScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    journeyId: String
) {
    journeyViewModel.getJourneyById(journeyId.toIntOrNull())
    val selectedJourney by journeyViewModel.selectedJourney.collectAsState()
    val context = LocalContext.current

    val pois: List<PointOfInterest> by journeyViewModel.pois.collectAsState(emptyList())
    var showConfirmDeletePoiDialog by remember { mutableStateOf(false) }
    var poiIdToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedJourney?.id) {
        selectedJourney?.id?.let { journeyId ->
            journeyViewModel.getAllPointOfInterestsByJourneyId(journeyId)
        }
    }

    PoiDialogs(
        showConfirmDeletePoiDialog = showConfirmDeletePoiDialog,
        closeConfirmDeletePoiDialog = { showConfirmDeletePoiDialog = false },
        journeyViewModel = journeyViewModel,
        poiId = poiIdToDelete,
        journeyId = selectedJourney?.id,
        onConfirmDelete = {
            showConfirmDeletePoiDialog = false
            poiIdToDelete = null
        }
    )

    ThreeSectionLayout(
        topContent = {
            ScreenTitle(title = stringResource(R.string.points_of_interest))
        },
        middleContent = {
            PoiList(
                poiList = pois,
                journey = selectedJourney!!,
                onPoiClick = { poiId ->
                    navController.navigate("Journey/${selectedJourney?.getIdentifier()}/ViewPoi/${poiId}") },
                onEditClick = { poiId ->
                    navController.navigate("Journey/${selectedJourney?.getIdentifier()}/EditPoi/${poiId}") },
                onDeleteClick = { poiId ->
                    poiIdToDelete = poiId
                    showConfirmDeletePoiDialog = true
                }
            )
        },
        bottomContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()) {
                BackButton(onClick = { navController.navigateUp() }, contentDescription = context.getString(R.string.back),)
            }

        }
    )
}


/**
 * A composable function that displays a list of POIs.
 *
 * @param poiList The list of points of interest.
 * @param journey The journey to which the POIs belong.
 * @param onPoiClick The callback function to be invoked when a POI is clicked.
 * @param onEditClick The callback function to be invoked when the edit button is clicked.
 * @param onDeleteClick The callback function to be invoked when the delete button is clicked.
 */
@Composable
fun PoiList(
    poiList: List<PointOfInterest>,
    journey: Journey,
    onPoiClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (poiList.isEmpty()) {
        Text(
            text = stringResource(R.string.no_points_of_interest),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(poiList) { poi ->
                PoiListItem(
                    poi = poi,
                    journey = journey,
                    onPoiClick = { onPoiClick(poi.id.toString()) },
                    onEditClick = { onEditClick(poi.id.toString()) },
                    onDeleteClick = { onDeleteClick(poi.id.toString()) }
                )
            }
        }
    }
}


/**
 * A composable function that displays a single POI item in the list.
 *
 * @param poi The point of interest to be displayed.
 * @param journey The journey to which the POI belongs.
 * @param onPoiClick The callback function to be invoked when the POI is clicked.
 * @param onEditClick The callback function to be invoked when the edit button is clicked.
 * @param onDeleteClick The callback function to be invoked when the delete button is clicked.
 */
@Composable
fun PoiListItem(poi: PointOfInterest, journey: Journey, onPoiClick: () -> Unit, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = onPoiClick,
                modifier = Modifier.weight(0.75f)) {
            Text(
                text = if (poi.description.isEmpty()) context.getString(R.string.unnamed_poi) else poi.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        if (journey.status == JourneyStatus.ONGOING) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.weight(0.125f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_point_of_interest),
                    modifier = Modifier
                        .size(24.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.weight(0.125f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_point_of_interest),
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}

/**
 * A composable function that displays the dialogs for deleting a POI.
 *
 * @param showConfirmDeletePoiDialog A boolean indicating whether the dialog should be shown.
 * @param closeConfirmDeletePoiDialog The callback function to close the dialog.
 * @param journeyViewModel The ViewModel that holds the journey data.
 * @param poiId The ID of the POI to be deleted.
 * @param journeyId The ID of the journey to which the POI belongs.
 * @param onConfirmDelete The callback function to be invoked when the delete is confirmed.
 */
@Composable
fun PoiDialogs(
    showConfirmDeletePoiDialog: Boolean,
    closeConfirmDeletePoiDialog: () -> Unit,
    journeyViewModel: JourneyViewModel,
    poiId: String?,
    journeyId: Int?,
    onConfirmDelete: () -> Unit
) {
    val context = LocalContext.current

    if (showConfirmDeletePoiDialog && poiId != null && journeyId != null) {
        val poiIdAsInt = poiId.toIntOrNull()

        if (poiIdAsInt != null) {
            ConfirmDialog(
                title = stringResource(R.string.delete_poi),
                message = context.getString(R.string.poi_deleted_message),
                onConfirm = {
                    journeyViewModel.deletePointOfInterestById(journeyId, poiIdAsInt)
                    journeyViewModel.getAllPointOfInterestsByJourneyId(journeyId)
                    Toast.makeText(context,
                        context.getString(R.string.point_of_interest_deleted), Toast.LENGTH_SHORT).show()
                    onConfirmDelete()
                },
                onCancel = closeConfirmDeletePoiDialog
            )
        } else {
            Toast.makeText(context, stringResource(R.string.invalid_poi_id), Toast.LENGTH_SHORT).show()
        }
    }
}
