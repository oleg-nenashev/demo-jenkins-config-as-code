import hudson.tasks.Maven.MavenInstallation;
import hudson.tasks.Maven
import hudson.tools.InstallSourceProperty
import jenkins.model.Jenkins;
import hudson.model.JDK;

println("--- Setup tool installations")
// By default we offer no JDK7, Nodes should override
JDK jdk7 = new JDK("jdk7", "/non/existent/JVM");
// Java 8 should be a default Java, because we require it for Jenkins 2.60.1+
JDK jdk8 = new JDK("jdk8", "");
Jenkins.instance.getDescriptorByType(JDK.DescriptorImpl.class).setInstallations(jdk7, jdk8);


hudson.tools.InstallSourceProperty p = new InstallSourceProperty(Arrays.asList(new Maven.MavenInstaller("3.5.0")))
MavenInstallation mvn = new MavenInstallation("mvn", null, Arrays.asList(p));
Jenkins.instance.getDescriptorByType(Maven.DescriptorImpl.class).setInstallations(mvn);
