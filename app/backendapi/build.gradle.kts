plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    implementation(project(":app:datalib"))
    implementation(libs.gson)
    implementation(libs.retrofit) // API calls library
    implementation(libs.converter.gson)
}
