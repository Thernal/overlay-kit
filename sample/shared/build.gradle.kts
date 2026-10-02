plugins {
    alias(libs.plugins.overlaykit.compose)
}

kotlin {
    // The framework the iOS sample embeds (sample/ios, built by Xcode through
    // `embedAndSignAppleFrameworkForXcode`). `./gradlew build` links it, so it cannot rot unnoticed.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "SampleShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.overlay.core)
                implementation(projects.overlay.components)
                implementation(libs.compose.material3)
            }
        }
    }
}
