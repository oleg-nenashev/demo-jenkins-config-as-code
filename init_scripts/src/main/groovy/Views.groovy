// Initializes Views on the instance, including the default one.
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerColumn
import com.synopsys.arc.jenkins.plugins.ownership.jobs.OwnershipJobFilter
import jenkins.model.Jenkins
import hudson.model.ListView

// Additional view, which also shows owners
ListView withOwnership = new ListView("With ownership")
withOwnership.setIncludeRegex(".*")
List<hudson.views.ListViewColumn> columns = new LinkedList<>(withOwnership.getColumns())
columns.add(3, new JobOwnerColumn());
withOwnership.setColumns(columns);
Jenkins.instance.addView(withOwnership)

// Default view, which recursively shows jobs owned by the current user
ListView myJobs = new ListView("My jobs")
myJobs.setIncludeRegex(".*")
myJobs.setRecurse(true)
columns = new LinkedList<>(withOwnership.getColumns())
columns.add(3, new JobOwnerColumn());
withOwnership.setColumns(columns);
myJobs.getJobFilters().add(new OwnershipJobFilter("@Me", true))
Jenkins.instance.addView(myJobs)

Jenkins.instance.setPrimaryView(myJobs)
