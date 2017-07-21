###
# Custom agent build:
# Image name: onenashev/jenkins-demo-maven-builder
###
FROM maven:3.5.0-jdk-8
MAINTAINER Oleg Nenashev <o.v.nenashev@gmail.com>

#ENV HOME /home/jenkins
#RUN groupadd -g 10000 jenkins
#RUN useradd -c "Jenkins user" -d $HOME -u 10000 -g 10000 -m jenkins
LABEL Description="This is n agent image for the configuration-as-code demo" Vendor="Oleg Nenashev" Version="0.1"

#USER jenkins
RUN mkdir /root/.jenkins
VOLUME /root/.jenkins
VOLUME /root/.m2
WORKDIR /root