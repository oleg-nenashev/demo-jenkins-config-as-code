// Initializes Views on the instance, including the default one.
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerColumn
import com.synopsys.arc.jenkins.plugins.ownership.jobs.OwnershipJobFilter
import jenkins.model.Jenkins
import hudson.model.ListView
import hudson.views.ListViewColumn

// Additional view, which also shows owners
ListView withOwnership = new ListView("With ownership")
List<ListViewColumn> columns = new LinkedList<>(withOwnership.getColumns())
columns.add(3, new JobOwnerColumn())
withOwnership.with {
    includeRegex = ".*"
    columns = columns
}
Jenkins.instance.addView(withOwnership)

// Default view, which recursively shows jobs owned by the current user
ListView myJobs = new ListView("My jobs")
columns = new LinkedList<>(withOwnership.getColumns())
columns.add(3, new JobOwnerColumn())
myJobs.with {
    includeRegex = ".*"
    recurse = true
    columns = columns
}
myJobs.jobFilters.add(new OwnershipJobFilter("@Me", true))
Jenkins.instance.addView(myJobs)

Jenkins.instance.setPrimaryView(myJobs)
