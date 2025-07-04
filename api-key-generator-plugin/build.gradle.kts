plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    signing
}
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

group = "com.fatherofapps"
version = "1.0.0"

gradlePlugin {
    website.set("https://github.com/frank-nhatvm")
    vcsUrl.set("https://github.com/frank-nhatvm/ApiKeysGeneratorPlugin")
    plugins {
        create("apiKeyGenerator") {
            id = "com.fatherofapps.api-key-generator"
            implementationClass = "com.fatherofapps.api_key_generator.ApiKeyGeneratorPlugin"
            displayName = "API key generator plugin"
            description = "API key generator plugin"
            tags.set(listOf("key generator", "api key", "gradle plugin"))
        }
    }
}

publishing {
    repositories {
//        maven {
//            name = "localPluginRepository"
//            url = uri("${rootProject.projectDir}/local-plugin-repository")
//        }
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        findProperty("signing.key") as String?,
        findProperty("signing.password") as String?
    )
    sign(publishing.publications)
}