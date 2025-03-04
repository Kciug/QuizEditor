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
    implementation(project(":firestore"))

    coreKtx()
    tests()
    daggerHilt()
    firebase()
}