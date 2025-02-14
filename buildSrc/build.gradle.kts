import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    implementation("com.android.tools.build:gradle:8.7.3")
    implementation("com.squareup:javapoet:1.13.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions{
    jvmTarget = "11"
}