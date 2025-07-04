import java.util.Base64

plugins {
    kotlin("jvm")
//    id("com.fatherofapps.api-key-generator")
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
//
//class CustomReadlineType : FAReadLine{
//    override fun readLine(line: String): Pair<String, String> {
//        val list =  line.split("=")
//        return Pair(list[0], list[1])
//    }
//}
//
//class CustomFAEncrypt: FAEncrypt{
//    override fun encrypt(key: String): ByteArray {
//        return Base64.getEncoder().encode(key.toByteArray())
//    }
//}
//
//apiKeyGenerator{
//
//    outPut {
//        apiKeyClassName = "ApiKeys"
//        apiKeyFile = layout.projectDirectory.file("src/main/kotlin/data/security/ApiKey.kt")
//        outPutPackageName = "data.security"
//        encryptType.set(CustomFAEncrypt())
//    }
//
//    input {
//
//        keyFile = layout.projectDirectory.file("../scripts/api_keys")
//        readLineType.set(CustomReadlineType())
//    }
//
//
//
//}