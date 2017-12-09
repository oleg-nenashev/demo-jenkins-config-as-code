package io.jenkins.systemgroovy.plugins

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.plugins.filesystem_scm.FSSCM
import org.apache.commons.io.FilenameUtils
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.libs.FolderLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMRetriever

/**
 * Helper script for Pipeline job management.
 * @author Oleg Nenashev
 * @since TODO
 */
class PipelineHelper {

    static WorkflowJob createDemo(File file, Folder folder, String name = null, boolean useScmSource = true) {
        String jobName = name ?: FilenameUtils.removeExtension(file.name)
        def descriptionFile = new File(file.path.replace(".groovy", ".txt"))
        def descriptionText = descriptionFile.exists() ? descriptionFile.text : null

        if (useScmSource) {
            return createScmPipelineJob(jobName, folder, file, descriptionText)
        } else {
            return createPipelineJob(jobName, folder, file.text, true, descriptionText)
        }
    }

    static WorkflowJob createPipelineJob(String name, Folder folder, String script,
                                 boolean sandbox = true, String description = null) {
        def p = folder.createProject(WorkflowJob.class, name)
        if (description != null) {
            p.description = description
        }
        p.definition = new CpsFlowDefinition(
            script,
            sandbox
        )
        return p
    }

    static WorkflowJob createScmPipelineJob(String name, Folder folder, File script, String description = null) {
        def p = folder.createProject(WorkflowJob.class, name)
        if (description != null) {
            p.description = description
        }
        p.definition = new CpsScmFlowDefinition(
            new FSSCM(script.parent, false, false, null),
            script.name
        )
        return p
    }

    static WorkflowJob createBuildPluginJob(Folder folder, String repo, String organization = "jenkinsci", String nameSuffix = "", String args = null, String extras = "") {
        return createPipelineJob("${repo}${nameSuffix}", folder,
            "buildPlugin(platforms: ['linux'], repo: 'https://github.com/${organization}/${repo}.git' ${extras})",
            true, "Builds the ${repo} plugin"
        )
    }

    static def addLocalLibrary(Folder folder, String library, String name = null) throws IOException {
        def libraryPath = new File(library)
        if (!libraryPath.exists() || !libraryPath.isDirectory()) {
            throw new Error("Library path does not exist or it is not a directory: ${library}")
        }
        def libraryName = name ?: libraryPath.name
        def scm = new FSSCM(libraryPath.absolutePath, false, false, null)
        LibraryConfiguration lc = new LibraryConfiguration(libraryName, new SCMRetriever(scm))
        lc.with {
            implicit = true
            defaultVersion = "master"
        }
        folder.addProperty(new FolderLibraries([lc]))
    }
}
