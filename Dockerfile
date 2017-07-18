FROM jenkinsci/jenkins:2.60.1

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY init_scripts/src/main/groovy/ /usr/share/jenkins/ref/init.groovy.d/

# TODO: It should be configurable in "docker run"
ARG DEV_HOST=192.168.101.57
ARG CREATE_ADMIN=true
# If false, only few runs can be actually executed on the master
# See JobRestrictions settings
ARG ALLOW_RUNS_ON_MASTER=false
ARG LOCAL_PIPELINE_LIBRARY_PATH=/var/jenkins_home/pipeline-library

# Directory for Pipeline Library development sample
RUN mkdir -p ${LOCAL_PIPELINE_LIBRARY_PATH}
ENV LOCAL_PIPELINE_LIBRARY_PATH=${LOCAL_PIPELINE_LIBRARY_PATH}

VOLUME /var/jenkins_home/pipeline-library

EXPOSE 5005
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Djenkins.model.Jenkins.slaveAgentPort=50000 -Djenkins.model.Jenkins.slaveAgentPortEnforce=true  -Dio.jenkins.dev.security.createAdmin=${CREATE_ADMIN} -Dio.jenkins.dev.security.allowRunsOnMaster=${ALLOW_RUNS_ON_MASTER} -Dio.jenkins.dev.host=${DEV_HOST} -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n"
