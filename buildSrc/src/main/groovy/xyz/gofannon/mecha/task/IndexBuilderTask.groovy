package xyz.gofannon.mecha.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class IndexBuilderTask extends DefaultTask {

    @InputFiles
    Configuration configuration

    @Input
    String embeddedJarDirectory

    @Input
    List<String> ignoredDependencies = new ArrayList<>()

    @Input
    String indexFilename = "jar-index.txt"


    @TaskAction
    void action() {
        var targetDirectory = project.layout.buildDirectory.dir(embeddedJarDirectory).get()
        targetDirectory.asFile.mkdirs()

//        var conf = project.configurations.named("embeddedJar").get()
//        conf.incoming.resolutionResult.allComponents {
//            println("xxx "+it)
//        }


        List<String> content = new ArrayList<>()
        configuration.incoming.resolutionResult.allDependencies {

            var moduleName = it.group + ":" + it.name
//            if (!ignoredDependencies.contains(moduleName)) {
            def jarFilename = it.name + "-" + it.version + ".jar"
            content.add(jarFilename)
//            }

        }

        var file = new File(targetDirectory.asFile, indexFilename)
        file.createNewFile()
        file.write(content.join("\n"), "UTF-8")
    }


}
