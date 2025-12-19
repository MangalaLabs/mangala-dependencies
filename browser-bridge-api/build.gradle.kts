import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.net.URI

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.mangala"
            artifactId = project.name
            version = "1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/MangalaLabs/mangala-dependencies")
            credentials {
                username = gradleLocalProperties(rootDir, providers).getProperty("GITHUB_ACTOR")
                password = gradleLocalProperties(rootDir, providers).getProperty("GITHUB_TOKEN")
            }
        }
    }
}

android {
    val androidMinSdk: String by project
    val androidCompileSdk: String by project
    val androidTargetSdk: String by project

    namespace = "com.mangala.wallet.features.browser_bridge_api"
    compileSdk = androidCompileSdk.toInt()
    defaultConfig {
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation(libs.androidx.fragment.ktx)
}
