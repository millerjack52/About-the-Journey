package nz.ac.canterbury.seng303.aboutthejourney.helpers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Checks if the required permissions are granted.
 *
 * @return `true` if all required permissions are granted, `false` otherwise.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun arePermissionsGranted(): Boolean {
    val arePermissionsGranted = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission list
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS
    )

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        arePermissionsGranted.value = permissionsResult.all { it.value }
    }

    val allPermissionsGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    arePermissionsGranted.value = allPermissionsGranted


    LaunchedEffect(key1 = allPermissionsGranted) { // launch permission request when component is first composed
        if (!allPermissionsGranted) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            arePermissionsGranted.value = true
        }
    }


    DisposableEffect(lifecycleOwner) { // Observe lifecycle events to re-check permissions when the app resumes
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val allPermissionsGrantedNow = permissions.all { // Re-check permissions when the app resumes
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                arePermissionsGranted.value = allPermissionsGrantedNow

                if(!allPermissionsGrantedNow){
                    //Set to don't allow happens on the second deny. While on ask everytime it will continue to ask.
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            }
        }


        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return arePermissionsGranted.value
}





