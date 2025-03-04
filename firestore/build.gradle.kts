plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.auth"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))

    coreKtx()
    tests()
    daggerHilt()
    firebase()
}