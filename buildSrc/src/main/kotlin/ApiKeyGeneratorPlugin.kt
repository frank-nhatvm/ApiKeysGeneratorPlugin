import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import java.io.File
import javax.inject.Inject

@DslMarker
annotation class APIKeyDsl

@APIKeyDsl
abstract class ApiKeyGeneratorPluginExtension @Inject constructor(objectFactory: ObjectFactory) {

    val environments: NamedDomainObjectContainer<FAEnvironment> =
        objectFactory.domainObjectContainer(FAEnvironment::class.java)

    fun environments(action: NamedDomainObjectContainer<FAEnvironment>.() -> Unit) {
        environments.apply(action)
    }

    val outPut: FAOutput = objectFactory.newInstance(FAOutput::class.java)

    fun outPut(action: FAOutput.() -> Unit) {
        action.invoke(outPut)
    }

    val input: FAInput = objectFactory.newInstance(FAInput::class.java)

    fun input(action: FAInput.() -> Unit) {
        action.invoke(input)
    }

}

abstract class FAEnvironment(private val name: String) : Named {

    var keyName: String = ""

    override fun getName(): String {
        return name
    }
}

interface FAEncrypt {
    fun encrypt(key: String): ByteArray
}

class DefaultFAEncrypt : FAEncrypt {
    override fun encrypt(key: String): ByteArray {
        return key.toByteArray()
    }
}

@APIKeyDsl
abstract class FAOutput @Inject constructor(objectFactory: ObjectFactory) {
    val apiKeyClassName: Property<String> = objectFactory.property(String::class.java)
    val apiKeyFile: RegularFileProperty = objectFactory.fileProperty()
    val encryptType: Property<FAEncrypt> = objectFactory.property(FAEncrypt::class.java)
}

interface FAReadLine {
    fun readLine(line: String): Pair<String, String>
}

class DefaultReadline : FAReadLine {
    override fun readLine(line: String): Pair<String, String> {
        val list = line.split("=")
        if (list.size != 2) {
            throw Exception("Line is incorrect format. Expect: key=value")
        }
        return Pair(list[0], list[1])
    }
}

@APIKeyDsl
abstract class FAInput @Inject constructor(objectFactory: ObjectFactory) {
    val keyFile: RegularFileProperty = objectFactory.fileProperty()
    val readLineType: Property<FAReadLine> = objectFactory.property(FAReadLine::class.java)
}

abstract class ApiKeyGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val extension =
            target.extensions.create("apiKeyGenerator", ApiKeyGeneratorPluginExtension::class.java, target.objects)


        extension.outPut.encryptType.convention(DefaultFAEncrypt())
        extension.outPut.apiKeyFile.convention(target.layout.projectDirectory.file("src/main/kotlin/ApiKey.kt"))
        extension.outPut.apiKeyClassName.convention("ApiKey")

        extension.input.keyFile.convention(target.layout.projectDirectory.file("api-key.txt"))
        extension.input.readLineType.convention(DefaultReadline())

        target.tasks.register("generateApiKey", ApiKeysGeneratorTask::class.java) {
            group = "com.fatherofapps"
            description = "Generate API key file"
        }

        target.afterEvaluate {
            extension.environments.forEach {
                println("Configuring environment: ${it.name} with key: ${it.keyName}")
            }
            val output = extension.outPut
            println("outPut file name:  ${output.apiKeyFile.get().asFile.path}")
            println("outPut class name: ${output.apiKeyClassName.get()}")
            println("input EncryptType type: ${output.encryptType.get().javaClass.name}")

            val input = extension.input
            println("input key file : ${input.keyFile.get()}")
            println("input read line type: ${input.readLineType.get().javaClass.name}")
        }


    }
}