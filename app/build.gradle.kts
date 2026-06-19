import java.util.Properties

/**
 * Gradle build script-a.
 * Android aplikazioaren konfigurazioa eta menpekotasunak definitzen ditu.
 */
plugins {
    alias(libs.plugins.android.application) // Android aplikazio plugin-a
    alias(libs.plugins.kotlin.android)      // Kotlin Android plugin-a
    alias(libs.plugins.kotlin.compose)      // Kotlin Compose plugin-a
    id ("kotlin-kapt")                      // Kotlin kode prozesadorea
}

/**
 * Android aplikazioaren konfigurazioa.
 */
android {
    namespace = "com.example.errenteriaapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.errenteriaapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.properties fitxegitik GROQ API giltza irakurri
        val localProps = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        val groqKey =
            localProps.getProperty("GROQ_API_KEY")
                ?: (project.findProperty("GROQ_API_KEY") as String?)
                ?: System.getenv("GROQ_API_KEY")
                ?: ""
        buildConfigField("String", "GROQ_API_KEY", "\"$groqKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true   // Compose gaituta
        buildConfig = true // BuildConfig gaituta (GROQ_API_KEY erabiltzeko)
    }
}

/**
 * Menpekotasunak (dependencies) definitzen ditu.
 */
dependencies {

    // AndroidX core eta exekuzioa
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose platforma eta osagaiak
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.foundation.layout)

    // Room datu-basea
    implementation(libs.androidx.room.common.jvm)
    implementation("androidx.room:room-runtime:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3") // Koroutinekin lan egiteko
    kapt("androidx.room:room-compiler:2.8.3")     // Room prozesadorea

    // Material 3
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation(libs.compose.material3)

    // Media exekuzioa (ExoPlayer)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")

    // Kokapena zerbitzuak
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Mapa (OSMDroid)
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // Animazioak
    implementation("androidx.compose.animation:animation:1.5.0")
    implementation("androidx.compose.animation:animation-graphics:1.5.0")

    // Sare komunikazioa (Retrofit, OkHttp, Moshi)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // AppCompat
    implementation(libs.androidx.appcompat)

    // Testak
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google letra-tipoak
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.foundation)
    implementation(libs.foundation.layout)
}