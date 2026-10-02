plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.components)
                implementation(libs.compose.runtime)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}
