Demo image for Jenkins Configuration-as-Code
===

This demo image brings up environment which can be used for development of Jenkins Pipeline libraries for [ci.jenkins.io](https://ci.jenkins.io).

Main purpose of this image is to show how to establish full configuration-as-code in Jenkins with Docker, Pipeline, and 
[Groovy Hook Scripts](https://wiki.jenkins.io/display/JENKINS/Groovy+Hook+Script).

:exclamation: Warning! This image is not designed for any kinds of production use.
Use it at your own risk.
Prototyping is in progress.

### Features

Jenkins container starts with the following features:

* Authentication: Internal database with two users: `admin` and `user`
  * Passwords are same as user names
* Authorization: 
  * [Ownership-Based Security](https://github.com/jenkinsci/ownership-plugin/blob/master/doc/OwnershipBasedSecurity.md), powered by [Role Strategy Plugin](https://plugins.jenkins.io/role-strategy)
  * [Authorize Project](https://plugins.jenkins.io/authorize-project) is enabled by default
    * Runs will authorize as users who triggered the build

Jobs and Folders

* 3 Folders on the root level: _Production_, _Development_, _System_. Folders offer different permissions to users
* _Production_ and _System_ folders implicitly load the [ci.jenkins.io Pipeline Library](https://github.com/jenkins-infra/pipeline-library.git) 
* _Development_ folder contains sandbox folders where common users can create and test their jobs
* Each folder contains several reference Pipeline jobs

Nodes: 
* Master node has a restricted access 
  * It is available only to System jobs started by the `admin` user, powered by [Job Restrictions Plugin](https://plugins.jenkins.io/job-restrictions)
* Extra agents with `linux` label are available from Docker Cloud, powered by [Yet Another Docker Plugin](https://plugins.jenkins.io/yet-another-docker-plugin)
* Master and agents offer the `mvn` and `jdk8`tools

Extra UI Features:
* Two extra views, the default one shows only jobs owned by the user
* Locale is enforced to `en_US` by [Locale Plugin](https://plugins.jenkins.io/locale)
* [Security Inspector](https://plugins.jenkins.io/security-inspector) and [Monitoring](https://plugins.jenkins.io/monitoring) plugin offer extra information

### Usage

Currently the image is in the PoC mode. 
You need build the image with c

Build image:

```shell
docker build -t onenashev/ci-jenkins-io-dev --build-arg DEV_HOST=${CURRENT_HOST} .
```

Run image:

```shell
docker run --rm --name ci-jenkins-io-dev -v maven-repo:/root/.m2 -p 8080:8080 -p 50000:50000 -p 5005:5005 onenashev/ci-jenkins-io-dev 
```

Jenkins will need to connect to the Docker host to run agents.
If you use Docker for Mac, use `-Dio.jenkins.dev.host` and additional `socat` image for forwarding.

```shell
docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 2376:2375 bobrik/socat TCP4-LISTEN:2375,fork,reuseaddr UNIX-CONNECT:/var/run/docker.sock
```
