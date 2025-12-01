import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            // @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            // implementation(compose.components.uiTest)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(project(":reversiCore"))
        }
    }
}
compose.desktop {
    application {
        mainClass = "MainKt" // substitui se a tua main tiver outro nome
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

// Para execução via Gradle
tasks.withType<JavaExec> {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

// Para o JAR gerado
tasks.withType<CreateStartScripts> {
    defaultJvmOpts = listOf("--enable-native-access=ALL-UNNAMED")
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

