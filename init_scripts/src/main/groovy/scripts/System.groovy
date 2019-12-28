import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
//import jenkins.CLI //class is deleted
import jenkins.security.s2m.AdminWhitelistRule
import org.kohsuke.stapler.StaplerProxy
import hudson.tasks.Mailer
import hudson.plugins.locale.PluginImpl

println """
###############################
# boot - System Hook (start)  #
###############################
"""

println("-- System configuration")

// TODO: Configure Job Restrictions, Script Security, Authorize Project, etc., etc.
println("--- Configuring Remoting (JNLP4 only, no Remoting CLI)")
//CLI.get().enabled = false //class is deleted
Jenkins.getInstanceOrNull().getExtensionList(StaplerProxy.class)
    .get(AdminWhitelistRule.class)
    .masterKillSwitch = false

println("--- Configuring Quiet Period")
// We do not wait for anything
Jenkins.getInstanceOrNull().quietPeriod = 0

println("--- Configuring Email global settings")
JenkinsLocationConfiguration.get().adminAddress = "admin@non.existent.email"
Mailer.descriptor().defaultSuffix = "@non.existent.email"

println("--- Configuring Locale")
//TODO: Create ticket to get better API
PluginImpl localePlugin = (PluginImpl)Jenkins.getInstanceOrNull().getPlugin("locale")
localePlugin.systemLocale = "en_US"
localePlugin.@ignoreAcceptLanguage=true

println """
###############################
# boot - System Hook (end)    #
###############################
"""
