plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.api)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
