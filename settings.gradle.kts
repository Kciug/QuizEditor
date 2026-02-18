pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version "2.0.20"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QuizEditor"
include(":app")
include(":quiz_mode")
include(":login_screen")
include(":core")
include(":auth")
include(":firestore")
include(":home")
include(":chat")
include(":swipe_mode")
include(":database_management")
include(":translations_mode")
include(":cem_mode")
include(":migration")
