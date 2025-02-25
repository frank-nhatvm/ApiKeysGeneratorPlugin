plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.fatherofapps"

gradlePlugin {
    plugins {
        register("ApiKeyGeneratorPlugin") {
            id = "com.fatherofapps.api-key-generator"
            implementationClass = "ApiKeyGeneratorPlugin"
        }
    }
}