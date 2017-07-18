import jenkins.model.Jenkins
import jenkins.CLI
import org.kohsuke.stapler.StaplerProxy
import hudson.tasks.Mailer

println("-- System configuration")

// TODO: Configure Job Restrictions, Script Security, Authorize Project, etc., etc.
println("--- Configuring Remoting (JNLP4, no Remoting CLI)")
CLI.get().setEnabled(false);
Jenkins.instance.setAgentProtocols(new HashSet<String>(Arrays.asList("JNLP4-connect")));
Jenkins.instance.getExtensionList(StaplerProxy.class)
        .get(jenkins.security.s2m.AdminWhitelistRule.class)
        .setMasterKillSwitch(false)


println("--- Configuring Quiet Period")
// We do not wait for anything
Jenkins.instance.setQuietPeriod(0)

println("--- Configuring Email global settings")
jenkins.model.JenkinsLocationConfiguration.get().setAdminAddress("admin@non.existent.email")
Mailer.descriptor().setDefaultSuffix("@non.existent.email")

println("--- Configuring Locale")
//TODO: Create ticket to get better API
hudson.plugins.locale.PluginImpl localePlugin = Jenkins.instance.getPlugin("locale")
localePlugin.setSystemLocale("en_US")
localePlugin.@ignoreAcceptLanguage=true
