plugins {
    kotlin("jvm")
    id("com.fatherofapps.api-key-generator")
}

group = "com.fatherofapps"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
apiKeyGenerator{
    environments {
        register("staging"){
            keyName = "STAGING"
        }
        register("production"){
            keyName = "PRODUCTION"
        }
        register("dev"){
            keyName = "DEV"
        }
    }
}