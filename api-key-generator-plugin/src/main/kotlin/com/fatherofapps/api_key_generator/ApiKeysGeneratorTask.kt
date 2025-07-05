package com.fatherofapps.api_key_generator

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import javax.inject.Inject

abstract class ApiKeysGeneratorTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @get:Input
    abstract val environments: NamedDomainObjectContainer<FAEnvironment>

    @get:Input
    abstract val outPut: Property<FAOutput>

    @get:Input
    @get:Optional
    abstract val input: Property<FAInput>

    @get:Input
    @get:Optional
    val apiKeyNames: ListProperty<String> = objects.listProperty(String::class.java)

    @Option(option = "apiKeyNames", description = "the list of API Key's Name")
    fun setApiKeyNames(keyNames: List<String>) {
        apiKeyNames.set(keyNames)
    }

    @get:Input
    @get:Optional
    val apiKeyValues: ListProperty<String> = objects.listProperty(String::class.java)

    @Option(option = "apiKeyValues", description = "the list of API Key's value")
    fun setApiKeyValues(values: List<String>) {
        apiKeyValues.set(values)
    }

    @TaskAction
    fun execute() {
       val map = getKeyValues()
        val output: FAOutput = outPut.orNull ?: throw  Exception("Can not find the configuration for Output")
        writeToFile(map=map, output=output)
    }

    private fun getKeyValues(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val cliNames = apiKeyNames.getOrElse(emptyList())
        val cliValues = apiKeyValues.getOrElse(emptyList())
        if (cliNames.isNotEmpty() && cliValues.isNotEmpty()) {
            if (cliValues.size != cliNames.size) {
                throw Exception("The size of keys and size of values are not equal")
            }

            for (index in 0 until cliNames.size) {
                val name = cliNames[index]
                val value = cliValues[index]
                map[name] = value
            }
        } else {
            try{
            val inputFile: File = input.orNull?.keyFile?.orNull?.asFile ?: throw Exception("Can not find the InputFile")
             val readLineType = input.orNull?.readLineType?.orNull ?: throw Exception("Can not find the ReadLineType of FAInput")
            inputFile.readLines().forEach { line ->
                val (name, value) = readLineType.readLine(line)
                map[name] = value
            }}catch (e: Exception){
                throw e
            }
        }
        return map.toMap()
    }

    private fun writeToFile(map: Map<String, String>, output: FAOutput) {

        if (!output.apiKeyFile.isPresent) {
            throw Exception("Can not find the configuration for apiKeyFile")
        }

        if (!output.apiKeyClassName.isPresent) {
            throw Exception("apiKeyClassName is required")
        }

        val apiKeyFile = output.apiKeyFile.get().asFile
        val apiKeyClassName = output.apiKeyClassName.get()


        val stringBuilder = StringBuilder()
        if (output.outPutPackageName.isPresent) {
            val outPutPackageName = output.outPutPackageName.get()
            if (outPutPackageName.isNotEmpty()) {
                stringBuilder.append("package $outPutPackageName")
            }
        }
        stringBuilder.append("\n")
        stringBuilder.append("internal object $apiKeyClassName{")
        stringBuilder.append("\n")
        val encryptType = output.encryptType.get()
        map.forEach { (key, value) ->
            val encryptedValue = encryptType.encrypt(value)
            stringBuilder.append(generateByteArrayCode(name = key, byteArray = encryptedValue))
            stringBuilder.append("\n")
        }

        stringBuilder.append("\n")
        stringBuilder.append("}")

        if (!apiKeyFile.exists()) {
            apiKeyFile.createNewFile()
        }
        apiKeyFile.writeText(stringBuilder.toString())
        println("Generated API key: ${apiKeyFile.absolutePath}")
    }
    private fun generateByteArrayCode(name: String, byteArray: ByteArray): String {
        val hexValues = byteArray.joinToString(",") { "0x" + it.toUByte().toString(16).uppercase() }
        return "val $name: ByteArray = byteArrayOf($hexValues)"
    }
}