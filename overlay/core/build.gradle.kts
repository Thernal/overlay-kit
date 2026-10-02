plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.animation.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.navigationevent.compose)
            }
        }
    }
}
