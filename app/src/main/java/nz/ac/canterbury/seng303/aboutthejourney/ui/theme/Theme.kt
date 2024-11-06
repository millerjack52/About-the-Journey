package nz.ac.canterbury.seng303.aboutthejourney.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import nz.ac.canterbury.seng303.aboutthejourney.viewmodels.SettingsViewModel

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White
)

@Composable
fun AboutTheJourneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    settingsViewModel: SettingsViewModel,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme or settingsViewModel.darkMode.collectAsState().value) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme or settingsViewModel.darkMode.collectAsState().value -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}