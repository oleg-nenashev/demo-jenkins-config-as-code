import jenkins.model.Jenkins
import jenkins.security.QueueItemAuthenticatorConfiguration
import hudson.model.*
import org.jenkinsci.plugins.authorizeproject.GlobalQueueItemAuthenticator
import org.jenkinsci.plugins.authorizeproject.strategy.TriggeringUsersAuthorizationStrategy


boolean createAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin")

println("=== Configuring users")
def securityRealm = Jenkins.instance.getSecurityRealm()
securityRealm.createAccount("user", "user").setFullName("User")
securityRealm.createAccount("readonly", "readonly").setFullName("Read-only Admin")
securityRealm.createAccount("manager", "manager").setFullName("Admin (Manage)")

if (createAdmin) {
    User admin = securityRealm.createAccount("admin", "admin")
    admin.setFullName("Admin (Full Permissions)")
}

println("=== Configure Authorize Project")
GlobalQueueItemAuthenticator auth = new GlobalQueueItemAuthenticator(
    new TriggeringUsersAuthorizationStrategy()
)
QueueItemAuthenticatorConfiguration.get().authenticators.add(auth)
