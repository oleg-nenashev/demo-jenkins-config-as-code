
import jenkins.model.Jenkins
import hudson.model.JDK
import hudson.tasks.Maven.MavenInstallation;
import hudson.tasks.Maven
import hudson.tools.InstallSourceProperty

println """
##############################
# boot - Tools Hook (start)  #
##############################
"""

println("--- Setup tool installations")
// By default we offer no JDK7, Nodes should override
JDK jdk7 = new JDK("jdk7", "/non/existent/JVM")
// Java 8 should be a default Java, because we require it for Jenkins 2.60.1+
JDK jdk8 = new JDK("jdk8", "")
Jenkins.getInstanceOrNull().getDescriptorByType(JDK.DescriptorImpl.class).setInstallations(jdk7, jdk8)


InstallSourceProperty p = new InstallSourceProperty([new Maven.MavenInstaller("3.6.3")])
MavenInstallation mvn = new MavenInstallation("mvn", null, [p])
Jenkins.getInstanceOrNull().getDescriptorByType(Maven.DescriptorImpl.class).setInstallations(mvn)

println """
##############################
# boot - Tools Hook (start)  #
##############################
"""
