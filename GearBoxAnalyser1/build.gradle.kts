import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.gearboxtestapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)

    implementation(compose.desktop.windows_x64)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("com.fazecast:jSerialComm:2.9.3")

    implementation("org.apache.poi:poi:5.0.0")

    implementation("org.jfree:jcommon:1.0.24")
    implementation("org.jfree:jfreechart:1.5.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MCM"
            packageVersion = "1.3.0"
        }

        buildTypes.release {
            proguard {
                //isEnabled.set(false)
                configurationFiles.from("compose-desktop.pro")
            }
        }
    }
}
