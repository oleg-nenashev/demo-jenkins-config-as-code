// Initializes the production jobs directory, which runs with deployed scripts and repos

import jenkins.model.Jenkins;
import com.cloudbees.hudson.plugins.folder.Folder
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.libs.FolderLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

println("=== Initialize the production Folder")
if (Jenkins.instance.getItem("Production") != null) {
    println("Production folder has been already initialized, skipping the step");
    return;
}

def folder = Jenkins.instance.createProject(Folder.class, "Production");

// Include https://github.com/jenkins-infra/pipeline-library
def pipelineLibrarySource = new GitSCMSource("pipeline-library", "https://github.com/jenkins-infra/pipeline-library.git", null, null, null, false)
LibraryConfiguration lc = new LibraryConfiguration("pipeline-library", new SCMSourceRetriever(pipelineLibrarySource))
lc.setImplicit(true)
lc.setDefaultVersion("master");
folder.addProperty(new FolderLibraries(Arrays.asList(lc)));

// Add a sample project
WorkflowJob project1 = folder.createProject(WorkflowJob.class, "Ownership_Plugin_Agent")
project1.setDefinition(new CpsFlowDefinition("buildPlugin(platforms: ['linux'], repo: 'https://github.com/jenkinsci/ownership-plugin.git')"));

WorkflowJob project2 = folder.createProject(WorkflowJob.class, "Ownership_Plugin_Master")
project2.setDefinition(new CpsFlowDefinition("buildPlugin(platforms: ['master'], repo: 'https://github.com/jenkinsci/ownership-plugin.git')"));


// TODO: Add Multi-Branch project, which does not build with Windows
