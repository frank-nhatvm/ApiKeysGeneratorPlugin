import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@DslMarker
annotation class APIKeyDsl

@APIKeyDsl
abstract class ApiKeyGeneratorPluginExtension constructor(project: Project) {

    val environments: NamedDomainObjectContainer<FAEnvironment> =
        project.objects.domainObjectContainer(FAEnvironment::class.java)

    fun environments(action: NamedDomainObjectContainer<FAEnvironment>.() -> Unit) {
        environments.apply(action)
    }

}

abstract class FAEnvironment(private val name: String) : Named {

    var keyName: String = ""

    override fun getName(): String {
        return name
    }


}

abstract class ApiKeyGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val extension = target.extensions.create("apiKeyGenerator", ApiKeyGeneratorPluginExtension::class.java, target)
        target.tasks.register("generateApiKey", ApiKeysGeneratorTask::class.java) {
            group = "com.fatherofapps"
            description = "Generate API key file"
        }

        target.afterEvaluate {
            extension.environments.forEach {
                println("Configuring environment: ${it.name} with key: ${it.keyName}")
            }
        }


    }
}