plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

group = "com.mangala.wallet.browser"
version = "1.0"

publishing {
    publications {
        create<MavenPublication>("myLib") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
