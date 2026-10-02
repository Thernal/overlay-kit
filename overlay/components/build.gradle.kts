plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.core)
                implementation(libs.compose.animation)
                implementation(libs.compose.animation.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
