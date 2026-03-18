plugins {
implementation("androidx.media3:media3-exoplayer:1.2.1")
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.detekt).apply(false)
}
