package xyz.gofannon.mecha.task

import org.gradle.api.InvalidUserDataException
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.FileCopyAction
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarInputStream

abstract class EmbedJarsTask extends Copy {

    @Input
    String indexFilename = "jar-index.txt"

    @TaskAction
    void embedJars() {
        println("first")
        //this.getMainSpec().getDestinationDir()

        super.copy()
        println("--- copy end")


        //File dir = this.getMainSpec().getDestinationDir()
//        File dir  =getDestinationDir()
//        println("===> "+dir)


        List<String> entries = new ArrayList<>();
        getSource().forEach {
            List<String> jarEntries = extractAllEntries(it)
            entries.addAll(jarEntries)
        }

        var file = new File(getDestinationDir(), indexFilename)
        file.createNewFile()
        file.write(entries.join("\n"), "UTF-8")

        println("last")
    }


    List<String> extractAllEntries(File file) {
        List<String> resourceList = new ArrayList<>()
        var jarFile = new JarFile(file)
        jarFile.entries().asIterator().forEachRemaining {
            resourceList.add(it.name+":"+file.name)
        }
        return resourceList
    }


    @Override
    protected CopyAction createCopyAction() {
        File destinationDir = this.getDestinationDir();
        if (destinationDir == null) {
            throw new InvalidUserDataException("No copy destination directory has been specified, use 'into' to specify a target directory.");
        } else {
            return new FileCopyAction(this.getFileLookup().getFileResolver(destinationDir));
        }
    }
}
