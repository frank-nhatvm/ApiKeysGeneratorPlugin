import java.util.Base64

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

class CustomReadlineType : FAReadLine{
    override fun readLine(line: String): Pair<String, String> {
        return Pair("staging","")
    }
}

class CustomFAEncrypt: FAEncrypt{
    override fun encrypt(key: String): ByteArray {
        return Base64.getEncoder().encode(key.toByteArray())
    }
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

    outPut {
        apiKeyClassName = "ApiKeys"
      //  apiKeyFileName = "ApiKeys.kt"
        encryptType.set(CustomFAEncrypt())
    }

    input {
        keyFile = layout.projectDirectory.file("scripts/api_key")
        readLineType.set(CustomReadlineType())
    }



}