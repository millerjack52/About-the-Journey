package nz.ac.canterbury.seng303.aboutthejourney.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.ScreenTitle
import nz.ac.canterbury.seng303.aboutthejourney.components.ThreeSectionLayout
import nz.ac.canterbury.seng303.aboutthejourney.components.composites.BackButtonRow
import nz.ac.canterbury.seng303.aboutthejourney.enums.JourneyDurationMode
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel


/**
 * A composable function that displays the settings screen.
 *
 * @param navController The NavController used for navigation.
 * @param settingsViewModel The ViewModel that holds the settings data.
 * */
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    ThreeSectionLayout(
        topContent = { ScreenTitle(title = stringResource(R.string.settings))},
        middleContent = { Settings(settingsViewModel)},
        bottomContent = { BackButtonRow { navController.navigate("Home") } }
    )
}


/**
 * A composable function that displays the settings.
 *
 * @param settingsViewModel The ViewModel that holds the settings data.
 * */
@Composable
fun Settings(settingsViewModel: SettingsViewModel){
    val coroutineScope = rememberCoroutineScope() //allows for synchronous storage changes.
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val maxPhotos by settingsViewModel.maxPhotos.collectAsState()
    val journeyDurationMode by settingsViewModel.journeyDurationMode.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {


        Spacer(modifier = Modifier.height(16.dp))

        // Dark Mode Switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.dark_mode), modifier = Modifier.weight(1f))
            Switch(checked = darkMode, onCheckedChange = { isChecked ->
                coroutineScope.launch {
                    settingsViewModel.setDarkMode(isChecked)
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Max Photos Slider
        Text(text = stringResource(R.string.max_photos_slider, maxPhotos))
        Slider(
            value = maxPhotos.toFloat(),
            onValueChange = { newValue ->
                settingsViewModel.setMaxPhotos(newValue.toInt())
            },
            valueRange = 1f..10f,
            steps = 9
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Journey Duration Mode Dropdown
        val durationModes = JourneyDurationMode.entries
        var durationExpanded by remember { mutableStateOf(false) }

        Text(text = stringResource(
            R.string.journey_duration_mode,
            journeyDurationMode.name.replace("_", " ")
        ), fontSize = 16.sp)
        Box {
            Button(onClick = { durationExpanded = true }) {
                Text(stringResource(R.string.select_duration_mode))
            }

            DropdownMenu(expanded = durationExpanded, onDismissRequest = { durationExpanded = false }) {
                durationModes.forEach { mode ->
                    DropdownMenuItem(onClick = {
                        coroutineScope.launch {
                            settingsViewModel.setJourneyDurationMode(mode)
                        }
                        durationExpanded = false
                    },
                        text = { Text(text = mode.name.replace("_", " ").uppercase(), fontSize = 16.sp) })
                }
            }
        }
    }
}
