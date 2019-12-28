import jenkins.model.Jenkins
import jenkins.security.QueueItemAuthenticatorConfiguration
import hudson.model.*
import org.jenkinsci.plugins.authorizeproject.GlobalQueueItemAuthenticator
import org.jenkinsci.plugins.authorizeproject.strategy.TriggeringUsersAuthorizationStrategy

println """
#############################
# boot - Auth Hook (start)  #
#############################
"""

boolean createAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin")

println("=== Configuring users")
def securityRealm = Jenkins.getInstanceOrNull().getSecurityRealm()
User user = securityRealm.createAccount("user", "user")
user.setFullName("User")
if (createAdmin) {
    User admin = securityRealm.createAccount("admin", "admin")
    admin.setFullName("Admin")
}

println("=== Configure Authorize Project")
GlobalQueueItemAuthenticator auth = new GlobalQueueItemAuthenticator(
    new TriggeringUsersAuthorizationStrategy()
)
QueueItemAuthenticatorConfiguration.get().authenticators.add(auth)

println """
#############################
# boot - Auth Hook (end)    #
#############################
"""
