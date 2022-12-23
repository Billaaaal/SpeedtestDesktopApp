import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
}

group = "me.billa"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    maven("https://packages.jetbrains.team/maven/p/skija/maven")
    maven("https://dl.bintray.com/jetbrains/compose" )
    maven("https://dl.bintray.com/jetbrains/kotlinx" )
    maven( "https://maven.google.com" )
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0" )//2.7.1)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.1")
    implementation ("org.json:json:20220924")
    implementation ("com.github.oatrice:internet-speed-testing:1.0.1")
    implementation("org.jetbrains.skija:skija-windows:0.93.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("fr.bmartel:jspeedtest:1.32.1")









}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "speedtestappkotlin"
            packageVersion = "1.0.0"
            windows{
                packageVersion = "1.0.0"


            }
        }

    }

}