import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.Permission
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

// https://github.com/jenkinsci/ownership-plugin/blob/master/doc/OwnershipBasedSecurity.md#role-based-strategy-integration
class OwnershipBasedSecurityHelper {

    static RoleMap getGlobalAdminAndAnonymousRoles() {
        Set<Permission> adminPermissions = new HashSet<Permission>()
        adminPermissions.add(Jenkins.ADMINISTER)
        Role adminRole = createRole("administrator", ".*", adminPermissions)

        Set<Permission> anonymousPermissions = new HashSet<Permission>([Jenkins.READ, Item.READ, Item.DISCOVER])
        Role anonymousRole = createRole("anonymous", ".*", anonymousPermissions)

        //TODO: This is a weird hack, which allows running WorkflowRun instances any node
        //TODO: Jenkins.getACL() returns RootACL for node, hence we cannot use Node-specific security settings
        // We need it to Run Pipeline Jobs. Ideally a RoleStrategy macro should be created.
        // If "-Dio.jenkins.dev.security.allowRunsOnMaster" is "false", the Master node will be protected by
        // Job Restrictions settings. Nodes have to be protected by Job Restrictions as well.
        // Otherwise any user will be able to run whatever stuff on that nodes...
        Set<Permission> masterBuildPermission = new HashSet<Permission>([Computer.BUILD])
        Role nodeBuildKillSwitch = createRole("BuildAnythingOnNode", ".*", masterBuildPermission)

        final SortedMap<Role, Set<String>> grantedRoles = new TreeMap<Role, Set<String>>()
        grantedRoles.put(adminRole, singleSid("admin"))
        grantedRoles.put(anonymousRole, singleSid("anonymous"))
        grantedRoles.put(nodeBuildKillSwitch, singleSid("authenticated"))

        return new RoleMap(grantedRoles)
    }

    static RoleMap getProjectRoleMap() {
        Set<Permission> ownerPermissions = new HashSet<Permission>()
        // Disabled: Ownership settings come from the directory, and we do not want the user to work with them
        // ownerPermissions.add(com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin.MANAGE_ITEMS_OWNERSHIP);
        ownerPermissions.addAll(Item.PERMISSIONS.permissions)
        ownerPermissions.addAll(Run.PERMISSIONS.permissions)
        Role ownerRole = createRole("@OwnerNoSid", ".*", ownerPermissions)

        Set<Permission> coownerPermissions = new HashSet<Permission>()
        coownerPermissions.addAll(Item.PERMISSIONS.permissions)
        coownerPermissions.addAll(Run.PERMISSIONS.permissions)
        coownerPermissions.removeAll([Item.DELETE, Run.DELETE])
        Role coOwnerRole = createRole("@CoOwnerNoSid", ".*", coownerPermissions)

        return createRoleMapForSid("authenticated", ownerRole, coOwnerRole)
    }

    static RoleMap getComputerRoleMap() {
        Set<Permission> ownerPermissions = new HashSet<Permission>()
        // Disabled: Ownership settings for agents are managed by Config-as-Code
        // ownerPermissions.add(com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin.MANAGE_SLAVES_OWNERSHIP);
        ownerPermissions.addAll(Computer.PERMISSIONS.getPermissions())
        Role ownerRole = createRole("@OwnerNoSid", ".*", ownerPermissions)

        Set<Permission> coownerPermissions = new HashSet<Permission>()
        coownerPermissions.addAll(Computer.PERMISSIONS.getPermissions())
        coownerPermissions.removeAll([Computer.DELETE, Computer.CONFIGURE])
        Role coOwnerRole = createRole("@CoOwnerNoSid", ".*", coownerPermissions)

        return createRoleMapForSid("authenticated", ownerRole, coOwnerRole)
    }

    // TODO: Should be replaced by RoleStrategy API
    private static Role createRole(String name, String pattern, Set<Permission> permissions) {
        return new Role(name, pattern, permissions)
    }

    private static RoleMap createRoleMapForSid(String sid, Role... roles) {
        final SortedMap<Role, Set<String>> grantedRoles = new TreeMap<Role, Set<String>>()
        for (Role role : roles) {
            grantedRoles.put(role, singleSid(sid))
        }
        return new RoleMap(grantedRoles)
    }

    private static Set<String> singleSid(String sid) {
        return new TreeSet<String>([sid])
    }

}

RoleBasedAuthorizationStrategy strategy = new RoleBasedAuthorizationStrategy()

Map<String, RoleMap> grantedRoles = new HashMap<String, RoleMap>()
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
