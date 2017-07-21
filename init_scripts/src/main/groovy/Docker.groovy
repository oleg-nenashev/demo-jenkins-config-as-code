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
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import hudson.model.Node.Mode;

println("=== Setting Jenkins URL")
String host = java.lang.System.getProperty("io.jenkins.dev.host")
if (host == null) {
    host = "localhost"
} else {
    JenkinsLocationConfiguration.get().setUrl("http://${host}:8080")
}

println("=== Installing Docker Cloud for Linux nodes")
final DockerSlaveTemplate defaultJnlpAgentTemplate = new DockerSlaveTemplate(
    labelString: "linux",
    maxCapacity: 10,
    mode: Mode.EXCLUSIVE,
    numExecutors: 1,
    launcher: new DockerComputerJNLPLauncher(
        user: "jenkins",
        jenkinsUrl: JenkinsLocationConfiguration.get().url,
        launchTimeout: 100,
        noCertificateCheck: true
    ),
    dockerContainerLifecycle: new DockerContainerLifecycle(
        image: "jenkins/jnlp-slave",
        pullImage: new DockerPullImage(
            pullStrategy: DockerImagePullStrategy.PULL_ONCE
        ),
        createContainer: new DockerCreateContainer(
            privileged: false,
            tty: false,
            volumes: [ //TODO: Make shared local Maven repo configurable
                "maven-repo:/root/.m2",
                "jar-cache:/root/.jenkins"
            ]
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

Jenkins.instance.clouds.add(
    new DockerCloud(
        "docker-cloud",
        [ defaultJnlpAgentTemplate ],
        10,
        //TODO: YAD Plugin does not work well with this image and Unix sockets. Would be useful to migrate
        new DockerConnector("tcp://${host}:2376"))
)
