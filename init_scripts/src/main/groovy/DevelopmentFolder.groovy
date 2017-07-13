// Initializes the Development folder, which is fully configurable by the user


import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import jenkins.model.Jenkins;
import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper

println("=== Initialize the Development folder")
if (Jenkins.instance.getItem("Development") != null) {
    println("Development folder has been already initialized, skipping the step");
    return;
}

def folder = Jenkins.instance.createProject(Folder.class, "Development");
FolderOwnershipHelper.setOwnership(folder, new OwnershipDescription(true, "user"));
