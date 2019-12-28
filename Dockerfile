FROM jenkins/jenkins:lts-alpine

LABEL maintainer="wolver.minion"
LABEL Description="Setup Jenkins Config-as-Code with Docker, Pipeline, and Groovy Hook Scripts. thx Oleg" Vendor="Wolver" Version="0.1"


USER root

RUN apk add --no-cache --update openssl && \
    rm -rf /var/cache/apk/*

USER ${user}

# all  plugins
COPY master/plugins.txt ${REF}/plugins.txt
RUN /usr/local/bin/install-plugins.sh < ${REF}/plugins.txt

# hook scripts
COPY init_scripts/src/main/groovy/ ${REF}/init.groovy.d/

# TODO: It should be configurable in "docker run"
ARG DEV_HOST=localhost

ARG CREATE_ADMIN=true
ARG ALLOW_RUNS_ON_MASTER=false
ARG LOCAL_PIPELINE_LIBRARY_PATH=${JENKINS_HOME}/pipeline-library

ENV CONF_CREATE_ADMIN=$CREATE_ADMIN
ENV CONF_ALLOW_RUNS_ON_MASTER=$ALLOW_RUNS_ON_MASTER

# Directory  Pipeline Library development
ENV LOCAL_PIPELINE_LIBRARY_PATH=${LOCAL_PIPELINE_LIBRARY_PATH}
RUN mkdir -p ${LOCAL_PIPELINE_LIBRARY_PATH}

#COPY container/jenkins/configuration/ ${JENKINS_HOME}/

ENV CASC_JENKINS_CONFIG=${JENKINS_HOME}/casc_configs/jenkins.yaml
COPY master/jenkins.yaml $CASC_JENKINS_CONFIG

COPY master/jenkins-cx.sh /usr/local/bin/jenkins-cx.sh

ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/jenkins-cx.sh"]
