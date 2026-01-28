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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.Kokapena
import com.example.errenteriaapp.classes.nireKokapenak
import com.example.errenteriaapp.components.AppScaffold
import com.example.errenteriaapp.components.KokapenaAzalpen
import com.example.errenteriaapp.components.ReusableModalBottomSheet
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.progress.KokapenaProgressRepository
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
import kotlin.math.abs

@Composable
fun MapaOsmScreen(navController: NavController) {
    AppScaffold(navController = navController) {
        // Rail solo de UI (no navega a ningún sitio)
        var railSelectedIndex by rememberSaveable { mutableStateOf(0) }

        // Nuevo: colapsado/expandido tipo Gmail
        var railExpanded by rememberSaveable { mutableStateOf(false) }

        // Forzamos colores muy contrastados para descartar tema/alpha.
        val railContainer = MaterialTheme.colorScheme.surfaceVariant
        val railSelected = MaterialTheme.colorScheme.primary
        val railUnselected = MaterialTheme.colorScheme.onSurfaceVariant
        val railIndicator = MaterialTheme.colorScheme.secondaryContainer

        // Un poco más estrecho en modo colapsado
        val targetRailWidth = if (railExpanded) 112.dp else 64.dp
        val railWidth by animateDpAsState(
            targetValue = targetRailWidth,
            animationSpec = tween(durationMillis = 180),
            label = "railWidth"
        )

        // Labels: animación ligera (solo alpha) para evitar recompos/layout caros
        val labelAlpha by animateFloatAsState(
            targetValue = if (railExpanded) 1f else 0f,
            animationSpec = tween(durationMillis = 140),
            label = "railLabelAlpha"
        )

        // Swipe gesture: umbral en px (ajustado a densidad)
        val density = LocalDensity.current
        val swipeThresholdPx = with(density) { 32.dp.toPx() }

        // --- Swipe (fallback compatible): capa Android para detectar swipe desde el borde ---
        // Importante: no depende de pointerInput (que en tu setup está dando Unresolved reference).
        val swipeDetectorWidth = 24.dp

        val context = LocalContext.current
        // Repositorio de progreso (persistente)
        val progressRepo = remember { KokapenaProgressRepository(context) }

        // Fuerza recomposición cuando cambia el progreso (al volver desde un juego)
        var unlockedIndex by rememberSaveable { mutableStateOf(progressRepo.getUnlockedStepIndex()) }

        // Releer prefs al volver a primer plano (después de jugar)
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    unlockedIndex = progressRepo.getUnlockedStepIndex()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        // DEBUG: muestra qué paso está desbloqueado
        val unlockedRouteDebug = progressRepo.tourSteps.getOrNull(unlockedIndex) ?: "(none)"

        // ** NUEVO: estado para el marcador seleccionado **
        var selectedKokapena by remember { mutableStateOf<Kokapena?>(null) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Detector de swipe en el borde izquierdo (abre el rail)
            AndroidView(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(swipeDetectorWidth)
                    .zIndex(3f),
                factory = { ctx ->
                    View(ctx).apply {
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        isClickable = true

                        val touchSlop = ViewConfiguration.get(ctx).scaledTouchSlop
                        val thresholdPx = swipeThresholdPx

                        var downX = 0f
                        var downY = 0f
                        var sumDx = 0f
                        var tracking = false

                        setOnTouchListener { v, event ->
                            // Si ya está abierto, este detector no hace nada.
                            if (railExpanded) return@setOnTouchListener false

                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    downX = event.x
                                    downY = event.y
                                    sumDx = 0f
                                    tracking = true
                                    true
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    if (!tracking) return@setOnTouchListener false
                                    val dx = event.x - downX
                                    val dy = event.y - downY

                                    // Solo nos interesa gesto principalmente horizontal.
                                    if (abs(dx) > touchSlop && abs(dx) > abs(dy) * 1.2f) {
                                        sumDx = dx
                                    }
                                    true
                                }

                                MotionEvent.ACTION_UP -> {
                                    if (!tracking) return@setOnTouchListener false
                                    tracking = false

                                    if (sumDx > thresholdPx) {
                                        railExpanded = true
                                        // No es un click, pero para accesibilidad evitamos warning.
                                        v?.performClick()
                                    }
                                    true
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    tracking = false
                                    true
                                }

                                else -> false
                            }
                        }
                    }
                }
            )

            // Detector de swipe sobre el rail cuando está expandido (cierra al swipar a la izquierda)
            if (railExpanded) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(railWidth)
                        .zIndex(3f),
                    factory = { ctx ->
                        View(ctx).apply {
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)

                            val touchSlop = ViewConfiguration.get(ctx).scaledTouchSlop
                            val thresholdPx = swipeThresholdPx

                            var downX = 0f
                            var downY = 0f
                            var sumDx = 0f
                            var tracking = false
                            var moved = false

                            setOnTouchListener { v, event ->
                                when (event.actionMasked) {
                                    MotionEvent.ACTION_DOWN -> {
                                        downX = event.x
                                        downY = event.y
                                        sumDx = 0f
                                        moved = false
                                        tracking = true
                                        // No consumimos: dejamos que el rail reciba taps si finalmente no hay swipe.
                                        false
                                    }

                                    MotionEvent.ACTION_MOVE -> {
                                        if (!tracking) return@setOnTouchListener false
                                        val dx = event.x - downX
                                        val dy = event.y - downY

                                        if (abs(dx) > touchSlop && abs(dx) > abs(dy) * 1.2f) {
                                            sumDx = dx
                                            moved = true
                                        }

                                        // Si estamos swipando horizontal, consumimos para evitar clicks raros.
                                        moved
                                    }

                                    MotionEvent.ACTION_UP -> {
                                        if (!tracking) return@setOnTouchListener false
                                        tracking = false

                                        if (sumDx < -thresholdPx) {
                                            railExpanded = false
                                            v?.performClick()
                                            true
                                        } else {
                                            false
                                        }
                                    }

                                    MotionEvent.ACTION_CANCEL -> {
                                        tracking = false
                                        false
                                    }

                                    else -> false
                                }
                            }
                        }
                    }
                )
            }

            // Contenido normal
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = railContainer,
                    tonalElevation = 2.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(railWidth)
                        .zIndex(2f)
                        .clip(RoundedCornerShape(0.dp))
                ) {
                    CompositionLocalProvider(LocalContentColor provides railUnselected) {
                        NavigationRail(
                            containerColor = railContainer,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 8.dp)
                        ) {
                            // Botón superior para expandir/colapsar
                            NavigationRailItem(
                                selected = false,
                                onClick = { railExpanded = !railExpanded },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Expandir/colapsar",
                                        tint = railUnselected
                                    )
                                },
                                // Siempre ponemos label, pero lo hacemos invisible con alpha cuando está colapsado.
                                label = {
                                    Text(
                                        "Menú",
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = railUnselected,
                                    selectedTextColor = railUnselected,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = railUnselected,
                                    unselectedTextColor = railUnselected,
                                )
                            )

                            // Más separación para que el header no quede pegado
                            Spacer(modifier = Modifier.padding(top = 12.dp))

                            val itemColors = NavigationRailItemDefaults.colors(
                                selectedIconColor = railSelected,
                                selectedTextColor = railSelected,
                                indicatorColor = railIndicator,
                                unselectedIconColor = railUnselected,
                                unselectedTextColor = railUnselected,
                            )

                            NavigationRailItem(
                                selected = railSelectedIndex == 0,
                                onClick = { railSelectedIndex = 0 },
                                icon = {
                                    Icon(
                                        Icons.Default.Home,
                                        "Inicio",
                                        tint = if (railSelectedIndex == 0) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        "Inicio",
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            NavigationRailItem(
                                selected = railSelectedIndex == 1,
                                onClick = { railSelectedIndex = 1 },
                                icon = {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        "Ubicación",
                                        tint = if (railSelectedIndex == 1) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        "GPS",
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            NavigationRailItem(
                                selected = railSelectedIndex == 2,
                                onClick = {
                                    navController.navigate(Routes.AJUSTES_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Settings,
                                        "Ajustes",
                                        tint = if (railSelectedIndex == 2) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        "Ajustes",
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            NavigationRailItem(
                                selected = railSelectedIndex == 3,
                                onClick = {
                                    navController.navigate(Routes.RANKIN_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Star,
                                        "Ranking",
                                        tint = if (railSelectedIndex == 3) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        "Ranking",
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            Spacer(modifier = Modifier.weight(1f, fill = true))

                            NavigationRailItem(
                                selected = railSelectedIndex == 4,
                                onClick = {
                                    navController.navigate(Routes.LOGIN_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.logout),
                                        contentDescription = "Saioa Itxi",
                                        tint = if (railSelectedIndex == 4) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        "Saioa Itxi",
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )
                        }
                    }
                }

                // Separador
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .zIndex(2f)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .zIndex(1f)
                ) {
                    // (sin variable dummy)
                    OsmMapView(
                        nireKokapenak = nireKokapenak,
                        modifier = Modifier.fillMaxSize(),
                        onKokapenaClick = { kokapena ->
                            if (progressRepo.isRouteUnlocked(kokapena.route)) {
                                selectedKokapena = kokapena
                            }
                        },
                        onUserMapGestureStart = {
                            // Si el usuario empieza a mover/zoomear el mapa y el rail está abierto, lo cerramos.
                            if (railExpanded) railExpanded = false
                        }
                    )
                }
            }

            // ** NUEVO: modal para mostrar KokapenaAzalpen **
            if (selectedKokapena != null) {
                val currentKokapena = selectedKokapena
                ReusableModalBottomSheet(
                    onDismiss = { selectedKokapena = null },
                    sheetContent = { onClose ->
                        KokapenaAzalpen(
                            kokapena = currentKokapena!!,
                            navController = navController,
                            onClose = {
                                selectedKokapena = null
                                onClose()
                            },
                            onNavigateToGame = { route ->
                                selectedKokapena = null
                                onClose()
                                navController.navigate(route)
                            }
                        )
                    }
                ) { openSheet ->
                    LaunchedEffect(Unit) { openSheet() }
                }
            }

            // DEBUG badge arriba (temporal)
            Text(
                text = "DEBUG paso=${unlockedIndex + 1}/${progressRepo.tourSteps.size} -> $unlockedRouteDebug",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
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
fun OsmMapView(
    nireKokapenak: List<Kokapena>,
    modifier: Modifier = Modifier,
    onKokapenaClick: (Kokapena) -> Unit = {},
    onUserMapGestureStart: () -> Unit = {}
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Configuración mínima recomendada de osmdroid.
    // Importante para que el servidor acepte requests (evita tiles en blanco / raros en algunos casos).
    SideEffect {
        try {
            Configuration.getInstance().userAgentValue = context.packageName
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
            (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) || (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
    }

    DisposableEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
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
                Priority.PRIORITY_HIGH_ACCURACY, 1500L
            ).setMinUpdateIntervalMillis(800L).build()

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
    fun scaledDrawable(resId: Int, sizeDp: Dp): Drawable? {
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
    // Mi ubicación más pequeño para que no tape el mapa.
    val myLocationIconSize = 48.dp
    // Mantén los kokapena como estaban (grandes).
    val kokapenaIconSize = 84.dp

    // Icono por defecto para kokapenak
    val kokapenaIconDefault = remember { scaledDrawable(R.drawable.ubinegra, kokapenaIconSize) }

    // Icono al seleccionar (al pulsar)
    val kokapenaIconSelected = remember { scaledDrawable(R.drawable.ubinlanca, kokapenaIconSize) }

    // Guardamos qué marker está seleccionado para hacer toggle
    val selectedKokapenaMarker = remember { mutableStateOf<Marker?>(null) }

    // --- Icono para mi ubicación (Marker) ---
    val myLocationIcon: Drawable? = remember {
        val base = ContextCompat.getDrawable(context, R.drawable.ic_my_location_person)
            ?: return@remember null

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

                    // Marcadores kokapenak
                    nireKokapenak.forEach { kokapena ->
                        val m = Marker(this).apply {
                            position = GeoPoint(kokapena.latitudea, kokapena.longitudea)
                            title = kokapena.izena
                            snippet = kokapena.deskribapena
                            icon = kokapenaIconDefault
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
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
                                marker.icon = kokapenaIconDefault
                                marker.closeInfoWindow()
                                selectedKokapenaMarker.value = null
                            } else {
                                marker.icon = kokapenaIconSelected
                                marker.showInfoWindow()
                                selectedKokapenaMarker.value = marker
                                // ** NUEVO: callback para modal **
                                onKokapenaClick(kokapena)
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
                // --- Notificar gesto real de mapa (pan/zoom) para autocerrar el rail ---
                // Esto no debe dispararse en taps (para no romper selección de marcadores).
                var gestureNotified = false
                val gestureSlop = ViewConfiguration.get(mapView.context).scaledTouchSlop

                // --- Bloqueo de gestos cuando está "Fijado" ---
                if (followMyLocation) {
                    mapView.setMultiTouchControls(false)
                    mapView.setOnTouchListener(object : View.OnTouchListener {
                        private var downX = 0f
                        private var downY = 0f
                        private var moved = false

                        override fun onTouch(v: View?, event: MotionEvent): Boolean {
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    downX = event.x
                                    downY = event.y
                                    moved = false
                                    gestureNotified = false
                                    return false
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val dx = abs(event.x - downX)
                                    val dy = abs(event.y - downY)
                                    if ((dx > gestureSlop || dy > gestureSlop || event.pointerCount > 1)) {
                                        moved = true
                                        if (!gestureNotified) {
                                            gestureNotified = true
                                            onUserMapGestureStart()
                                        }
                                    }
                                    return moved
                                }

                                MotionEvent.ACTION_UP -> {
                                    // Si NO se movió, es un tap -> dejamos que osmdroid lo procese.
                                    if (!moved) v?.performClick()
                                    gestureNotified = false
                                    return moved
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    gestureNotified = false
                                    return moved
                                }

                                else -> return moved
                            }
                        }
                    })
                } else {
                    // Libre: dejamos gestos del mapa, pero detectamos cuando realmente empieza un pan/zoom.
                    mapView.setMultiTouchControls(true)
                    mapView.setOnTouchListener(object : View.OnTouchListener {
                        private var downX = 0f
                        private var downY = 0f
                        private var moved = false

                        override fun onTouch(v: View?, event: MotionEvent): Boolean {
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    downX = event.x
                                    downY = event.y
                                    moved = false
                                    gestureNotified = false
                                    return false
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val dx = abs(event.x - downX)
                                    val dy = abs(event.y - downY)
                                    if (!moved && (dx > gestureSlop || dy > gestureSlop || event.pointerCount > 1)) {
                                        moved = true
                                        if (!gestureNotified) {
                                            gestureNotified = true
                                            onUserMapGestureStart()
                                        }
                                    }
                                    return false
                                }

                                MotionEvent.ACTION_UP -> {
                                    // Tap (sin mover) -> accesibilidad
                                    if (!moved) v?.performClick()
                                    gestureNotified = false
                                    return false
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    gestureNotified = false
                                    return false
                                }

                                else -> return false
                            }
                        }
                    })
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
            }, modifier = Modifier.fillMaxSize()
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
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else if (myLocation == null) {
            Text(
                text = "Buscando tu ubicación…",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
