import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType
import hudson.security.HudsonPrivateSecurityRealm
import io.jenkins.systemgroovy.plugins.OwnershipBasedSecurityHelper
import jenkins.model.Jenkins
import jenkins.security.QueueItemAuthenticatorConfiguration
import hudson.model.*
import org.jenkinsci.plugins.authorizeproject.GlobalQueueItemAuthenticator
import org.jenkinsci.plugins.authorizeproject.strategy.TriggeringUsersAuthorizationStrategy


boolean createAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin")

println("=== Installing the Security Realm")
def securityRealm = new HudsonPrivateSecurityRealm(false)
User user = securityRealm.createAccount("user", "user")
user.setFullName("User")
if (createAdmin) {
    User admin = securityRealm.createAccount("admin", "admin")
    admin.setFullName("Admin")
}
Jenkins.instance.setSecurityRealm(securityRealm)

println("=== Installing the Role-Based Authorization strategy")
RoleBasedAuthorizationStrategy strategy = new RoleBasedAuthorizationStrategy()
def grantedRoles = new HashMap<String, RoleMap>()
grantedRoles.put(RoleType.Project.stringType, OwnershipBasedSecurityHelper.projectRoleMap)
grantedRoles.put(RoleType.Slave.stringType, OwnershipBasedSecurityHelper.computerRoleMap)
grantedRoles.put(RoleType.Global.stringType, OwnershipBasedSecurityHelper.globalAdminAndAnonymousRoles)

strategy.@grantedRoles.putAll(grantedRoles)
Jenkins.instance.authorizationStrategy = strategy

println("=== Configure Authorize Project")
GlobalQueueItemAuthenticator auth = new GlobalQueueItemAuthenticator(
    new TriggeringUsersAuthorizationStrategy()
)
QueueItemAuthenticatorConfiguration.get().authenticators.add(auth)
