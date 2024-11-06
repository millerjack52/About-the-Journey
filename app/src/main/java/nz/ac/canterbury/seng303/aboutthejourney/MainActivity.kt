package nz.ac.canterbury.seng303.aboutthejourney

import JourneySummaryScreen
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nz.ac.canterbury.seng303.aboutthejourney.helpers.arePermissionsGranted
import nz.ac.canterbury.seng303.aboutthejourney.screens.AddPoiScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.EditPoiScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.HomeScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.JourneyScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.SettingsScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.ShareScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.ViewPoiListScreen
import nz.ac.canterbury.seng303.aboutthejourney.screens.ViewPoiScreen
import nz.ac.canterbury.seng303.aboutthejourney.ui.theme.AboutTheJourneyTheme
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.JourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.MainViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey.CreateJourneyViewModel
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.journey.EditJourneyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {
    private val journeyViewModel: JourneyViewModel by koinViewModel()
    private val viewModel: MainViewModel by viewModels<MainViewModel>()
    private val settingsViewModel: SettingsViewModel by  koinViewModel()


    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply { setKeepOnScreenCondition { !viewModel.isReady.value}  }
        setContent {
            AboutTheJourneyTheme(settingsViewModel = settingsViewModel) {
                createNotificationChannel()
                arePermissionsGranted()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(getScreenTitle(currentRoute)) },
                            navigationIcon = {
                                if (currentRoute != "Home") {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = getString(R.string.select_image_source)
                                        )
                                    }
                                }
                            },
                            actions = {
                                Icon(
                                    painter = painterResource(id = R.drawable.journeyicon),
                                    contentDescription = "Journey Icon",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(end = 16.dp)
                                )
                            }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        val createJourneyViewModel: CreateJourneyViewModel = viewModel()
                        val editJourneyViewModel: EditJourneyViewModel = viewModel()

                        NavHost(navController = navController, startDestination = "Home") {
                            // add composable routes here:
                            composable("Home") {
                                HomeScreen(
                                    navController = navController,
                                    journeyViewModel = journeyViewModel,
                                    createJourneyViewModel = createJourneyViewModel,
                                    editJourneyViewModel = editJourneyViewModel
                                )
                            }

                            composable("Journey/{journeyId}",
                                arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                if (journeyId != null) {
                                    JourneyScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        settingsViewModel = settingsViewModel,
                                        journeyId = journeyId)
                                }
                            }

                            composable("Journey/{journeyId}/AddPoi",
                                arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                if (journeyId != null) {
                                    AddPoiScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        journeyId = journeyId,
                                        imageCount = settingsViewModel.maxPhotos.collectAsState().value
                                    )
                                }
                            }


                            composable("Journey/{journeyId}/ViewPoi/{poiId}",
                                arguments = listOf(
                                    navArgument("journeyId") { type = NavType.StringType },
                                    navArgument("poiId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                val poiId = backStackEntry.arguments?.getString("poiId")
                                if (journeyId != null && poiId != null) {
                                    ViewPoiScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        journeyId = journeyId,
                                        poiId = poiId
                                    )
                                }
                            }

                            composable("Journey/{journeyId}/EditPoi/{poiId}",
                                arguments = listOf(
                                    navArgument("journeyId") { type = NavType.StringType },
                                    navArgument("poiId") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                val poiId = backStackEntry.arguments?.getString("poiId")
                                if (journeyId != null && poiId != null) {
                                    EditPoiScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        journeyId = journeyId,
                                        poiId = poiId,
                                        imageCount = settingsViewModel.maxPhotos.collectAsState().value
                                    )
                                }
                            }

                            composable("Journey/{journeyId}/ListPoi",
                                arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                if (journeyId != null) {
                                    ViewPoiListScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        journeyId = journeyId
                                    )
                                }
                            }

                            composable("Journey/{journeyId}/Share",
                                arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")

                                if (journeyId != null) {
                                    ShareScreen(
                                        navController = navController,
                                        journeyId = journeyId,
                                        journeyViewModel = journeyViewModel
                                    )
                                }
                            }

                            composable("Settings") {
                                SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
                            }
                            composable("Journey/{journeyId}/Summary",
                                arguments = listOf(navArgument("journeyId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val journeyId = backStackEntry.arguments?.getString("journeyId")
                                if (journeyId != null) {
                                    JourneySummaryScreen(
                                        navController = navController,
                                        journeyViewModel = journeyViewModel,
                                        settingsViewModel = settingsViewModel,
                                        journeyId = journeyId
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private fun getScreenTitle(route: String?): String {
        //update these to what ever
        return when {
            route == null -> getString(R.string.about_the_journey)
            route.startsWith("Journey") && route.contains("AddPoi") -> getString(R.string.add_point_of_interest)
            route.startsWith("Journey") && route.contains("ViewPoi") -> getString(R.string.view_point_of_interest)
            route.startsWith("Journey") && route.contains("EditPoi") -> getString(R.string.edit_point_of_interest)
            route.startsWith("Journey") && route.contains("ListPoi") -> getString(R.string.list_view)
            route.startsWith("Journey") && route.contains("Share") -> getString(R.string.share_journey)
            route.startsWith("Journey") -> getString(R.string.journey_details)
            route == "Home" -> getString(R.string.about_the_journey)
            route == "Settings" -> getString(R.string.settings)
            else -> getString(R.string.about_the_journey)
        }
    }

    /**
     * Create the notification channel for the app so that notifications can be sent.
     * Acknowledgement: This code is based on the createNotificationChannel function
     * from the Android Developers documentation on Notifications.
     * https://developer.android.com/develop/ui/views/notifications/build-notification
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "AboutTheJourney"
    }
}



