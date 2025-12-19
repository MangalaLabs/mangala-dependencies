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
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "bitcoinj"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.kotlinxIO)
                implementation(libs.krypto)
                implementation(libs.ktor.io)
                implementation(libs.okio)
                implementation(libs.kotlinx.datetime)
                implementation(libs.stately.common)
                implementation(libs.stately.concurrent.collections)
                implementation(libs.stately.concurrency)
                implementation("com.mangala:guava:1.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
    }
}

android {
    val androidMinSdk: String by project

    namespace = "com.mangala.bitcoinj"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    androidTestImplementation(libs.junit)
}
