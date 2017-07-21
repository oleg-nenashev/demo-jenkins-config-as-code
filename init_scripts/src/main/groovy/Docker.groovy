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

//TODO: YAD Plugin does not work well with this image and Unix sockets. Would be useful to migrate

final DockerConnector connector = new DockerConnector("tcp://${host}:2376")

final DockerPullImage pullImageOpt = new DockerPullImage()
pullImageOpt.pullStrategy = DockerImagePullStrategy.PULL_ONCE

final DockerComputerJNLPLauncher jnlpLauncher = new DockerComputerJNLPLauncher()
jnlpLauncher.with {
    launchTimeout = 100
    user = "jenkins"
    jenkinsUrl = JenkinsLocationConfiguration.get().getUrl()
    noCertificateCheck = true
}

final DockerCreateContainer createContainerOpt = new DockerCreateContainer()
createContainerOpt.with {
    privileged = false
    tty = false
    //TODO: Make shared local Maven repo configurable
    volumes = Arrays.asList("maven-repo:/root/.m2", "jar-cache:/root/.jenkins")
}

final DockerStopContainer stopContainerOpt = new DockerStopContainer()
stopContainerOpt.with {
    timeout = 100
}

final DockerRemoveContainer removeContainerOpt = new DockerRemoveContainer()
removeContainerOpt.with {
    force = true
    removeVolumes = true
}

final DockerContainerLifecycle containerLifecycle = new DockerContainerLifecycle()
containerLifecycle.with {
    image = "jenkinsci/jnlp-slave"
    pullImage = pullImageOpt
    createContainer = createContainerOpt
    stopContainer = stopContainerOpt
    removeContainer = removeContainerOpt
}

final DockerSlaveTemplate dockerSlaveTemplate = new DockerSlaveTemplate()
dockerSlaveTemplate.with {
    dockerContainerLifecycle = containerLifecycle
    labelString = "linux"
    launcher = jnlpLauncher
    maxCapacity = 10
    mode = hudson.model.Node.Mode.EXCLUSIVE
    numExecutors = 1
    retentionStrategy = new DockerOnceRetentionStrategy(30)
}

final ArrayList<DockerSlaveTemplate> dockerSlaveTemplates = new ArrayList<>()
dockerSlaveTemplates.add(dockerSlaveTemplate)

def yadCloud = new DockerCloud("docker-cloud", dockerSlaveTemplates, 10, connector)
Jenkins.instance.clouds.add(yadCloud)
