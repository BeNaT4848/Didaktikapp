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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.res.stringResource
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

/**
 * OSM mapa pantaila nagusia konposatzen du (GPS nabigazioa)
 * @param navController Nabigazio kontrolatzailea
 */
@Composable
fun MapaOsmScreen(navController: NavController) {
    AppScaffold(navController = navController) {
        // Rail soilik UIrako (ez du nabigatzen)
        var railSelectedIndex by rememberSaveable { mutableStateOf(0) }

        // Berria: kolapsoa/zabaltzea Gmail estilokoa
        var railExpanded by rememberSaveable { mutableStateOf(false) }

        // Kontraste handiko koloreak indartzen ditugu gaia/alpha baztertzeko
        val railContainer = MaterialTheme.colorScheme.surfaceVariant
        val railSelected = MaterialTheme.colorScheme.primary
        val railUnselected = MaterialTheme.colorScheme.onSurfaceVariant
        val railIndicator = MaterialTheme.colorScheme.secondaryContainer

        // Kolapso moduan pixka bat estutuago
        val targetRailWidth = if (railExpanded) 112.dp else 64.dp
        val railWidth by animateDpAsState(
            targetValue = targetRailWidth,
            animationSpec = tween(durationMillis = 180),
            label = "railWidth"
        )

        // Etiketak: animazio arina (alpha soilik) berri osatu/kostu handiak saihesteko
        val labelAlpha by animateFloatAsState(
            targetValue = if (railExpanded) 1f else 0f,
            animationSpec = tween(durationMillis = 140),
            label = "railLabelAlpha"
        )

        // Swipe gestua: atalasea px-tan (dentsitateari egokitua)
        val density = LocalDensity.current
        val swipeThresholdPx = with(density) { 32.dp.toPx() }

        // --- Swipe (erreserba bateragarria): Android geruza swipe atzetik detektatzeko ---
        // Garrantzitsua: ez du pointerInput-en menpe (zure konfigurazioan Unresolved reference ematen ari da)
        val swipeDetectorWidth = 24.dp

        val context = LocalContext.current

        // Gorde/berreskuratu erabiltzaile aktiboa (Login-ek eguneratzen du)
        val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
        val activeUserName = sessionPrefs.getString("active_user_name", null)

        // Aurrerapen errepikaria (erabiltzaile bakoitzeko)
        val progressRepo = remember(activeUserName) {
            KokapenaProgressRepository(context, activeUserName ?: "default")
        }

        // Bir konposaketa behartzen du aurrerapena aldatzean (joko batetik itzultzean)
        var unlockedIndex by rememberSaveable { mutableStateOf(progressRepo.getUnlockedStepIndex()) }

        LaunchedEffect(activeUserName) {
            // GPS-n sartzean edo erabiltzailea aldatzean aurrerapena freskatu
            unlockedIndex = progressRepo.getUnlockedStepIndex()
        }

        // Prefs berrirakurri lehenengo planoan (jokatu ondoren)
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

        // ** BERRIA: hautatutako markadorearentzako egoera **
        var selectedKokapena by remember { mutableStateOf<Kokapena?>(null) }

        Box(modifier = Modifier.fillMaxSize()) {
            // Swipe detektagailua ezkerreko ertzean (rail-a irekitzeko)
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
                            // Irekita badago, detektagailu honek ez du ezer egiten
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

                                    // Gehienbat horizontala den gestua bakarrik
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
                                        // Ez da klik bat, baina irisgarritasuna arazorik ez sortzeko
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

            // Swipe detektagailua rail gainean irekita dagoenean (ezkerrera swipe egitean ixten da)
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

                            NavigationRailItem(
                                selected = false,
                                onClick = { railExpanded = !railExpanded },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = stringResource(R.string.nav_menu),
                                        tint = railUnselected
                                    )
                                },

                                label = {
                                    Text(
                                        stringResource(R.string.nav_menu),
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
                                onClick = {
                                    navController.navigate(Routes.CHAT_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Send,
                                        stringResource(R.string.nav_chat),
                                        tint = if (railSelectedIndex == 0) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(R.string.nav_chat),
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            NavigationRailItem(
                                selected = railSelectedIndex == 1,
                                onClick = {
                                    navController.navigate(Routes.AJUSTES_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Settings,
                                        stringResource(R.string.nav_settings),
                                        tint = if (railSelectedIndex == 1) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(R.string.nav_settings),
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            NavigationRailItem(
                                selected = railSelectedIndex == 2,
                                onClick = {
                                    navController.navigate(Routes.RANKIN_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.Star,
                                        stringResource(R.string.nav_ranking),
                                        tint = if (railSelectedIndex == 2) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(R.string.nav_ranking),
                                        modifier = Modifier.graphicsLayer { alpha = labelAlpha })
                                },
                                alwaysShowLabel = true,
                                colors = itemColors
                            )

                            Spacer(modifier = Modifier.weight(1f, fill = true))

                            NavigationRailItem(
                                selected = railSelectedIndex == 3,
                                onClick = {
                                    navController.navigate(Routes.LOGIN_SCREEN)
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.logout),
                                        contentDescription = stringResource(R.string.nav_logout),
                                        tint = if (railSelectedIndex == 3) railSelected else railUnselected
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(R.string.nav_logout),
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

                            if (railExpanded) railExpanded = false
                        }
                    )
                }
            }


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


        }


    }
}


/**
 * Kontratua:
 * - Markadore finkoak + zure kokapen denbora errealean erakusten ditu
 * - Botoia jarraipena finkatzeko/askatzeko (etengabe zentratua)
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

    // Aurrerapena erabiltzaile bakoitzeko (MapaOsmScreen-ko erabiltzaile aktibo bera)
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val activeUserName = sessionPrefs.getString("active_user_name", null)

    val progressRepo = remember(activeUserName) {
        KokapenaProgressRepository(context, activeUserName ?: "default")
    }

    // OSMdroid-en konfigurazio minimo gomendatua
    // Garrantzitsua zerbitzariak eskaerak onartzeko (zuretako laukizurik gabe/gauza bitxiak saihesteko)
    SideEffect {
        try {
            Configuration.getInstance().userAgentValue = context.packageName
        } catch (_: Throwable) {
            // Salbuespena barkatu
        }
    }

    // --- Baimenak + kokapen egoera ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Kokapena irekitzeko gehienezko distantzia (metro)
    val unlockRadiusMeters = 100f

    /**
     * Bi punturen arteko distantzia kalkulatzen du metroetan
     * @param from Hasierako puntua
     * @param toLat Helburu latitudea
     * @param toLon Helburu longitudea
     * @return Distantzia metroetan
     */
    fun distanceMeters(from: GeoPoint, toLat: Double, toLon: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(from.latitude, from.longitude, toLat, toLon, results)
        return results[0]
    }

    /**
     * Kokapena hurbilegi dagoen egiaztatzen du
     * @param k Egiaztatu beharreko kokapena
     * @return Hurbilegi dagoen ala ez
     */
    fun isNearEnough(k: Kokapena): Boolean {
        val here = myLocation ?: return false
        return distanceMeters(here, k.latitudea, k.longitudea) <= unlockRadiusMeters
    }

    // Egia bada: mapa kokapen eguneraketa bakoitzean zentratzen da
    var followMyLocation by rememberSaveable { mutableStateOf(true) }

    // Hasierako zoom txikiagoa (lehenengo finkapena lortzen denean)
    // 18-19 da oreka ona kaleak ikusteko exageratu gabe
    val initialZoom = 18.5

    // Fallback "mundua" ikusi ez dadin lehenengo finkapenaren zain
    // (center/zoom hasierarik ez bada, osmdroid-ek ikuspegi globalean has lezake)
    val startupCenter = GeoPoint(43.3129, -1.9018)
    val startupZoom = 14.0

    // BERRIA: mapa "zure kokapenean" hasi dadin puntu finko batean baino
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


        try {
            val last = fused.lastLocation
            last.addOnSuccessListener { loc ->
                if (loc != null && !hasInitialCentered) {
                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                }
            }
        } catch (_: SecurityException) {
        }


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


    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose { }
        } else {
            val fused = LocationServices.getFusedLocationProviderClient(context)

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

    // --- MapView + overlays: EZ garbitu overlays berri osaketa bakoitzean ---
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val myMarkerRef = remember { mutableStateOf<Marker?>(null) }

    // BERRIA: kokapena aldatzean, birkarratzea behartu eta zentratu
    // - Lehen finkapena: zentratu + zoom indartsua aplikatu eta hasInitialCentered markatu
    // - Ondoren: finkatuta badago, jarraitu zentratzen
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

    // --- Util: Drawable bat sortu tamaina finko batean dp-tan eskalatuta ---
    /**
     * Drawable bat sortzen du baliabide batetik dp tamaina jakin batera eskalatuta
     * @param resId Balibalido baliabidearen IDa
     * @param sizeDp Helburu tamaina dp-tan
     * @return Eskalatutako Drawable-a edo null
     */
    fun scaledDrawable(resId: Int, sizeDp: Dp): Drawable? {
        val base = ContextCompat.getDrawable(context, resId) ?: return null
        val sizePx = with(density) { sizeDp.roundToPx() }.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        base.setBounds(0, 0, sizePx, sizePx)
        base.draw(canvas)
        return BitmapDrawable(context.resources, bitmap)
    }


    // BERRIA: drawable eskalatu alpha-rekin (efektu "itzalita" sortzeko)
    /**
     * Drawable bat sortzen du alpha zehaztuarekin
     * @param resId Balibalido baliabidearen IDa
     * @param sizeDp Helburu tamaina dp-tan
     * @param alpha Alpha balioa (0-255)
     * @return Eskalatutako Drawable-a alpha-rekin edo null
     */
    fun scaledDrawableWithAlpha(resId: Int, sizeDp: Dp, alpha: Int): Drawable? {
        val base = ContextCompat.getDrawable(context, resId)?.mutate() ?: return null
        base.alpha = alpha.coerceIn(0, 255)
        val sizePx = with(density) { sizeDp.roundToPx() }.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        base.setBounds(0, 0, sizePx, sizePx)
        base.draw(canvas)
        return BitmapDrawable(context.resources, bitmap)
    }

    // Ikono tamainak
    // Kokapenek nire kokapenaren tamaina bera izan dezan nahi dugu
    // Nire kokapena txikiagoa da mapa ez estaltzeko
    val myLocationIconSize = 48.dp
    // Kokapenak hasierako moduan utzi (handiak)
    val kokapenaIconSize = 84.dp

    // Lehenetsitako ikonoa kokapenentzat
    val kokapenaIconDefault = remember { scaledDrawable(R.drawable.ubinegra, kokapenaIconSize) }

    // Hautatzean (sakatzean) ikonoa
    val kokapenaIconSelected = remember { scaledDrawable(R.drawable.ubinlanca, kokapenaIconSize) }

    // BERRIA: ikono "itzalita" blokeatutako kokapenentzat
    // (berdin baliabidea erabiltzen dugu, baina alpha txikiagoarekin)
    val kokapenaIconLocked = remember {
        scaledDrawableWithAlpha(R.drawable.ubinegra, kokapenaIconSize, alpha = 110)
    }

    // Gorde zein marker dago hautatuta toggle-a egiteko
    val selectedKokapenaMarker = remember { mutableStateOf<Marker?>(null) }

    // --- Nire kokapenarentzat ikonoa (Marker) ---
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

                    // Gailu batzuetan, tile-ak DPI-ra eskalatzeak lausotuta/pixelduta ikusten dira
                    // Hobe tamaina naturalean uztea
                    isTilesScaledToDpi = false

                    // Lehenetsita tile gehiago kargatu "pixel" efektua saihesteko zoom iristen den bitartean
                    // (Aukerakoa, baina mugitze/zoom egitean garbitasuna hobetzen du)
                    setUseDataConnection(true)

                    controller.setZoom(startupZoom)
                    controller.setCenter(startupCenter)

                    // Markadore kokapenak
                    nireKokapenak.forEach { kokapena ->
                        val canOpenInitial = progressRepo.isRouteUnlocked(kokapena.route) && isNearEnough(kokapena)

                        val m = Marker(this).apply {
                            position = GeoPoint(kokapena.latitudea, kokapena.longitudea)
                            title = kokapena.izena
                            snippet = kokapena.deskribapena
                            icon = if (canOpenInitial) kokapenaIconDefault else kokapenaIconLocked
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setInfoWindowAnchor(Marker.ANCHOR_CENTER, 0.30f)
                        }

                        // Hautaketa toggle-a + InfoWindow erakutsi
                        m.setOnMarkerClickListener { marker, mapView ->
                            val canOpenNow = progressRepo.isRouteCurrentOrSecondary(kokapena.route) && isNearEnough(kokapena)
                            if (!canOpenNow) {
                                mapView?.invalidate()
                                return@setOnMarkerClickListener true
                            }

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
                                onKokapenaClick(kokapena)
                            }

                            mapView?.invalidate()
                            false
                        }

                        overlays.add(m)
                    }

                    mapViewRef.value = this
                }
            },
            update = { mapView ->
                // --- Mapa gestu erreala jakinarazi (pan/zoom) rail automatikoki ixteko ---
                // Hau ez da tap-etan jaurti behar (markadore hautaketa haustu gabe)
                var gestureNotified = false
                val gestureSlop = ViewConfiguration.get(mapView.context).scaledTouchSlop

                // --- Gestuen blokeoa "Finkatuta" dagoenean ---
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

                // --- Nire kokapen eguneraketa ---
                val point = myLocation
                if (hasLocationPermission && point != null) {
                    val marker = myMarkerRef.value ?: Marker(mapView).also { created ->
                        created.title = "Mi ubicación"
                        created.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        // InfoWindow koherentea mantendu (egunean batean erakutsi behar bada)
                        created.setInfoWindowAnchor(Marker.ANCHOR_CENTER, 0.30f)
                        myMarkerRef.value = created
                        mapView.overlays.add(created)
                    }

                    marker.position = point
                    // Ikonoa beti berriro aplikatu tamaina/drawable aldatu balitz
                    marker.icon = myLocationIcon
                }

                // Kokapen ikonoak eguneratu hurbiltasun/desblokeoaren arabera (kokapena aldatzen bada)
                mapView.overlays.filterIsInstance<Marker>().forEach { marker ->
                    if (marker.title == "Mi ubicación") return@forEach

                    val pos = marker.position ?: return@forEach
                    val k = nireKokapenak.firstOrNull {
                        it.latitudea == pos.latitude && it.longitudea == pos.longitude
                    } ?: return@forEach

                    val canOpen = progressRepo.isRouteCurrentOrSecondary(k.route) && isNearEnough(k)
                    val isSelected = selectedKokapenaMarker.value == marker

                    marker.icon = when {
                        isSelected && canOpen -> kokapenaIconSelected
                        canOpen -> kokapenaIconDefault
                        else -> kokapenaIconLocked
                    }
                }

                mapView.invalidate()
            }, modifier = Modifier.fillMaxSize()
        )


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


        FloatingActionButton(
            onClick = { followMyLocation = !followMyLocation },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = stringResource(
                    if (followMyLocation) R.string.gps_follow_fixed else R.string.gps_follow_free
                )
            )
        }


        if (!hasLocationPermission) {
            Text(
                text = stringResource(R.string.gps_permission_needed),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else if (myLocation == null) {
            Text(
                text = stringResource(R.string.gps_locating),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
