// Initializes the production jobs directory, which runs with deployed scripts and repos

import com.cloudbees.hudson.plugins.folder.Folder
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper
import hudson.plugins.git.GitSCM
import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.libs.FolderLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import org.jenkinsci.plugins.workflow.job.WorkflowJob

println("=== Initialize the Production folder")
if (Jenkins.instance.getItem("Production") != null) {
    println("Production folder has been already initialized, skipping the step")
    return
}

def folder = Jenkins.instance.createProject(Folder.class, "Production")

// Include https://github.com/jenkins-infra/pipeline-library
def pipelineLibrarySource = new GitSCMSource("pipeline-library", "https://github.com/jenkins-infra/pipeline-library.git", null, null, null, false)
LibraryConfiguration lc = new LibraryConfiguration("pipeline-library", new SCMSourceRetriever(pipelineLibrarySource))
lc.with {
    implicit = true
    defaultVersion = "master"
}
folder.addProperty(new FolderLibraries([lc]))
FolderOwnershipHelper.setOwnership(folder, new OwnershipDescription(true, "admin"))

// Add a sample project
WorkflowJob project1 = folder.createProject(WorkflowJob.class, "Ownership_Plugin_Agent")
project1.setDefinition(new CpsFlowDefinition("buildPlugin(platforms: ['linux'], repo: 'https://github.com/jenkinsci/ownership-plugin.git')", true))
JobOwnerHelper.setOwnership(project1, new OwnershipDescription(true, "admin", Arrays.asList("user")))

WorkflowJob project2 = folder.createProject(WorkflowJob.class, "Ownership_Plugin_Master")
project2.setDefinition(new CpsFlowDefinition("buildPlugin(platforms: ['master'], repo: 'https://github.com/jenkinsci/ownership-plugin.git')", true))
JobOwnerHelper.setOwnership(project2, new OwnershipDescription(true, "admin", Arrays.asList("user")))

// Sample project with a build flow from SCM
WorkflowJob project3 = folder.createProject(WorkflowJob.class, "Remoting")
GitSCM source = new GitSCM("https://github.com/jenkinsci/remoting.git")
project3.setDefinition(new CpsScmFlowDefinition(source, "Jenkinsfile"))
JobOwnerHelper.setOwnership(project3, new OwnershipDescription(true, "admin", Arrays.asList("user")))

// TODO: Add Multi-Branch project, which does not build with Windows
