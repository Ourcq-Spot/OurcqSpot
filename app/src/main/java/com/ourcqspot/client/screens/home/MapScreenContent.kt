package com.ourcqspot.client.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.ourcqspot.client.R
import com.ourcqspot.client.composables.MapFiltersMenu
import com.ourcqspot.client.composables.buildIconPoiCulture
import com.ourcqspot.client.composables.buildIconPoiEvent
import com.ourcqspot.client.composables.buildIconUserPosition
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent() {
    Box (
        Modifier
            .clipToBounds() // forbids overflow
            .fillMaxSize(),
        Alignment.Center
    ) {
        MapLibreView()
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
fun MapLibreView() {
    AndroidView(
        { context ->
            MapLibre.getInstance(context)
            MapView(context).apply {
                getMapAsync { map ->
                    val key = "9zzN7nvsweC0cL7QY6k9" // BuildConfig.MAPTILER_API_KEY
                    val mapId = "streets-v2"
                    val styleUrl = "https://api.maptiler.com/maps/$mapId/style.json?key=$key"

                    map.setStyle(styleUrl) { style ->
//                        val userPosIconDrawable = ResourcesCompat.getDrawable(
//                            this.resources,
//                            //org.maplibre.android.R.drawable.maplibre_info_icon_default,
//                            R.drawable.icon_user_position,
//                            null
//                        )!!
//                        val userPosBitmapBlue = userPosIconDrawable.toBitmap()
//                        val userPosBitmapRed = userPosIconDrawable
//                            .mutate()
//                            .apply { setTint(android.graphics.Color.RED) }
//                            .toBitmap()
//
//                        val poiDefaultIconDrawable = ResourcesCompat.getDrawable(
//                            this.resources,
//                            R.drawable.icon_pin_event, //org.maplibre.android.R.drawable.maplibre_info_icon_default,
//                            null
//                        )!!
//                        val poiDefaultBitmap = poiDefaultIconDrawable.toBitmap()


//                        val userPosIcon = IconFactory.getInstance(context)
//                            .fromBitmap(if (false) userPosBitmapRed else userPosBitmapBlue)
//                        val poiDefaultIcon = IconFactory.getInstance(context)
//                            .fromBitmap(poiDefaultBitmap)

                        val userPosIcon = buildIconUserPosition(this, context)
                        val poiEventIcon = buildIconPoiEvent(this, context)
                        val poiCultureIcon = buildIconPoiCulture(this, context)


                        map.addMarker(MarkerOptions()
                            .position(LatLng(48.90290839445146, 2.4565472144686185))
                            .icon(userPosIcon)
                        )
                        map.addMarker(MarkerOptions()
                            .position(LatLng(48.98290839445176, 2.4065472144680085))
                            .title("POI")
                            .snippet("Description du POI")
                            .icon(poiEventIcon)
                        )
                        map.addMarker(MarkerOptions()
                            .position(LatLng(48.96290839445176, 2.4865472144680085))
                            .title("POI")
                            .snippet("Description du POI")
                            .icon(poiCultureIcon)
                        )
                    }

                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(48.90290839445146, 2.4565472144686185))
                        .zoom(10.2)
                        .build()
                }
            }
        },
        Modifier.fillMaxSize()
    )
}


/*@Composable
fun MockupMap() {
    Image(
        painterResource(R.drawable.map_default),
        contentDescription = "",
        Modifier
            .fillMaxSize()
            .scale(1.925F)
            .offset(x = (15).dp, y = 66.dp),
        contentScale = ContentScale.Crop
    )
    Image(
        painterResource(R.drawable.icon_user_position),
        contentDescription = "Vous êtes ici",
        Modifier
            .size(50.dp)
            .offset(x = (-133).dp, y = (-50).dp)
    )
}*/

/*@Composable
fun MapLibreView() {
    AndroidView(
        { context ->
            MapLibre.getInstance(context)
            MapView(context).apply {
                getMapAsync { map ->
                    //map.setStyle("https://demotiles.maplibre.org/style.json")
                    //map.setStyle("mapStyle_1.json")

                    val type = "test"
                    val jsonStyle = """
                        {
                          "version": 8,
                          "name": "OSM OpenMapTiles",
                          "id": "openmaptiles",
                          "center": [
                            0,
                            0
                          ],
                          "zoom": 1,
                          "bearing": 0,
                          "pitch": 0,
                          "sources": {
                            "openmaptiles": {
                              "type": "vector",
                              "url": "mbtiles:///data/tiles.mbtiles"
                            },
                            "attribution": {
                              "attribution": "<a href=\"https://www.openmaptiles.org/\" target=\"_blank\">&copy; OpenMapTiles</a> <a href=\"https://www.openstreetmap.org/copyright\" target=\"_blank\">&copy; OpenStreetMap contributors</a>",
                              "type": "vector"
                            }
                          },
                          "glyphs": "{fontstack}/{range}.pbf",
                          "sprite": "sprite",
                          "layers": [
                            {
                              "id": "background",
                              "type": "background",
                              "layout": {
                                "visibility": "visible"
                              },
                              "paint": {
                                "background-color": "#f2efe9"
                              }
                            }
                          ]
                        }
                    """.trimIndent()
                    //map.setStyle(Style.Builder().fromJson(jsonStyle))
                    val key = "9zzN7nvsweC0cL7QY6k9"// BuildConfig.MAPTILER_API_KEY
                    val mapId = "streets-v2"
                    val styleUrl = "https://api.maptiler.com/maps/$mapId/style.json?key=$key"
                    map.setStyle(styleUrl)



                    map.cameraPosition = CameraPosition.Builder()
                        //.target(LatLng(0.0,0.0))
                        .target(LatLng(48.90290839445146, 2.4565472144686185))
                        .zoom(10.2)
                        //.zoom(1.0)
                        .build()
                    //map.setStyle("MAPBOX_STREETS")
                    //map.setStyle(Style.MAPBOX_STREETS) // Change de style si besoin
                }
            }
        },
        Modifier.fillMaxSize()
    )
}*/