import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import com.synopsys.arc.jenkins.plugins.ownership.nodes.NodeOwnerHelper
import com.synopsys.arc.jenkins.plugins.ownership.util.ui.UserSelector
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestriction
import jenkins.model.Jenkins
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.nodes.JobRestrictionProperty;
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.logic.OrJobRestriction
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.logic.MultipleAndJobRestriction
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.job.RegexNameRestriction
import com.synopsys.arc.jenkins.plugins.ownership.security.jobrestrictions.OwnersListJobRestriction
import io.jenkins.plugins.jobrestrictions.restrictions.job.JobClassNameRestriction
import io.jenkins.plugins.jobrestrictions.util.ClassSelector

println("== Configuring Master computer")

// Admin owns the node
NodeOwnerHelper.setOwnership(Jenkins.instance, new OwnershipDescription(true, "admin"))

// Job restrictions
boolean allowRunsOnMaster = Boolean.getBoolean("io.jenkins.dev.security.allowRunsOnMaster")
if (allowRunsOnMaster) {
    // It is not that bad since we have Authorize Project enabled
    // Only admin will be eligible to run anything on the Master
    // TODO: test and confirm
    println("Runs on Master are enabled. It is a bad idea from the security standpoint")
    return;
}

// We allow running jobs in the SystemFolder owned by admin + whitelisted job types
// TODO: Job Restrictions API polishing would be really useful
OwnersListJobRestriction ownedByAdmin = new OwnersListJobRestriction(
        Arrays.asList(new UserSelector("admin")), false);
RegexNameRestriction inSystemFolder = new RegexNameRestriction("^System/.+", false);

ClassSelector workflowJob = new ClassSelector(org.jenkinsci.plugins.workflow.job.WorkflowJob.class.name);
JobClassNameRestriction whitelistedClasses = new JobClassNameRestriction(Arrays.asList(workflowJob));

Jenkins.instance.getNodeProperties().add(
        new JobRestrictionProperty(new OrJobRestriction(
                new MultipleAndJobRestriction(new ArrayList<JobRestriction>(Arrays.asList(ownedByAdmin, inSystemFolder))),
                whitelistedClasses)))
