package com.example.errenteriaapp.navigation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.errenteriaapp.classes.Kokapena
import com.example.errenteriaapp.classes.nireKokapenak
import com.example.errenteriaapp.components.AppScaffold
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.library.R as OsmDroidR

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

/**
 * Contrato:
 * - Muestra marcadores fijos + tu ubicación en tiempo real.
 * - Botón para fijar/desfijar el seguimiento (centrado continuo).
 */
@SuppressLint("MissingPermission")
@Composable
fun OsmMapView(nireKokapenak: List<Kokapena>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Nota: osmdroid puede funcionar sin inicializar Configuration aquí.
    // Si quieres cache/UA configurados, lo reintroducimos añadiendo la dependencia de Preference.

    // --- Permisos + estado de ubicación ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Si true: el mapa se centra en cada update de ubicación
    var followMyLocation by rememberSaveable { mutableStateOf(true) }

    // Menos zoom al arrancar (cuando llegue el primer fix)
    // 18-19 suele ser un buen equilibrio para ver calles sin exagerar.
    val initialZoom = 18.5

    // Fallback para evitar ver "el mundo" mientras llega el primer fix
    // (si no hay center/zoom inicial, osmdroid puede arrancar en vista global)
    val startupCenter = GeoPoint(43.2687, -2.9337)
    val startupZoom = 14.0

    // NUEVO: para que el mapa "arranque" en tu ubicación y no en un punto fijo
    var hasInitialCentered by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission =
            (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    DisposableEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = fineGranted || coarseGranted

        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        onDispose { }
    }

    // NUEVO: intentar centrar rápido usando la última ubicación conocida
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission || hasInitialCentered) return@LaunchedEffect

        val fused = LocationServices.getFusedLocationProviderClient(context)

        // 1) lastLocation (rápido si existe)
        try {
            val last = fused.lastLocation
            last.addOnSuccessListener { loc ->
                if (loc != null && !hasInitialCentered) {
                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                }
            }
        } catch (_: SecurityException) {
            // ignorar
        }

        // 2) getCurrentLocation (puede ser más rápido que esperar al callback)
        try {
            fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null && !hasInitialCentered) {
                        myLocation = GeoPoint(loc.latitude, loc.longitude)
                    }
                }
        } catch (_: SecurityException) {
            // ignorar
        }
    }

    // Updates de ubicación en tiempo real
    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            val fused = LocationServices.getFusedLocationProviderClient(context)

            // Ajuste: primer fix más rápido + luego precisión alta
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1500L
            )
                .setMinUpdateIntervalMillis(800L)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation ?: return
                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                }
            }

            fused.requestLocationUpdates(request, callback, Looper.getMainLooper())

            onDispose {
                fused.removeLocationUpdates(callback)
            }
        }
    }

    // --- MapView + overlays: NO limpiar overlays en cada recomposición ---
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val myMarkerRef = remember { mutableStateOf<Marker?>(null) }

    // NUEVO: cuando cambia la ubicación, fuerza redraw y centra.
    // - Primer fix: centra + aplica zoom fuerte y marca hasInitialCentered
    // - Después: si está fijado, sigue centrando
    LaunchedEffect(myLocation, followMyLocation) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        val point = myLocation ?: return@LaunchedEffect

        if (!hasInitialCentered) {
            mapView.controller.setZoom(initialZoom)
            mapView.controller.setCenter(point)
            hasInitialCentered = true
        } else if (followMyLocation) {
            mapView.controller.setCenter(point)
        }

        mapView.invalidate()
    }

    // Overlay simple para dibujar un "punto azul" (muy visible)
    val myDotOverlay = remember {
        object : Overlay() {
            private val paintFill = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                color = "#1E88E5".toColorInt() // azul
            }
            private val paintStroke = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.WHITE
            }

            override fun draw(c: android.graphics.Canvas?, osmv: MapView?, shadow: Boolean) {
                if (shadow) return
                val canvas = c ?: return
                val mapView = osmv ?: return

                val p = myLocation ?: return
                val point = mapView.projection.toPixels(p, null)

                // 8dp radio aprox
                val radiusPx = with(density) { 8.dp.toPx() }
                canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), radiusPx, paintFill)
                canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), radiusPx, paintStroke)
            }
        }
    }

    // --- Icono azul para mi ubicación (Marker) ---
    val myLocationIcon: Drawable? = remember {
        // Icono propio de osmdroid (azul)
        ContextCompat.getDrawable(context, OsmDroidR.drawable.marker_default)
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    isTilesScaledToDpi = true

                    // Evitar vista global al arrancar: usamos un fallback hasta tener ubicación.
                    controller.setZoom(startupZoom)
                    controller.setCenter(startupCenter)

                    // Cuando está fijado, no queremos que el usuario mueva el mapa.
                    // Dejamos multiTouch y bloqueamos gestos con un overlay Compose encima.

                    // Marcadores fijos una sola vez
                    nireKokapenak.forEach { kokapena ->
                        Marker(this).apply {
                            position = GeoPoint(kokapena.latitudea, kokapena.longitudea)
                            title = kokapena.izena
                            snippet = kokapena.deskribapena
                            overlays.add(this)
                        }
                    }

                    overlays.add(myDotOverlay)
                    mapViewRef.value = this
                }
            },
            update = { mapView ->
                val point = myLocation
                if (hasLocationPermission && point != null) {
                    val marker = myMarkerRef.value ?: Marker(mapView).also { created ->
                        created.title = "Mi ubicación"
                        created.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        created.icon = myLocationIcon
                        myMarkerRef.value = created
                        mapView.overlays.add(created)
                    }
                    marker.position = point
                    marker.icon = myLocationIcon
                }

                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        // NUEVO: cuando está fijado, ponemos una capa transparente que consume gestos,
        // así no puede arrastrar/zoomear el mapa.
        if (followMyLocation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            // Consumir eventos hasta levantar el dedo
                            do {
                                awaitPointerEvent()
                            } while (true)
                        }
                    }
            )
        }

        // Botón para fijar/desfijar el seguimiento
        FloatingActionButton(
            onClick = { followMyLocation = !followMyLocation },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(if (followMyLocation) "Fijado" else "Libre")
        }

        // Mensaje si no hay permisos o todavía no hay fix
        if (!hasLocationPermission) {
            Text(
                text = "Activa permisos de ubicación",
                modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else if (myLocation == null) {
            Text(
                text = "Buscando tu ubicación…",
                modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
