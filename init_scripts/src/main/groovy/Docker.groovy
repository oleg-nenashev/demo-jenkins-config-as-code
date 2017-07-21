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

println("=== Setting Jenkins URL")
String host = java.lang.System.getProperty("io.jenkins.dev.host")
if (host == null) {
    host = "localhost"
} else {
    JenkinsLocationConfiguration.get().setUrl("http://${host}:8080")
}

println("=== Installing Docker Cloud for Linux nodes")
final DockerSlaveTemplate defaultJnlpAgentTemplate = new DockerSlaveTemplate(
    dockerContainerLifecycle: new DockerContainerLifecycle(
        image: "jenkinsci/jnlp-slave",
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
    labelString: "linux",
    launcher: new DockerComputerJNLPLauncher(
        launchTimeout: 100,
        user: "jenkins",
        jenkinsUrl: JenkinsLocationConfiguration.get().url,
        noCertificateCheck: true
    ),
    maxCapacity: 10,
    mode: hudson.model.Node.Mode.EXCLUSIVE,
    numExecutors: 1,
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
