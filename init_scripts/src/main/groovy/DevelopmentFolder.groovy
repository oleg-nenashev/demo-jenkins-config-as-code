// Initializes the Development folder, which is fully configurable by the user


import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import hudson.plugins.filesystem_scm.FSSCM
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.libs.FolderLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMRetriever

println("=== Initialize the Development folder")
if (Jenkins.instance.getItem("Development") != null) {
    println("Development folder has been already initialized, skipping the step")
    return
}

// Admin owns the root Development folder
def folder = Jenkins.instance.createProject(Folder.class, "Development")
FolderOwnershipHelper.setOwnership(folder, new OwnershipDescription(true, "admin"))

// Users get their own sandboxes
def folder2 = folder.createProject(Folder.class, "User")
FolderOwnershipHelper.setOwnership(folder2, new OwnershipDescription(true, "user"))

// Create a library for local Jenkins Pipeline Library Development
// if the Env Var is set and the directory is mapped
println("==== Initializing local Pipeline Library development dir")
File file = new File("/var/jenkins_home/pipeline-library/vars")
if (!file.exists()) {
    println("/var/jenkins_home/pipeline-library is not mapped, skipping")
    return
} else {
    println("/var/jenkins_home/pipeline-library is mapped, initializing the directory")
}

def pipelineLib = folder.createProject(Folder.class, "PipelineLibrary")
FolderOwnershipHelper.setOwnership(pipelineLib, new OwnershipDescription(true, "user"))
def scm = new FSSCM("/var/jenkins_home/pipeline-library", false, false, null)
LibraryConfiguration lc = new LibraryConfiguration("pipeline-library", new SCMRetriever(scm))
lc.with {
    implicit = true
    defaultVersion = "master"
}
pipelineLib.addProperty(new FolderLibraries([lc]))

// Add sample projects
static def createPipelineLibJob(Folder folder, String repo, String nameSuffix = "", String args = null) {
    WorkflowJob sshdModuleProject = folder.createProject(WorkflowJob.class, "${repo}${nameSuffix}")
    String extras = args == null ? "" : ", $args"
    sshdModuleProject.definition = new CpsFlowDefinition(
        "buildPlugin(platforms: ['linux'], repo: 'https://github.com/jenkinsci/${repo}.git' ${extras})", true
    )
}

createPipelineLibJob(pipelineLib, "job-restrictions-plugin", "_findbugs", "findbugs: [archive: true, unstableTotalAll: '0']")
createPipelineLibJob(pipelineLib, "sshd-module")
createPipelineLibJob(pipelineLib, "sshd-module", "_findbugs", "findbugs: [archive: true, unstableTotalAll: '0']")
createPipelineLibJob(pipelineLib, "sshd-module", "_findbugs_checkstyle", "findbugs: [archive: true, unstableTotalAll: '0'], checkstyle: [run: true, archive: true]")
// Just a plugin, where FindBugs really fails
createPipelineLibJob(pipelineLib, "last-changes-plugin", "_findbugs", "findbugs: [archive: true, unstableTotalAll: '0']")
