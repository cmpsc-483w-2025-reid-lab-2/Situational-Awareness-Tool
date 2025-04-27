plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.spotless)
}

android {
    namespace = "org.reidlab.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.reidlab.frontend"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.9"
    }

    buildTypes {
        release {
            // Enable the following to decrease binary size and optimize res.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

spotless {
    kotlin {
        ktfmt().googleStyle()
        // ktfmt().dropboxStyle()

        // Set the files to format
        target("src/**/*.kt")
        targetExclude("build/**/*.kt")
        // targetExclude("src/main/kotlin/com/example/dontformat/*")
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.compose.material)
    implementation(libs.horologist.composables)
    implementation(libs.compose.materialIconsCore)
    implementation(libs.compose.materialIconsExtended)
    implementation(libs.accompanist.pager.indicators)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)
    implementation(libs.compose.material3)
    implementation(libs.androidx.health.services.client)
    implementation(libs.concurrent.futures)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}