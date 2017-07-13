FROM jenkinsci/jenkins:2.60.1

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY init_scripts/src/main/groovy/ /usr/share/jenkins/ref/init.groovy.d/

# TODO: It should be configurable in "docker run"
ARG DEV_HOST=192.168.101.57
ARG CREATE_ADMIN=true

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Djenkins.model.Jenkins.slaveAgentPort=50000 -Djenkins.model.Jenkins.slaveAgentPortEnforce=true  -Dio.jenkins.dev.security.createAdmin=${CREATE_ADMIN} -Dio.jenkins.dev.host=${DEV_HOST}"
