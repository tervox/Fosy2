plugins {
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.detekt).apply(false)
}
dependencies { implementation("androidx.media3:media3-exoplayer:1.2.1") }
