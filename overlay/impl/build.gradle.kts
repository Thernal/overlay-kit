plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.api)
                implementation(libs.compose.animation)
                implementation(libs.compose.animation.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.navigationevent.compose)
            }
        }
    }
}
