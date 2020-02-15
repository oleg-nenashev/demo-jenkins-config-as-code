import hudson.model.User
import hudson.security.SecurityRealm
import jenkins.model.Jenkins

println """
#############################
# boot - Auth Hook (start)  #
#############################
"""

boolean createAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin")

println("=== Configuring users")
SecurityRealm securityRealm = Jenkins.getInstanceOrNull().getSecurityRealm()
User user = securityRealm.createAccount("user", "user")
user.setFullName("User")
if (createAdmin) {
    User admin = securityRealm.createAccount("admin", "admin")
    admin.setFullName("Admin")
}

/*println("=== Configure Authorize Project")
GlobalQueueItemAuthenticator auth = new GlobalQueueItemAuthenticator(
    new TriggeringUsersAuthorizationStrategy()
)
QueueItemAuthenticatorConfiguration.get().authenticators.add(auth)*/

println """
#############################
# boot - Auth Hook (end)    #
#############################
"""
