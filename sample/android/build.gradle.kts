plugins {
    alias(libs.plugins.overlaykit.android.application)
}

dependencies {
    implementation(projects.sample.shared)
    implementation(libs.androidx.activity.compose)
}
