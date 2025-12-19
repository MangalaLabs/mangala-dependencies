import java.io.FileInputStream
import java.util.Properties

rootProject.name = "MangalaDependencies"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

val prop = Properties().apply {
    load(FileInputStream(File(rootProject.projectDir, "local.properties")))
}
val githubUsername: String = prop.getProperty("GITHUB_ACTOR")
val githubToken: String = prop.getProperty("GITHUB_TOKEN")

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal {
            content {
                includeGroup("com.mangala.wallet.browser")
                includeGroup("com.mangala")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://maven.pkg.github.com/trustwallet/wallet-core")
                    credentials {
                        username = githubUsername
                        password = githubToken
                    }
                }
            }
            filter {
                includeGroup("com.trustwallet")
            }
        }

        maven {
            url = uri("https://maven.pkg.github.com/MangalaLabs/mangala-dependencies")
            credentials {
                username = githubUsername 
                password = githubToken    
            }
            content {
                includeGroup("com.mangala.wallet.browser")
                includeGroup("com.mangala")
            }
        }
    }
}

include(":composeApp")
include(":guava")
include(":bitcoinj")
include(":browser-bridge-api")

// Browser modules - Level 1 (no internal dependencies)
include(":browser:anrs-api")
include(":browser:app-build-config-api")
include(":browser:app-store")
include(":browser:component_resources")
include(":browser:device-auth-api")
include(":browser:di")
include(":browser:downloads-api")
include(":browser:feature-toggles-api")
include(":browser:local")
include(":browser:macos-api")
include(":browser:remote-messaging-api")
include(":browser:remote-messaging-store")
include(":browser:secure-storage-api")
include(":browser:secure-storage-store")
include(":browser:traces-api")

// Browser modules - Level 2
include(":browser:common")
include(":browser:device-auth-impl")
include(":browser:privacy-config-api")
include(":browser:traces-impl")

// Browser modules - Level 3
include(":browser:bandwidth-store")
include(":browser:common-test")
include(":browser:common-ui")
include(":browser:downloads-store")
include(":browser:feature-toggles-impl")
include(":browser:macos-store")
include(":browser:privacy-config-store")
include(":browser:secure-storage-impl")
include(":browser:statistics")

// Browser modules - Level 4
include(":browser:anrs-store")
include(":browser:autofill-api")
include(":browser:browser-api")
include(":browser:crypto:execute")
include(":browser:privacy-config-impl")

// Browser modules - Level 5
include(":browser:anrs-impl")
include(":browser:autofill-store")
include(":browser:bandwidth-impl")
include(":browser:downloads-impl")
include(":browser:macos-impl")
include(":browser:remote-messaging-impl")

// Browser modules - Level 6
include(":browser:autofill-impl")

// Browser modules - Level 7
include(":browser:app")