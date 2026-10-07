// An app's design system, small: the tokens the sample is drawn with, and the one file that maps them
// onto overlay-kit's styles. It is the sample's, never copied into an app — an app maps its own.
plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.api)
            }
        }
    }
}
