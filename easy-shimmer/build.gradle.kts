plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

android {
    namespace = "io.github.easyshimmer"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.coil.compose)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.github.EvergreenTree97"
                artifactId = "easy-shimmer-compose"
                version = providers.gradleProperty("libraryVersion").get()

                from(components["release"])
            }
        }
    }
}