import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription
import com.synopsys.arc.jenkins.plugins.ownership.nodes.NodeOwnerHelper
import com.synopsys.arc.jenkins.plugins.ownership.util.ui.UserSelector
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import jenkins.model.Jenkins
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.nodes.JobRestrictionProperty
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.logic.OrJobRestriction
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.logic.MultipleAndJobRestriction
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.job.RegexNameRestriction
import com.synopsys.arc.jenkins.plugins.ownership.security.jobrestrictions.OwnersListJobRestriction
import io.jenkins.plugins.jobrestrictions.restrictions.job.JobClassNameRestriction
import io.jenkins.plugins.jobrestrictions.util.ClassSelector

println("== Configuring Master computer")

// Admin owns the node
NodeOwnerHelper.setOwnership(Jenkins.instance, new OwnershipDescription(true, "admin"))

