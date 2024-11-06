package nz.ac.canterbury.seng303.aboutthejourney.helpers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

/**
 * A composable function that retrieves the current location.
 *
 * @return The current location as a LatLng object.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun getLocation(): LatLng {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng>(LatLng(0.0, 0.0))}

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // You can choose the priority level
        1000L // Update interval in milliseconds
    ).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    //location client:
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    DisposableEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    //Old version doesn't get location updates often enough.
//    if (arePermissionsGranted()){
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return currentLocation
//        }
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            location?.let {
//                currentLocation = LatLng(it.latitude, it.longitude)
//            }
//        }
//    }

    return currentLocation
}