import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"

    // UI
    const val ANDROIDX_APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_NAVIGATION_FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION_FRAGMENT_KTX}"
    const val ANDROIDX_NAVIGATION_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION_UI_KTX}"
    const val SWIPE_REFRESH_LAYOUT = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.SWIPE_REFRESH_LAYOUT}"

    // Test
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:${Versions.JUNIT_VERSION}"
    const val ANDROIDX_ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINE_TEST}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    const val TURBINE = "app.cash.turbine:turbine:${Versions.TURBINE}"

    // HILT
    const val HILT_ANDROID = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"

    //FIREBASE BOM
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"

    //FIREBASE AUTH
    const val FIREBASE_AUTH = "com.google.firebase:firebase-auth-ktx"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx"

    // Json serializer
    const val KOTLINX_SERIALIZATION_JSON = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_JSON}"
}

fun DependencyHandler.coreKtx() = implementation(Dependencies.ANDROIDX_CORE_KTX)

fun DependencyHandler.ui() {
    implementation(Dependencies.ANDROIDX_APP_COMPAT)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(Dependencies.ANDROIDX_NAVIGATION_FRAGMENT_KTX)
    implementation(Dependencies.ANDROIDX_NAVIGATION_UI_KTX)
}

fun DependencyHandler.tests() {
    test(Dependencies.JUNIT)
    androidTest(Dependencies.ANDROIDX_JUNIT)
    androidTest(Dependencies.ANDROIDX_ESPRESSO_CORE)
    testImplementation(Dependencies.COROUTINE_TEST)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TURBINE)
}

fun DependencyHandler.daggerHilt() {
    implementation(Dependencies.HILT_ANDROID)
    kapt(Dependencies.HILT_COMPILER)
}

fun DependencyHandler.firebase() {
    implementation(platform(Dependencies.FIREBASE_BOM))
    implementation(Dependencies.FIREBASE_AUTH)
    implementation(Dependencies.FIREBASE_FIRESTORE)
}

fun DependencyHandler.kotlinxSerialization() {
    implementation(Dependencies.KOTLINX_SERIALIZATION_JSON)
}
