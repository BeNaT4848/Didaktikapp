package com.example.errenteriaapp.navigation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.Kokapena
import com.example.errenteriaapp.classes.nireKokapenak
import com.example.errenteriaapp.components.AppScaffold
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    val lifecycleOwner = LocalLifecycleOwner.current

    // Configuración mínima recomendada de osmdroid.
    // Importante para que el servidor acepte requests (evita tiles en blanco / raros en algunos casos).
    SideEffect {
        try {
            org.osmdroid.config.Configuration.getInstance().userAgentValue = context.packageName
        } catch (_: Throwable) {

        }
    }

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
    val startupCenter = GeoPoint(43.3129, -1.9018)
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

    // --- Util: crear un Drawable escalado a un tamaño fijo en dp ---
    fun scaledDrawable(resId: Int, sizeDp: androidx.compose.ui.unit.Dp): Drawable? {
        val base = ContextCompat.getDrawable(context, resId) ?: return null
        val sizePx = with(density) { sizeDp.roundToPx() }.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        base.setBounds(0, 0, sizePx, sizePx)
        base.draw(canvas)
        return BitmapDrawable(context.resources, bitmap)
    }

    // Tamaños de iconos
    // Queremos que los kokapenak tengan el mismo tamaño que el de mi ubicación.
    val myLocationIconSize = 84.dp
    val kokapenaIconSize = myLocationIconSize

    // Icono por defecto para kokapenak
    val kokapenaIconDefault = remember { scaledDrawable(R.drawable.ubinegra, kokapenaIconSize) }

    // Icono al seleccionar (al pulsar)
    val kokapenaIconSelected = remember { scaledDrawable(R.drawable.ubinlanca, kokapenaIconSize) }

    // Guardamos qué marker está seleccionado para hacer toggle
    val selectedKokapenaMarker = remember { mutableStateOf<Marker?>(null) }

    // --- Icono para mi ubicación (Marker) ---
    val myLocationIcon: Drawable? = remember {
        val base = ContextCompat.getDrawable(context, R.drawable.ubinegra) ?: return@remember null

        val sizeDp = myLocationIconSize
        val sizePx = with(density) { sizeDp.roundToPx() }.coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        base.setBounds(0, 0, sizePx, sizePx)
        base.draw(canvas)

        BitmapDrawable(context.resources, bitmap)
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    // En algunos dispositivos, escalar tiles a DPI hace que se vean borrosos/pixelados.
                    // Mejor dejarlos a tamaño nativo.
                    isTilesScaledToDpi = false

                    // Cargar más tiles por defecto para evitar el efecto “pixelado” mientras llega el zoom.
                    // (Opcional, pero suele mejorar la nitidez al mover/zoomear)
                    setUseDataConnection(true)

                    controller.setZoom(startupZoom)
                    controller.setCenter(startupCenter)

                    // Marcadores fijos (kokapenak) una sola vez
                    nireKokapenak.forEach { kokapena ->
                        val m = Marker(this).apply {
                            position = GeoPoint(kokapena.latitudea, kokapena.longitudea)
                            title = kokapena.izena
                            snippet = kokapena.deskribapena
                            icon = kokapenaIconDefault

                            // Anclar el icono abajo-centro
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            // Acerca la InfoWindow al marker.
                            // Si se ve demasiado abajo, baja Y.
                            setInfoWindowAnchor(Marker.ANCHOR_CENTER, 0.30f)
                        }

                        // Toggle de selección + mostrar InfoWindow
                        m.setOnMarkerClickListener { marker, mapView ->
                            val prev = selectedKokapenaMarker.value

                            if (prev != null && prev != marker) {
                                prev.icon = kokapenaIconDefault
                                prev.closeInfoWindow()
                            }

                            if (prev == marker) {
                                // Des-seleccionar
                                marker.icon = kokapenaIconDefault
                                marker.closeInfoWindow()
                                selectedKokapenaMarker.value = null
                            } else {
                                // Seleccionar
                                marker.icon = kokapenaIconSelected
                                marker.showInfoWindow()
                                selectedKokapenaMarker.value = marker
                            }

                            mapView?.invalidate()

                            // false = dejamos que osmdroid procese el tap (mejor compatibilidad)
                            false
                        }

                        overlays.add(m)
                    }

                    mapViewRef.value = this
                }
            },
            update = { mapView ->
                // --- Bloqueo de gestos cuando está "Fijado" ---
                // Queremos permitir TAPs sobre marcadores, pero bloquear arrastre/zoom.
                if (followMyLocation) {
                    mapView.setMultiTouchControls(false)
                    mapView.setOnTouchListener(object : View.OnTouchListener {
                        private var downX = 0f
                        private var downY = 0f
                        private var moved = false
                        private val slop = ViewConfiguration.get(mapView.context).scaledTouchSlop

                        override fun onTouch(v: View?, event: MotionEvent): Boolean {
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    downX = event.x
                                    downY = event.y
                                    moved = false
                                    // No consumimos: dejamos que osmdroid gestione el down
                                    return false
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val dx = kotlin.math.abs(event.x - downX)
                                    val dy = kotlin.math.abs(event.y - downY)
                                    if (dx > slop || dy > slop || event.pointerCount > 1) {
                                        moved = true
                                    }
                                    // Si hay movimiento o multitouch, bloqueamos
                                    return moved
                                }

                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                    // Si NO se movió, es un tap -> no consumimos para que pueda clickar marcadores
                                    // Si se movió, consumimos para evitar "fling"/pan
                                    return moved
                                }

                                else -> return moved
                            }
                        }
                    })
                } else {
                    mapView.setOnTouchListener(null)
                    mapView.setMultiTouchControls(true)
                }

                // --- Actualización de mi ubicación ---
                val point = myLocation
                if (hasLocationPermission && point != null) {
                    val marker = myMarkerRef.value ?: Marker(mapView).also { created ->
                        created.title = "Mi ubicación"
                        created.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        // Mantener una InfoWindow coherente (si algún día se muestra)
                        created.setInfoWindowAnchor(Marker.ANCHOR_CENTER, 0.30f)
                        myMarkerRef.value = created
                        mapView.overlays.add(created)
                    }

                    marker.position = point
                    // Re-aplicar icono siempre por si cambia el tamaño/drawable
                    marker.icon = myLocationIcon
                }

                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )

        // Mantener ciclo de vida correcto del MapView para que recargue tiles bien.
        DisposableEffect(lifecycleOwner) {
            val mapView = mapViewRef.value
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView?.onPause()
            }
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
