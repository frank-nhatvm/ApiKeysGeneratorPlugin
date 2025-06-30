pluginManagement {

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ApiKeyGenerators"
include("app")
include(":api-key-generator-plugin")
