package nz.ac.canterbury.seng303.aboutthejourney.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.ButtonsColumn
import nz.ac.canterbury.seng303.aboutthejourney.components.TwoSectionLayout
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.DeleteButton
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.EditButton
import nz.ac.canterbury.seng303.aboutthejourney.components.composites.BackButtonRow
import nz.ac.canterbury.seng303.aboutthejourney.components.composites.EditAddAndSettingsButtonsRow
import nz.ac.canterbury.seng303.aboutthejourney.components.dialogs.ConfirmDialog
import nz.ac.canterbury.seng303.aboutthejourney.components.dialogs.JourneyDialog
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyStatus
import nz.ac.canterbury.seng303.aboutthejourney.helpers.importexport.importJourney
import nz.ac.canterbury.seng303.aboutthejourney.models.Journey
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey.CreateJourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey.EditJourneyViewModel

/**
 * The home screen of the app where the user can view their journeys,
 * add a new journey, edit a journey, or navigate to the app settings.
 * @param navController             the navigation controller
 * @param journeyViewModel             the view model for the journey
 * @param createJourneyViewModel       the view model for creating a journey
 * @param editJourneyViewModel         the view model for editing a journey
 */
@Composable
fun HomeScreen(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    createJourneyViewModel: CreateJourneyViewModel,
    editJourneyViewModel: EditJourneyViewModel
) {
    val context = LocalContext.current

    // Switches from viewing journeys to editing journeys
    var editingJourneys by rememberSaveable { mutableStateOf(false) }

    // Fetches the journeys for viewing
    journeyViewModel.getJourneys()
    val journeys: List<Journey> by journeyViewModel.journeys.collectAsState(emptyList())

    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val importedJourney: Journey? = importJourney(uri, context)
                if (importedJourney != null) {
                    journeyViewModel.importJourney(importedJourney)
                    Toast.makeText(
                        context,
                        context.getString(R.string.journey_imported),
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    Toast.makeText(
                        context,
                        context.getString(R.string.journey_import_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            }
        }
    }

    fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        directoryPickerLauncher.launch(intent)
    }

    // Dialogs setup for creating, editing, and deleting journeys
    var showCreateJourneyDialog by rememberSaveable { mutableStateOf(false) }
    var showEditJourneyDialog by rememberSaveable { mutableStateOf(false) }
    var showConfirmDeleteJourneyDialog by rememberSaveable { mutableStateOf(false) }
    var showCreateButtonsColumn by rememberSaveable { mutableStateOf(false) }

    JourneyDialogs(
        showCreateJourneyDialog = showCreateJourneyDialog,
        showEditJourneyDialog = showEditJourneyDialog,
        showConfirmDeleteJourneyDialog = showConfirmDeleteJourneyDialog,
        closeCreateJourneyDialog = { showCreateJourneyDialog = false },
        closeEditJourneyDialog = { showEditJourneyDialog = false },
        closeConfirmDeleteJourneyDialog = { showConfirmDeleteJourneyDialog = false },
        createJourneyViewModel = createJourneyViewModel,
        editJourneyViewModel = editJourneyViewModel,
        journeyViewModel = journeyViewModel
    )

    TwoSectionLayout(
        mainContent = {
            JourneysListView(
                navController = navController,
                journeyViewModel = journeyViewModel,
                journeys = journeys,
                editingJourneys = editingJourneys,
                editJourneyViewModel = editJourneyViewModel,
                showEditJourneyDialog = { showEditJourneyDialog = true },
                showConfirmDeleteJourneyDialog = { showConfirmDeleteJourneyDialog = true }
            )


            if (showCreateButtonsColumn) {
                ButtonsColumn(
                    onCreateJourneyClick = {
                        showCreateButtonsColumn = false
                        showCreateJourneyDialog = true
                    },
                    onImportJourneyClick = {
                        showCreateButtonsColumn = false
                        openDirectoryPicker()
                    }
                )
            }
        },
        bottomContent = {
            if (!editingJourneys) {
                EditAddAndSettingsButtonsRow(
                    showEditingMode = { showCreateButtonsColumn = false; editingJourneys = true },
                    showCreateMode = { showCreateButtonsColumn = !showCreateButtonsColumn },
                    showSettings = {navController.navigate("Settings")}
                )
            } else {
                BackButtonRow(goBack = { editingJourneys = false })
            }
        }
    )
}

/**
 * The dialogs for creating, editing, and deleting journeys.
 * @param showCreateJourneyDialog          whether to show the create journey dialog
 * @param showEditJourneyDialog            whether to show the edit journey dialog
 * @param showConfirmDeleteJourneyDialog   whether to show the confirm delete journey dialog
 * @param closeCreateJourneyDialog         the action to take when the create journey dialog is closed
 * @param closeEditJourneyDialog           the action to take when the edit journey dialog is closed
 * @param closeConfirmDeleteJourneyDialog  the action to take when the confirm delete journey dialog is closed
 * @param createJourneyViewModel           the view model for creating a journey
 * @param editJourneyViewModel             the view model for editing a journey
 * @param journeyViewModel                 the view model for the journey
 */
@Composable
fun JourneyDialogs(
    showCreateJourneyDialog: Boolean,
    showEditJourneyDialog: Boolean,
    showConfirmDeleteJourneyDialog: Boolean,
    closeCreateJourneyDialog: () -> Unit,
    closeEditJourneyDialog: () -> Unit,
    closeConfirmDeleteJourneyDialog: () -> Unit,
    createJourneyViewModel: CreateJourneyViewModel,
    editJourneyViewModel: EditJourneyViewModel,
    journeyViewModel: JourneyViewModel
) {
    val context = LocalContext.current

    if (showCreateJourneyDialog) {
        JourneyDialog(
            title = context.getString(R.string.create_a_new_journey),
            name = createJourneyViewModel.name,
            onNameChange = { createJourneyViewModel.updateName(it) },
            onSaveJourney = { journeyName ->
                journeyViewModel.createJourney(journeyName, context)
                createJourneyViewModel.clearName()
                Toast.makeText(
                    context,
                    context.getString(R.string.journey_created),
                    Toast.LENGTH_SHORT
                ).show()
            },
            closeJourneyDialog = closeCreateJourneyDialog
        )
    }

    if (showEditJourneyDialog) {
        JourneyDialog(
            title = context.getString(R.string.edit_journey),
            name = editJourneyViewModel.name,
            onNameChange = { editJourneyViewModel.updateName(it) },
            onSaveJourney = { journeyName ->
                journeyViewModel.editJourneyNameById(editJourneyViewModel.id, journeyName)
                editJourneyViewModel.clearModel()
                Toast.makeText(
                    context,
                    context.getString(R.string.journey_edited),
                    Toast.LENGTH_SHORT
                ).show()
            },
            closeJourneyDialog = closeEditJourneyDialog
        )
    }

    if (showConfirmDeleteJourneyDialog) {
        ConfirmDialog(
            title = context.getString(R.string.delete_journey),
            message = context.getString(R.string.delete_journey_message, editJourneyViewModel.name),
            onConfirm = {
                journeyViewModel.deleteJourneyById(editJourneyViewModel.id, context)
                editJourneyViewModel.clearModel()
                Toast.makeText(
                    context,
                    context.getString(R.string.journey_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            },
            onCancel = closeConfirmDeleteJourneyDialog
        )
    }
}

/**
 * The list view of the journeys.
 * @param journeys                         the list of journeys
 * @param editingJourneys                  whether the user is editing journeys
 * @param editJourneyViewModel             the view model for editing a journey
 * @param showEditJourneyDialog            the action to take when the user wants to edit a journey
 * @param showConfirmDeleteJourneyDialog   the action to take when the user wants to delete a journey
 */
@Composable
fun JourneysListView(
    navController: NavController,
    journeyViewModel: JourneyViewModel,
    journeys: List<Journey>,
    editingJourneys: Boolean,
    editJourneyViewModel: EditJourneyViewModel,
    showEditJourneyDialog: () -> Unit,
    showConfirmDeleteJourneyDialog: () -> Unit
) {
    val context = LocalContext.current

    val completedJourneys = journeys.filter { it.status == JourneyStatus.COMPLETED || it.status == JourneyStatus.IMPORTED }
    val uncompletedJourneys = journeys.filter { it.status == JourneyStatus.ONGOING }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Card for In Progress Journeys
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF5573B5), Color(0xFFA3BCEB))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.in_progress_journeys))
                Spacer(modifier = Modifier.height(10.dp))

                if (uncompletedJourneys.isEmpty()) {
                    Text(
                        text = context.getString(R.string.no_ongoing_journeys_message),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(uncompletedJourneys) { journey ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(vertical = 4.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF87A7C0),
                                                Color(0xFF8CA7DB),
                                                Color(0xFFDAE3EB)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        navController.navigate("Journey/${journey.id}")
                                    }
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = journey.name,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (editingJourneys) {
                                    EditButton(
                                        onClick = {
                                            editJourneyViewModel.updateId(journey.id)
                                            editJourneyViewModel.updateName(journey.name)
                                            showEditJourneyDialog()
                                        },
                                        contentDescription = context.getString(R.string.edit_button_description)
                                    )
                                    DeleteButton(
                                        onClick = {
                                            editJourneyViewModel.updateId(journey.id)
                                            editJourneyViewModel.updateName(journey.name)
                                            showConfirmDeleteJourneyDialog()
                                        },
                                        contentDescription = context.getString(R.string.delete_button_description)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card for Completed Journeys
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF5573B5), Color(0xFFA3BCEB))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.completed_journeys))
                Spacer(modifier = Modifier.height(10.dp))

                if (completedJourneys.isEmpty()) {
                    Text(
                        text = context.getString(R.string.no_completed_journeys_message),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(completedJourneys) { journey ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(vertical = 4.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF87A7C0),
                                                Color(0xFF8CA7DB),
                                                Color(0xFFDAE3EB)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        navController.navigate("Journey/${journey.id}")
                                    }
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = journey.name,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    if (journey.status == JourneyStatus.IMPORTED) {
                                        Text(
                                            text = context.getString(R.string.imported),
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.DarkGray // Light grey color
                                            )
                                        )
                                    }
                                }

                                if (editingJourneys) {
                                    EditButton(
                                        onClick = {
                                            editJourneyViewModel.updateId(journey.id)
                                            editJourneyViewModel.updateName(journey.name)
                                            showEditJourneyDialog()
                                        },
                                        contentDescription = context.getString(R.string.edit_button_description)
                                    )
                                    DeleteButton(
                                        onClick = {
                                            editJourneyViewModel.updateId(journey.id)
                                            editJourneyViewModel.updateName(journey.name)
                                            showConfirmDeleteJourneyDialog()
                                        },
                                        contentDescription = context.getString(R.string.delete_button_description)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}
