
This Plugin provides a Task to allow you to generate the API key to the ByteArray format.
By doing this, you can hide the real API key from your source code.
Here is how the output file looks like:
```kotlin
package data.security

internal object ApiKeys{
    
    val staging: ByteArray = byteArrayOf(0x61,0x32,0x78,0x73,0x61,0x32,0x46,0x7A,0x62,0x47,0x52,0x6D,0x61,0x32,0x70,0x7A,0x62,0x47,0x46,0x6D,0x61,0x77,0x3D,0x3D)
    
    val production: ByteArray = byteArrayOf(0x62,0x47,0x46,0x7A,0x61,0x32,0x52,0x6D,0x61,0x6D,0x74,0x7A,0x62,0x47,0x46,0x6D,0x5A,0x47,0x73,0x3D)
    
    val dev: ByteArray = byteArrayOf(0x4D,0x6E,0x4A,0x31,0x4D,0x44,0x6C,0x33,0x64,0x57,0x59,0x35,0x4D,0x48,0x4E,0x71,0x5A,0x67,0x3D,0x3D)

}
```
This Plugin supports both local setup and CI/CD. 
- For local setup, you can provide  an input file. For example,here is the input file for the above generated file
```shell
    staging=kllkasldfkjslafk
    production=laskdfjkslafdk
    dev=2ru09wuf90sjf
``` 
- Fo CI/CD, you can provide name and value as below:
```shell
./gradlew :app:generateApiKey --apiKeyNames=staging --apiKeyValues=keyStaging --apiKeyNames=dev --apiKeyValues=keyDev
```


# Basic usage
## Add plugin to your module , (build.gradle.kts)
```kotlin
 id("com.fatherofapps.api-key-generator")
```
## Configure the Plugin
In the build.gradle.kts of your module
```kotlin
apiKeyGenerator{

    outPut {
        apiKeyClassName = "ApiKeys"
        apiKeyFile = layout.projectDirectory.file("src/main/kotlin/data/security/ApiKey.kt")
        outPutPackageName = "data.security"
    }

    input {
        keyFile = layout.projectDirectory.file("../scripts/api_keys")
    }
    
}
```
Whereas :
- keyFile: is the file you locate the input file. This file will contain all API key's name and its value that you want to generate to file
- apiKeyClassName: name of `internal object`. In our example, we use `ApiKeys`, then later you can access the API key like this: `APIKeys.staging`
- apiKeyFile: the file you want to write the generated code to. You just need to make sure the parent folder is created, if the file is not exist, it will be created by Plugin
- outPutPackName: the package's name of `internal object $apiKeyClassName`. In our sample,we use `data.security`.

# Advance usage
## Customize how to read the input file
By default, Plugin expects each line of the input file will be in this format: `name=value`. It splits this line into two parts to take `name` and `value` by using pattern `=`. 
If you want to use a different format for each line of your input file, you can provide a custom `FAReadLine`.
```kotlin
class CustomReadlineType : FAReadLine{
    override fun readLine(line: String): Pair<String, String> {
        
    }
}
```
Plugin reads the input file line by line, then it gives you each line, and expects a Pair of name and value.
You can provide your custom `FAReadLine` as below:
```kotlin
apiKeyGenerator {

    intPut {
        // other configurations
        readLineType.set(CustomReadlineType())
    }

// other configurations
}
```
## Customize how to encrypt the API Key's value
Here is how a Pair of name and value is generated to file:
```kotlin
val staging: ByteArray = byteArrayOf(0x61,0x32,0x78,0x73,0x61,0x32,0x46,0x7A,0x62,0x47,0x52,0x6D,0x61,0x32,0x70,0x7A,0x62,0x47,0x46,0x6D,0x61,0x77,0x3D,0x3D)
```
For each line like this, Plugin receives the Pair name and value, converts the value to ByteArray: `value.toByteArray()` then uses the following function to produce the line of code:
```kotlin
 private fun generateByteArrayCode(name: String, byteArray: ByteArray): String {
        val hexValues = byteArray.joinToString(",") { "0x" + it.toUByte().toString(16).uppercase() }
        return "val $name: ByteArray = byteArrayOf($hexValues)"
    }
```
With this solution, you can use `String` to convert the `ByteArray` to real API key's value, 
```kotlin
object  ApiKeyProvider{
    fun apiKeyStaging() = String(ApiKeys.staging)
    fun apiKeyProduction() = String(ApiKeys.production)
    fun apiKeyDev() = String(ApiKeys.dev)
}
```
If you want to apply another encryption algorithm for your API Key's value before Plugin prints it to `ByteArray`,
you can provide a custom of `FAEncrypt`:
```kotlin
class CustomFAEncrypt: FAEncrypt{
    override fun encrypt(key: String): ByteArray {
        // replace this by your encryption algorithm
        return Base64.getEncoder().encode(key.toByteArray()) 
    }
}
```
Plugin gives you the key and expect a `ByteArray`.  
then apply this custom:
```kotlin
apiKeyGenerator {

    outPut {
        // other configurations
        encryptType.set(CustomFAEncrypt())
    }

    // other configurations
}
```
Then to convert the key to real key:
```kotlin
object  ApiKeyProvider {
    // replace by your decryption algorithm
    fun apiKeyStaging() = String(Base64.getDecoder().decode(ApiKeys.staging))
    // other codes
}
```