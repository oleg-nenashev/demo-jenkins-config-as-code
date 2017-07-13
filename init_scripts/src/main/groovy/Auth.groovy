import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.RoleMap
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.Permission
import jenkins.model.Jenkins;
import hudson.model.*
import org.jenkinsci.plugins.authorizeproject.GlobalQueueItemAuthenticator
import org.jenkinsci.plugins.authorizeproject.strategy.TriggeringUsersAuthorizationStrategy

boolean createAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin");

println("=== Installing the Security Realm");
def securityRealm = new HudsonPrivateSecurityRealm(false);
securityRealm.createAccount("user", "user");
if (createAdmin) {
    securityRealm.createAccount("admin", "admin");
}
Jenkins.instance.setSecurityRealm(securityRealm);

println("=== Installing the Role-Based Authorization strategy");

// https://github.com/jenkinsci/ownership-plugin/blob/master/doc/OwnershipBasedSecurity.md#role-based-strategy-integration
public class OwnershipBasedSecurityHelper {

     static RoleMap getGlobalAdminAndAnonymousRoles() {
        Set<Permission> adminPermissions = new HashSet<Permission>();
        adminPermissions.add(Jenkins.ADMINISTER);
        Role adminRole = createRole("administrator", ".*", adminPermissions);

        Set<Permission> anonymousPermissions = new HashSet<Permission>();
        anonymousPermissions.add(Jenkins.READ);
        anonymousPermissions.add(Item.READ);
        anonymousPermissions.add(Item.DISCOVER);
        Role anonymousRole = createRole("anonymous", ".*", anonymousPermissions);

        final SortedMap<Role,Set<String>> grantedRoles = new TreeMap<Role, Set<String>>();
        grantedRoles.put(adminRole, singleSid("admin"));
        grantedRoles.put(anonymousRole, singleSid("anonymous"));

        return new RoleMap(grantedRoles);
    }

     static RoleMap getProjectRoleMap() {
        Set<Permission> ownerPermissions = new HashSet<Permission>();
        ownerPermissions.add(com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin.MANAGE_ITEMS_OWNERSHIP);
        ownerPermissions.addAll(Item.PERMISSIONS.getPermissions());
        ownerPermissions.addAll(Run.PERMISSIONS.getPermissions());
        Role ownerRole = createRole("@OwnerNoSid", ".*", ownerPermissions);

        Set<Permission> coownerPermissions = new HashSet<Permission>();
        coownerPermissions.addAll(Item.PERMISSIONS.getPermissions());
        coownerPermissions.addAll(Run.PERMISSIONS.getPermissions());
        coownerPermissions.remove(Item.DELETE);
        coownerPermissions.remove(Run.DELETE);
        Role coOwnerRole = createRole("@CoOwnerNoSid", ".*", coownerPermissions);

        return createRoleMapForSid("authenticated", ownerRole, coOwnerRole);
    }

     static RoleMap getComputerRoleMap() {
        Set<Permission> ownerPermissions = new HashSet<Permission>();
        ownerPermissions.add(com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin.MANAGE_SLAVES_OWNERSHIP);
        ownerPermissions.addAll(Computer.PERMISSIONS.getPermissions());
        Role ownerRole = createRole("@OwnerNoSid", ".*", ownerPermissions);

        Set<Permission> coownerPermissions = new HashSet<Permission>();
        coownerPermissions.addAll(Computer.PERMISSIONS.getPermissions());
        coownerPermissions.remove(Computer.DELETE);
        coownerPermissions.remove(Computer.CONFIGURE);
        Role coOwnerRole = createRole("@CoOwnerNoSid", ".*", coownerPermissions);

        return createRoleMapForSid("authenticated", ownerRole, coOwnerRole);
    }

    // TODO: Should be replaced by RoleStrategy API
    private static Role createRole(String name, String pattern, Set<Permission> permissions) {
        Set<Permission> permSet = new HashSet<Permission>();
        for (Permission p : permissions) {
            permSet.add(p);
        }
        return new Role(name, pattern, permSet);
    }

    private static RoleMap createRoleMapForSid(String sid, Role ... roles) {
        final SortedMap<Role,Set<String>> grantedRoles = new TreeMap<Role, Set<String>>();
        for (Role role : roles) {
            grantedRoles.put(role, singleSid(sid));
        }
        return new RoleMap(grantedRoles);
    }

    private static Set<String> singleSid(String sid) {
        final Set<String> sids = new TreeSet<String>();
        sids.add(sid);
        return sids;
    }

}

RoleBasedAuthorizationStrategy strategy = new RoleBasedAuthorizationStrategy();

Map<String,RoleMap> grantedRoles = new HashMap<String, RoleMap>();
grantedRoles.put(RoleType.Project.getStringType(), OwnershipBasedSecurityHelper.getProjectRoleMap());
grantedRoles.put(RoleType.Slave.getStringType(), OwnershipBasedSecurityHelper.getComputerRoleMap());
grantedRoles.put(RoleType.Global.getStringType(), OwnershipBasedSecurityHelper.getGlobalAdminAndAnonymousRoles());

strategy.@grantedRoles.putAll(grantedRoles);
Jenkins.instance.setAuthorizationStrategy(strategy);

println("=== Configure Authorize Project")
GlobalQueueItemAuthenticator auth = new GlobalQueueItemAuthenticator(
        new TriggeringUsersAuthorizationStrategy()
);
jenkins.security.QueueItemAuthenticatorConfiguration.get().getAuthenticators().add(auth);
