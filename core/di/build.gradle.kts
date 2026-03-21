plugins {
    id("android-library-convention")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.ziopam.kollocol.core.di"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core:network"))
    implementation(project(":core:session"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.room.runtime)
//    implementation(libs.androidx.room.ktx)
//    ksp(libs.androidx.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.datastore.preferences)
}
