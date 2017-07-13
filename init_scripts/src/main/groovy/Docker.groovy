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
String host = java.lang.System.getProperty("io.jenkins.dev.host");
if (host == null) {
    host = "localhost"
} else {
    JenkinsLocationConfiguration.get().setUrl("http://${host}:8080");
}

println("=== Installing Docker Cloud for Linux nodes")

//TODO: YAD Plugin does not work well with this image and Unix sockets. Would be useful to migrate

final DockerConnector connector = new DockerConnector("tcp://${host}:2376");
// TODO: Add credentials?
// connector.setCredentialsId(dockerServerCredentials.getId());

final DockerPullImage pullImage = new DockerPullImage();
// pullImage.setCredentialsId("");
pullImage.setPullStrategy(DockerImagePullStrategy.PULL_ALWAYS);

final DockerComputerJNLPLauncher launcher = new DockerComputerJNLPLauncher();
launcher.setLaunchTimeout(100);
launcher.setUser("jenkins");
launcher.setJenkinsUrl(JenkinsLocationConfiguration.get().getUrl());
launcher.setNoCertificateCheck(true);

final DockerCreateContainer createContainer = new DockerCreateContainer();
createContainer.setPrivileged(false);
createContainer.setTty(false);
//TODO: Make shared local Maven repo configurable
createContainer.setVolumes(Collections.singletonList("maven-repo:/root/.m2"));
//createContainer.setVolumesFrom(Collections.singletonList("maven-repo:/root/.m2"));

final DockerStopContainer stopContainer = new DockerStopContainer();
stopContainer.setTimeout(100);

final DockerRemoveContainer removeContainer = new DockerRemoveContainer();
removeContainer.setForce(true);
removeContainer.setRemoveVolumes(true);

final DockerContainerLifecycle containerLifecycle = new DockerContainerLifecycle();
containerLifecycle.setImage("jenkinsci/jnlp-slave");
containerLifecycle.setPullImage(pullImage);
containerLifecycle.setCreateContainer(createContainer);
containerLifecycle.setStopContainer(stopContainer);
containerLifecycle.setRemoveContainer(removeContainer);

final DockerSlaveTemplate dockerSlaveTemplate = new DockerSlaveTemplate();
dockerSlaveTemplate.setDockerContainerLifecycle(containerLifecycle);
dockerSlaveTemplate.setLabelString("linux");
dockerSlaveTemplate.setLauncher(launcher);
dockerSlaveTemplate.setMaxCapacity(10);
dockerSlaveTemplate.setMode(hudson.model.Node.Mode.EXCLUSIVE);
dockerSlaveTemplate.setNumExecutors(1);
dockerSlaveTemplate.setRetentionStrategy(new DockerOnceRetentionStrategy(30));

final ArrayList<DockerSlaveTemplate> dockerSlaveTemplates = new ArrayList<>();
dockerSlaveTemplates.add(dockerSlaveTemplate);

def yadCloud = new DockerCloud("docker-cloud", dockerSlaveTemplates, 10, connector);
Jenkins.instance.clouds.add(yadCloud);
