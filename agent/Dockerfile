###
# Custom agent build:
# Image name: onenashev/demo-jenkins-maven-builder
###
FROM maven:3.5.0-jdk-8
MAINTAINER Oleg Nenashev <o.v.nenashev@gmail.com>

LABEL Description="This is an agent image for the configuration-as-code demo" Vendor="Oleg Nenashev" Version="0.1"

#TODO: Consider moving image to the "jenkins" user instead of root
RUN mkdir /root/.jenkins
VOLUME /root/.jenkins
VOLUME /root/.m2
WORKDIR /root