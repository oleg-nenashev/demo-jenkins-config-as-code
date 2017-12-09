package scripts
// Initializes the Development folder, which is fully configurable by the user

import groovy.io.FileType
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import io.jenkins.systemgroovy.plugins.PipelineHelper
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper

println("=== Initialize the Demo folder")
if (Jenkins.instance.getItem("Demo") != null) {
    println("Demo folder has been already initialized, skipping the step")
    return
}

// Admin owns the root Development folder
def demoFolder = Jenkins.instance.createProject(Folder.class, "Demo")
demoFolder.description = "Demo Pipeline jobs"
FolderOwnershipHelper.setOwnership(demoFolder, new OwnershipDescription(true, "user"))

// Create a library for local Jenkins Pipeline Library Development
// if the Env Var is set and the directory is mapped
println("==== Initializing local Pipeline Library development dir")
File pipelineLibSource = new File("/var/jenkins_home/pipeline-library/")
if (!new File(pipelineLibSource, "vars").exists()) { // TODO: Lame, to be fixed
    println("/var/jenkins_home/pipeline-library is not mapped, skipping")
} else {
    println("/var/jenkins_home/pipeline-library is mapped, initializing the directory")
    PipelineHelper.addLocalLibrary(demoFolder, pipelineLibSource.absolutePath)
}

// Loads demo from sources
def demoSourceDir = new File(Jenkins.instance.rootDir, "init.groovy.d/demo")
if (demoSourceDir.exists()) {
    println("===== Loading demo")
    demoSourceDir.eachFile(FileType.FILES) { file ->
        if (file.name.endsWith('.groovy')) {
            PipelineHelper.createDemo(file, demoFolder, file.name, false)
        }
    }

    // Also load demo from the Pipeline library
    def libDemos = new File(pipelineLibSource, "demo")
    if (libDemos.exists()) {
        libDemos.eachFile(FileType.FILES) { file ->
            if (file.name.endsWith('.groovy')) {
                PipelineHelper.createDemo(file, demoFolder)
            }
        }
    }
}
