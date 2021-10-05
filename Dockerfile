ARG JENKINS_VERSION=2.303.1
FROM jenkins/jenkins:$JENKINS_VERSION
LABEL Maintainer="Oleg Nenashev <o.v.nenashev@gmail.com>"
LABEL Description="This demo shows how to setup Jenkins Config-as-Code with Docker, Pipeline, and Groovy Hook Scripts"

USER root
RUN apt-get update && apt-get install -y wget

USER jenkins

ENV JENKINS_UC_EXPERIMENTAL=https://updates.jenkins.io/experimental
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

ENV CONF_CREATE_ADMIN=$CREATE_ADMIN
ENV CONF_ALLOW_RUNS_ON_MASTER=$ALLOW_RUNS_ON_MASTER

# Directory for Pipeline Library development sample
ENV LOCAL_PIPELINE_LIBRARY_PATH=${LOCAL_PIPELINE_LIBRARY_PATH}
RUN mkdir -p ${LOCAL_PIPELINE_LIBRARY_PATH}

VOLUME /var/jenkins_home/pipeline-library
VOLUME /var/jenkins_home/pipeline-libs
EXPOSE 5005

COPY jenkins2.sh /usr/local/bin/jenkins2.sh
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/jenkins.yaml
COPY jenkins.yaml /var/jenkins_home/jenkins.yaml
ENTRYPOINT ["tini", "--", "/usr/local/bin/jenkins2.sh"]
