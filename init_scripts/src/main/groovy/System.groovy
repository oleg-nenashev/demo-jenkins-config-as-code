import jenkins.model.Jenkins
import jenkins.CLI
import org.kohsuke.stapler.StaplerProxy

boolean allowRunsOnMaster = Boolean.getBoolean("io.jenkins.dev.security.allowRunsOnMaster");;

println("-- System configuration")

// TODO: Job Restrictions
if (allowRunsOnMaster) {
    println("Runs on Master are enabled. It is a bad idea from the security standpoint")
    //TODO: Should be another option when Job Restrictions are introduced.
}

// TODO: Configure Job Restrictions, Script Security, Authorize Project, etc., etc.
println("--- Configuring Remoting (JNLP4, no Remoting CLI)")
CLI.get().setEnabled(false);
Jenkins.instance.setAgentProtocols(new HashSet<String>(Arrays.asList("JNLP4-connect")));
Jenkins.instance.getExtensionList(StaplerProxy.class)
        .get(jenkins.security.s2m.AdminWhitelistRule.class)
        .setMasterKillSwitch(false)
