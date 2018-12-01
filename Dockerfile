FROM jenkins/jenkins:2.138.3
MAINTAINER Oleg Nenashev <o.v.nenashev@gmail.com>
LABEL Description="This demo shows how to setup Jenkins Config-as-Code with Docker, Pipeline, and Groovy Hook Scripts" Vendor="Oleg Nenashev" Version="0.2"

USER root

# Maven
ENV MAVEN_VERSION 3.5.3
RUN curl -Lf http://central.maven.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -C /opt -xzv
ENV M2_HOME /opt/apache-maven-$MAVEN_VERSION
ENV maven.home $M2_HOME
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH

# JDK11
RUN curl -L --show-error https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_linux-x64_bin.tar.gz --output openjdk.tar.gz && \
    echo "7a6bb980b9c91c478421f865087ad2d69086a0583aeeb9e69204785e8e97dcfd  openjdk.tar.gz" | sha256sum -c && \
    tar xvzf openjdk.tar.gz && \
    mv jdk-11.0.1/ /usr/lib/jvm/java-11-opendjdk-amd64 && \
    rm openjdk.tar.gz

USER jenkins