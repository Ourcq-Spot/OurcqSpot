package com.ourcqspot.client.screens.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.ourcqspot.client.composables.MapFiltersMenu
import com.ourcqspot.client.composables.buildIconPoiCulture
import com.ourcqspot.client.composables.buildIconPoiEvent
import com.ourcqspot.client.composables.buildIconUserPosition
import com.ourcqspot.client.viewmodel.LocationViewModel
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdate
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

/*@SuppressLint("NewApi")
@Composable
fun MapScreenContent() {
    Box (
        Modifier
            .clipToBounds() // forbids overflow
            .fillMaxSize(),
        Alignment.Center
    ) {
        val context = LocalContext.current
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

        var userLatLng by remember {
            mutableStateOf<LatLng?>(null)
        }

        val locationRequest = remember {
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        }
        val locationCallback = remember {
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    Log.d("LOCATION", "Location result received")
                    result.lastLocation?.let { location ->
                        userLatLng = LatLng(location.latitude, location.longitude)
                        Log.d("LOCATION", "Updated location: $userLatLng")
                    }
                }
            }
        }

        // Lance les updates une seule fois
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("LOCATION", "Permission accordée")
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }

        // Affiche la carte quand la position est disponible (sinon spinner ou fallback)
        userLatLng?.let {
            MapLibreView(userLatLng = it)
            MapFiltersMenu(
                Modifier
                    .align(Alignment.TopEnd)   // Important de le faire ici pour être dans BoxScope
                    .padding(top = 25.dp, bottom = 200.dp),
            )
            /*ModalBottomSheet(onDismissRequest = { /* Executed when the sheet is dismissed */ }) {
                // Sheet content
                Text("Ciné 104")
                Spacer(Modifier.height(10.dp))
                Text("Votre cinéma du quartier pour le cinéma actuel et les films cultes pour tous les publics.")
                Spacer(Modifier.height(10.dp))
                Text("Horaires :")
            }*/
        } ?: Text("Chargement de la position...")
    }
}*/

@Composable
fun MapScreenContent() {
    Box (
        Modifier
            .clipToBounds() // forbids overflow
            .fillMaxSize(),
        Alignment.Center
    ) {
        GeolocatedMapLibreView()
        MapFiltersMenu(
            Modifier
                .align(Alignment.TopEnd)   // Important de le faire ici pour être dans BoxScope
                .padding(top = 25.dp, bottom = 200.dp),
        )
        /*ModalBottomSheet(onDismissRequest = { /* Executed when the sheet is dismissed */ }) {
            // Sheet content
            Text("Ciné 104")
            Spacer(Modifier.height(10.dp))
            Text("Votre cinéma du quartier pour le cinéma actuel et les films cultes pour tous les publics.")
            Spacer(Modifier.height(10.dp))
            Text("Horaires :")
        }*/
    }
}





@Composable
fun GeolocatedMapLibreView() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Variable pour garder une référence au dernier niveau de zoom
    val locationWasInitialized = remember { mutableStateOf(false) }
    val cameraFollowsLocation = remember { mutableStateOf(false) }
    val zoomLevel = remember { mutableDoubleStateOf(15.0) } // Niveau de zoom par défaut

    AndroidView(
        factory = { context ->
            Log.d("MAP", "MapView created")
            MapLibre.getInstance(context)
            MapView(context).apply {
                Log.d("MAP", "MapView applied")
                // Initialisation de la carte
                getMapAsync { map ->
                    val key = "9zzN7nvsweC0cL7QY6k9" // Clé MapTiler API
                    val mapId = "streets-v2"
                    val styleUrl = "https://api.maptiler.com/maps/$mapId/style.json?key=$key"
                    map.setStyle(styleUrl) {
                        // Ajouter des marqueurs fixes (optionnel)
                        val poiEventIcon = buildIconPoiEvent(this, context)
                        val poiCultureIcon = buildIconPoiCulture(this, context)

                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(48.98290839445176, 2.4065472144680085))
                                .title("POI")
                                .snippet("Description du POI")
                                .icon(poiEventIcon)
                        )
                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(48.96290839445176, 2.4865472144680085))
                                .title("POI")
                                .snippet("Description du POI")
                                .icon(poiCultureIcon)
                        )

                        var userLocationMarker: Marker? = null

                        // Mise à jour continue de la position de l'utilisateur (optionnel)
                        val locationRequest = LocationRequest.create().apply {
                            interval = 5000 // Mise à jour toutes les 10 secondes
                            fastestInterval = 2000 // Intervalle le plus rapide
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        }

                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(p0: LocationResult) {
                                p0.let {
                                    val location = it.lastLocation
                                    location?.let { loc ->
                                        val newLatLng = LatLng(loc.latitude, loc.longitude)
                                        userLocationMarker?.remove()
                                        // Ajouter ou mettre à jour le marqueur utilisateur
                                        userLocationMarker = map.addMarker(
                                            MarkerOptions()
                                                .position(newLatLng)
                                                .title("Vous êtes ici")
                                        )
                                        if (cameraFollowsLocation.value || !locationWasInitialized.value) {
                                            map.animateCamera(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    newLatLng,
                                                    zoomLevel.doubleValue
                                                )
                                            )
                                            if (!locationWasInitialized.value) locationWasInitialized.value = true
                                        }
                                    }
                                }
                            }
                        }

                        val permission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let {
                                    val userLocation = LatLng(it.latitude, it.longitude)
                                    var cameraUpdate: CameraUpdate? = null
                                    if (cameraFollowsLocation.value) {
                                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                            userLocation,
                                            zoomLevel.doubleValue
                                        )
                                    }
                                    if(cameraUpdate!=null) {
                                        map.animateCamera(cameraUpdate)
                                    }
                                    /*val userMarker =
                                        MarkerOptions().position(userLocation).title("You are here")
                                    map.addMarker(userMarker)*/
                                }
                            }
                        }

                        // Demander la mise à jour de la position de l'utilisateur
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}





//@Composable
//fun GeolocatedMapLibreView() {
//    val context = LocalContext.current
//    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//    // Variable pour garder une référence au dernier niveau de zoom
//    val zoomLevel = remember { mutableDoubleStateOf(15.0) } // Niveau de zoom par défaut
//
//    AndroidView(
//        factory = { context ->
//            Log.d("MAP", "MapView created")
//            MapLibre.getInstance(context)
//            MapView(context).apply {
//                Log.d("MAP", "MapView applied")
//                // Initialisation de la carte
//                getMapAsync { map ->
//                    val key = "9zzN7nvsweC0cL7QY6k9" // Clé MapTiler API
//                    val mapId = "streets-v2"
//                    val styleUrl = "https://api.maptiler.com/maps/$mapId/style.json?key=$key"
//                    map.setStyle(styleUrl) {
//                        // Ajouter des marqueurs fixes (optionnel)
//                        val poiEventIcon = buildIconPoiEvent(this, context)
//                        val poiCultureIcon = buildIconPoiCulture(this, context)
//
//                        map.addMarker(
//                            MarkerOptions()
//                                .position(LatLng(48.98290839445176, 2.4065472144680085))
//                                .title("POI")
//                                .snippet("Description du POI")
//                                .icon(poiEventIcon)
//                        )
//                        map.addMarker(
//                            MarkerOptions()
//                                .position(LatLng(48.96290839445176, 2.4865472144680085))
//                                .title("POI")
//                                .snippet("Description du POI")
//                                .icon(poiCultureIcon)
//                        )
//
//                        val permission = ContextCompat.checkSelfPermission(
//                            context,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        )
//                        if (permission == PackageManager.PERMISSION_GRANTED) {
//                            // Récupérer la position actuelle de l'utilisateur et centre la caméra
//                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                                location?.let {
//                                    val userLocation = LatLng(it.latitude, it.longitude)
//                                    // Déplacer la caméra sans toucher au zoom actuel
//                                    map.animateCamera(
//                                        CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel.doubleValue)
//                                    )
//                                    // Ajouter un marqueur pour la position de l'utilisateur
//                                    val userMarker =
//                                        MarkerOptions().position(userLocation).title("You are here")
//                                    map.addMarker(userMarker)
//                                }
//                            }
//                        }
//
//                        // Mise à jour continue de la position de l'utilisateur (optionnel)
//                        val locationRequest = LocationRequest.create().apply {
//                            interval = 5000 // Mise à jour toutes les 10 secondes
//                            fastestInterval = 2000 // Intervalle le plus rapide
//                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//                        }
//
//                        val locationCallback = object : LocationCallback() {
//                            override fun onLocationResult(p0: LocationResult) {
//                                p0.let {
//                                    val location = it.lastLocation
//                                    location?.let { loc ->
//                                        // Vérifier si la position a changé de manière significative
//                                        val newLocation = LatLng(loc.latitude, loc.longitude)
//                                        map.animateCamera(
//                                            CameraUpdateFactory.newLatLng(newLocation)
//                                        )
//                                        // Ajouter ou mettre à jour le marqueur utilisateur
//                                        val userMarker = MarkerOptions().position(newLocation).title("You are here")
//                                        map.addMarker(userMarker)
//                                    }
//                                }
//                            }
//                        }
//
//                        // Demander la mise à jour de la position de l'utilisateur
//                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//                    }
//                }
//            }
//        },
//        modifier = Modifier.fillMaxSize()
//    )
//}



