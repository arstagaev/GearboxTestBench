import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")

    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.tagaev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation(compose.desktop.windows_x64)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("com.fazecast:jSerialComm:2.9.3")

    implementation("org.apache.poi:poi:5.0.0")

    implementation("org.jfree:jcommon:1.0.24")
    implementation("org.jfree:jfreechart:1.5.5")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GearBoxAnalyser2"
            packageVersion = "1.0.0"
        }
    }
}
