import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.net.URI

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "com.mangala"
version = "1.0"

publishing {
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

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "guava"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bignum)
            }
        }
        val androidUnitTest by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.linh.guava"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
