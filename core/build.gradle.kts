plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.core"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreKtx()
    ui()
    tests()
    daggerHilt()
}