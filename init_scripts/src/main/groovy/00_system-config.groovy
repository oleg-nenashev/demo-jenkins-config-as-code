import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

println """
###################################
# Hook - 00 system config (start) #
###################################
"""

def jenkins = Jenkins.getInstanceOrNull()
assert jenkins != null : "Jenkins instance is null"

if (!jenkins.isQuietingDown()){
    println("=== Setting Jenkins URL")
    String host = java.lang.System.getProperty("io.jenkins.dev.host")


    if (host == null) {
        host = "localhost"
    } else {
        JenkinsLocationConfiguration.get().setUrl("http://${host}:8080")
    }
} else {
    println '*** Shutdown mode enabled. Configure Jenkins is SKIPPED!'
}


println """
###################################
# Hook - 00 system config (end)   #
###################################
"""
