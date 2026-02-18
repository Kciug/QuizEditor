plugins {
    `android-library`
    `kotlin-android`
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.migration"
    
    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":firestore"))

    coreKtx()
    ui()
    tests()
    daggerHilt()
}
