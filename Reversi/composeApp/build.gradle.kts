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
            // Driver oficial do MongoDB
            implementation("org.mongodb:mongodb-driver-sync:4.11.0")
            // (Opcional) SLF4J
            implementation("org.slf4j:slf4j-simple:2.0.9")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(project(":reversiCore"))
        }

        // Dependências para Testes Unitários (JVM) ---
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                // Testes de Corrotinas (para o ViewModel)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                // Mockk (para simular o MongoGameManager)
                implementation("io.mockk:mockk:1.13.8")
                // JUnit 5 (framework de testes)
                implementation("org.junit.jupiter:junit-jupiter:5.10.1")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "reversi_app.MainKt"
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

// Configuração necessária para o JUnit 5 funcionar corretamente
tasks.withType<Test> {
    useJUnitPlatform()
}

compose.resources {
    publicResClass = true
    generateResClass = always
}