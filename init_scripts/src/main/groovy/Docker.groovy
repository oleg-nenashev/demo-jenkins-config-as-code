import com.github.kostyasha.yad.DockerCloud
import com.github.kostyasha.yad.DockerConnector
import com.github.kostyasha.yad.DockerContainerLifecycle
import com.github.kostyasha.yad.DockerSlaveTemplate
import com.github.kostyasha.yad.commons.DockerCreateContainer
import com.github.kostyasha.yad.commons.DockerImagePullStrategy
import com.github.kostyasha.yad.commons.DockerPullImage
import com.github.kostyasha.yad.commons.DockerRemoveContainer
import com.github.kostyasha.yad.commons.DockerStopContainer
import com.github.kostyasha.yad.launcher.DockerComputerJNLPLauncher
import com.github.kostyasha.yad.strategy.DockerOnceRetentionStrategy
import hudson.tasks.Maven.MavenInstallation
import hudson.tools.ToolLocationNodeProperty
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import hudson.model.Node.Mode

println("=== Setting Jenkins URL")
String host = java.lang.System.getProperty("io.jenkins.dev.host")
if (host == null) {
    host = "localhost"
} else {
    JenkinsLocationConfiguration.get().setUrl("http://${host}:8080")
}

println("=== Installing Docker Cloud for Linux nodes")
static DockerSlaveTemplate fromTemplate(String image) {
    return new DockerSlaveTemplate(
        maxCapacity: 10,
        mode: Mode.EXCLUSIVE,
        numExecutors: 1,
        launcher: new DockerComputerJNLPLauncher(
            jenkinsUrl: JenkinsLocationConfiguration.get().url,
            launchTimeout: 100,
            noCertificateCheck: true
        ),
        dockerContainerLifecycle: new DockerContainerLifecycle(
            image: image,
            pullImage: new DockerPullImage(
                pullStrategy: DockerImagePullStrategy.PULL_ONCE
            ),
            createContainer: new DockerCreateContainer(
                privileged: false,
                tty: false
            ),
            stopContainer: new DockerStopContainer(
                timeout: 100
            ),
            removeContainer: new DockerRemoveContainer(
                force: true,
                removeVolumes: true
            )
        ),
        retentionStrategy: new DockerOnceRetentionStrategy(30)
    )
}

// Default agent image
final DockerSlaveTemplate defaultJnlpAgentTemplate = fromTemplate("jenkins/jnlp-slave")
defaultJnlpAgentTemplate.with {
    // Default label to "docker", no caching
    // User - jenkins (default)
}

// Custom image for Maven builds
MavenInstallation.DescriptorImpl mavenDescriptor = Jenkins.instance.getDescriptorByType(MavenInstallation.DescriptorImpl.class);
final DockerSlaveTemplate mavenBuilderTemplate = fromTemplate("onenashev/demo-jenkins-maven-builder")
mavenBuilderTemplate.with {
    labelString = "docker linux mvnBuilder"
    remoteFs = "/root"
    ((DockerComputerJNLPLauncher)launcher).user = "root"
    //TODO: Make volume names configurable
    dockerContainerLifecycle.createContainer.volumes = ["maven-repo:/root/.m2", "jar-cache:/root/.jenkins"]
    nodeProperties = [
        new ToolLocationNodeProperty(
            // Maven from the parent Maven image, we do not want to run the installer each time
            new ToolLocationNodeProperty.ToolLocation(mavenDescriptor,"mvn", "/usr/share/maven")
        )
    ]

}


Jenkins.instance.clouds.add(
    new DockerCloud(
        "docker-cloud",
        [ defaultJnlpAgentTemplate, mavenBuilderTemplate ],
        10,
        //TODO: YAD Plugin does not work well with this image and Unix sockets. Would be useful to migrate
        new DockerConnector("tcp://${host}:2376"))
)
