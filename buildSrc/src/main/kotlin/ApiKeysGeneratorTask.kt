import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class ApiKeysGeneratorTask @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {

    @get:Input
    abstract var environments: NamedDomainObjectContainer<FAEnvironment>

    @get:Input
    abstract var outPut: Property<FAOutput>

    @get:Input
    abstract var input: Property<FAInput>



    @TaskAction
    fun execute() {
        println("outPut: ${outPut.get().apiKeyClassName}")
        println("inPut: ${input.get().keyFile.get().asFile.path}")
        environments.forEach {
            println("environment ${it.name} - ${it.keyName}")
        }
    }
}