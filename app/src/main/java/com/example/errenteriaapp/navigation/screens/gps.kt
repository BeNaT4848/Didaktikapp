package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.errenteriaapp.classes.Kokapena
import com.example.errenteriaapp.classes.nireKokapenak
import com.example.errenteriaapp.components.AppScaffold
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaOsmScreen(navController: NavController) {
    AppScaffold(navController = navController) {
        Box(modifier = Modifier.fillMaxSize()) {
            OsmMapView(
                nireKokapenak = nireKokapenak,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun OsmMapView(nireKokapenak: List<Kokapena>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    remember {
        Configuration.getInstance().apply {
            load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
            userAgentValue = context.packageName
        }
        true
    }

    AndroidView(
        factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                // Opcional\: evita "tile blanks" en algunos dispositivos/caches
                isTilesScaledToDpi = true
            }
        },
        update = { mapView ->
            val mapController = mapView.controller
            mapController.setZoom(18.0)
            mapController.setCenter(GeoPoint(43.2687, -2.9337))

            mapView.overlays.clear()

            nireKokapenak.forEach { kokapena ->
                Marker(mapView).apply {
                    position = GeoPoint(kokapena.latitudea, kokapena.longitudea)
                    title = kokapena.izena
                    snippet = kokapena.deskribapena
                    mapView.overlays.add(this)
                }
            }

            mapView.invalidate()
        },
        modifier = modifier
    )
}
