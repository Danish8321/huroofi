plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.huroofi.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.huroofi.app"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            // R8 and resource shrinking (plan 08 decision 14). Signed with the debug key for local
            // installs only; real signing comes with plan 09.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = false
        // Contract tests read content straight from disk; declare it so a data-only change re-runs them.
        unitTests.all { test ->
            test.inputs.dir("../data").withPropertyName("contentData").withPathSensitivity(PathSensitivity.RELATIVE)
            test.inputs.dir("src/main/assets").withPropertyName("appAssets").withPathSensitivity(PathSensitivity.RELATIVE)
            test.inputs.dir("src/main/res").withPropertyName("appRes").withPathSensitivity(PathSensitivity.RELATIVE)
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
