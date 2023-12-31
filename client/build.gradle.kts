import korlibs.korge.gradle.*
import korlibs.korge.gradle.targets.ProjectType
import korlibs.korge.gradle.targets.desktop.configureNativeDesktop
import korlibs.korge.gradle.targets.desktop.configureNativeDesktopCross
import korlibs.korge.gradle.targets.desktop.configureNativeDesktopRun
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

apply<KorgeGradlePlugin>()
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.korge)
}

korge {
    name = "Metron"
    id = "io.github.bruce0203.${rootProject.name}"
    targetAll()
    targetJvm()
    targetJs()
    targetDesktop()
    //targetDesktopCross()
    targetIos()
    targetAndroid()
    entryPoint = "runMain"
    orientation = Orientation.LANDSCAPE
    icon = File(projectDir, "src/commonMain/resources/images/logo.png"
        .replace("/", File.separator))
//    exeBaseName = ""
//    name = ""
    androidManifestChunks.addAll(setOf(
        """<uses-permission android:name="android.permission.INTERNET" />""",
        """<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />"""
    ))
    serializationJson()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
//            kotlin.addSrcDir(File(project(":shared").projectDir, "src/commonMain/kotlin"))
            dependencies {
                api(project(":deps"))
                api(libs.kotlinx.uuid)
//                api(project(":shared"))
            }
        }
    }
}


fun SourceDirectorySet.addSrcDir(file: File) {
    setSrcDirs(srcDirs.apply { add(file) })
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
        filesMatching("client.properties") {
            runCatching {
                val props =
                    Properties().apply { load(File(rootDir, "gradle.properties").inputStream()) }
                println(props)
                expand(
                    "version" to props["version"],
                    "server" to props["server"],
                )
            }.exceptionOrNull()?.apply { println(this) }
    }
}
