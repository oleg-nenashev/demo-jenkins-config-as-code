// Initializes the System folder
// In the default configuration Jenkins admin is eligible to start jobs from this directory on the master
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.libs.FolderLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def folder = Jenkins.instance.createProject(Folder.class, "System")

// Include https://github.com/jenkins-infra/pipeline-library
def pipelineLibrarySource = new GitSCMSource("pipeline-library", "https://github.com/jenkins-infra/pipeline-library.git", null, null, null, false)
LibraryConfiguration lc = new LibraryConfiguration("pipeline-library", new SCMSourceRetriever(pipelineLibrarySource))
lc.setImplicit(true)
lc.setDefaultVersion("master")
folder.addProperty(new FolderLibraries([lc]))
folder.setDescription("This directory belongs to the Jenkins administrator. " +
    "By default he is eligible to run jobs from this Folder on the Master")
FolderOwnershipHelper.setOwnership(folder, new OwnershipDescription(true, "admin"))

// Add a sample project
WorkflowJob project = folder.createProject(WorkflowJob.class, "Ownership_Plugin_System_Master")
project.setDefinition(new CpsFlowDefinition("buildPlugin(platforms: ['master'], repo: 'https://github.com/jenkinsci/ownership-plugin.git')", true))
JobOwnerHelper.setOwnership(project, new OwnershipDescription(true, "admin"))
