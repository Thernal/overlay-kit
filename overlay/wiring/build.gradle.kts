plugins {
    alias(libs.plugins.overlaykit.compose)
    alias(libs.plugins.overlaykit.injection)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.api)
                implementation(projects.overlay.impl)
            }
        }
    }
}
