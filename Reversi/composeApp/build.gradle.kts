import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvmToolchain(21)
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(project(":reversiCore"))
            implementation(compose.materialIconsExtended)

            // Serialization (common) so DTOs in core are serializable
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            // Driver oficial do MongoDB (Sync é mais simples para começar)
            implementation("org.mongodb:mongodb-driver-sync:4.11.0")
            // (Opcional) SLF4J para gerir logs e não poluir a consola
            implementation("org.slf4j:slf4j-simple:2.0.9")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(project(":reversiCore"))
            // jvm-specific is optional (we have common serialization)
        }
    }
}
compose.desktop {
    application {
        mainClass = "reversi_ui.MainKt"
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "ReversiComposeApp"
            packageVersion = "1.0.0"
        }
        javaHome = System.getProperty("java.home")
    }
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<CreateStartScripts> {
    defaultJvmOpts = listOf("--enable-native-access=ALL-UNNAMED")
}

compose.resources {
    publicResClass = true
    generateResClass = always
}