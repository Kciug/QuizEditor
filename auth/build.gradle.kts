plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.quiz_mode"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreKtx()
    tests()
    daggerHilt()
    firebaseAuth()
}