import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.Permission
import hudson.security.ProjectMatrixAuthorizationStrategy
import jenkins.model.Jenkins;
import hudson.model.*;

boolean creatAdmin = Boolean.getBoolean("io.jenkins.dev.security.createAdmin");

println("=== Installing the Security Realm");
def securityRealm = new HudsonPrivateSecurityRealm(false);
securityRealm.createAccount("user", "user");
if (creatAdmin) {
    securityRealm.createAccount("admin", "admin");
}
Jenkins.instance.setSecurityRealm(securityRealm);

println("=== Installing the auth strategy");
def strategy = new ProjectMatrixAuthorizationStrategy();
// TODO: Use Role Strategy, remove CONFIGURE on the top level
strategy.add(Permission.READ, "user");
strategy.add(Item.READ, "user");
strategy.add(Item.CONFIGURE, "user");
strategy.add(Item.CREATE, "user");
strategy.add(Item.BUILD, "user");
strategy.add(Item.DELETE, "user");
if(creatAdmin) {
    strategy.add(Jenkins.ADMINISTER, "admin")
}

strategy.add(Permission.READ, "user");
Jenkins.instance.setAuthorizationStrategy(strategy);
