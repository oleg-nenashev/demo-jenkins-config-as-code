Demo image for Jenkins Configuration-as-Code
===

[![Docker Build Status](https://img.shields.io/docker/build/onenashev/demo-jenkins-config-as-code.svg)](https://hub.docker.com/r/onenashev/demo-jenkins-config-as-code/)

[![Follow @oleg-nenashev](https://img.shields.io/twitter/follow/oleg_nenashev.svg?style=social)](https://twitter.com/intent/follow?screen_name=oleg_nenashev) 

This demo image shows how to establish full configuration-as-code in Jenkins with Docker, Pipeline, and 
[Groovy Hook Scripts](https://wiki.jenkins.io/display/JENKINS/Groovy+Hook+Script).
It offer a `GroovyBootstrap` logic which adds support of Groovy classes, debugging and correct error propagation from scripts.

This demo also brings up environment which can be used to develop Jenkins Pipeline libraries locally
and to evaluate Jenkins features like [Ownership-Based Security](https://github.com/jenkinsci/ownership-plugin/blob/master/doc/OwnershipBasedSecurity.md).

:exclamation: Warning! This image is not designed for production use.
Use it at your own risk.
Prototyping is in progress, compatibility of the scripts and Dockerfiles is **NOT GUARANTEED**.

### Features

Jenkins container starts with the following contents:

* Authentication: Internal database with four users. Passwords are same as user names
  * `admin` - Admin with full access
  * `manager` - User with `Jenkins/Manage` permissions
    ([JEP-223](https://github.com/jenkinsci/jep/tree/master/jep/223))
  * `readonly` - User with `Jenkins/SystemRead` and read-only permissions
    ([JEP-224](https://github.com/jenkinsci/jep/tree/master/jep/224)) -
    [announcement](https://www.jenkins.io/blog/2020/05/25/read-only-jenkins-announcement/)
  * `user` - User with ability to run jobs
* Authorization: 
  * [Ownership-Based Security](https://github.com/jenkinsci/ownership-plugin/blob/master/doc/OwnershipBasedSecurity.md), 
  powered by [Role Strategy](https://plugins.jenkins.io/role-strategy) 
  and [Ownership](https://plugins.jenkins.io/ownership) plugins
  * [Authorize Project](https://plugins.jenkins.io/authorize-project) is enabled by default
    * Runs will authorize as users who triggered the build

Jobs and Folders

* 3 Folders on the root level: _Production_, _Development_, _System_. Folders offer different permissions to users
* _Production_ and _System_ folders implicitly load the [ci.jenkins.io Pipeline Library](https://github.com/jenkins-infra/pipeline-library.git) 
* _Development_ folder contains sandbox folders where common users can create and test their jobs
* Each folder contains several reference Pipeline jobs

Nodes: 

* Master node is restricted for builds 
  * It is available only to System jobs started by the `admin` user, powered by [Job Restrictions Plugin](https://plugins.jenkins.io/job-restrictions)
* Extra agents with `linux` label are available from the Docker Cloud, 
powered by [Yet Another Docker Plugin](https://plugins.jenkins.io/yet-another-docker-plugin)
  * Maven cache is shared via the `maven-repo` volume
* Master and agents offer the `mvn` and `jdk8`tools

Extra UI Features:

* Two extra views, the default one shows only jobs owned by the user
* Locale is enforced to `en_US` by [Locale Plugin](https://plugins.jenkins.io/locale)
* [Security Inspector](https://plugins.jenkins.io/security-inspector) and [Monitoring](https://plugins.jenkins.io/monitoring) plugin offer extra information

### Usage

To start the demo instance, run the following command:

```shell
docker run --rm --name ci-jenkins-io-dev -v maven-repo:/root/.m2 -e DEV_HOST=${CURRENT_HOST} -p 8080:8080 -p 50000:50000 onenashev/demo-jenkins-config-as-code
```

The `DEV_HOST` environment variable is used to connect agents without using Docker-in-Docker.
If you use Docker for Mac or Docker for Windows,
use additional `socat` image for port forwarding
to ensure that you can connect to the Docker VM on these platforms.

```shell
docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 2376:2375 bobrik/socat TCP4-LISTEN:2375,fork,reuseaddr UNIX-CONNECT:/var/run/docker.sock
```

#### Developing Pipeline libraries

In the _Development_ folder there is a _PipelineLib_ folder, which allows local building and testing of the library.
This folder can be mapped to a local repository in order to develop the library without committing changes: 

```shell
docker run --rm --name ci-jenkins-io-dev -v maven-repo:/root/.m2 -v ${MY_PIPELINE_LIBRARY_DIR}:/var/jenkins_home/pipeline-library -v ${MY_OTHER_PIPELINE_LIBS_DIRS}:/var/jenkins_home/pipeline-libs -e DEV_HOST=${CURRENT_HOST} -p 8080:8080 -p 50000:50000  onenashev/demo-jenkins-config-as-code
```

Once started, you can just start editing the Pipeline library locally.
On every job start the changes will be reflected in the directory without committing anything.

##### Debugging Master

In order to debug the master, use the `-e DEBUG=true -p 5005:5005` when starting the container.
Jenkins will be suspended on the startup in such case.

If you open parent POM as a Maven project in your IDE, 
you will be also able to debug initialization Groovy scripts.

### Building images

#### Agents

Having a local agent build is a prerequisite for using the master
for high-speed builds with Maven repository caching.
For this purpose there is a custom Dockerfile in the `/agent` folder.

```shell
cd agent && docker build -t onenashev/demo-jenkins-maven-builder .
```

#### Master

Build image:

```shell
docker build -t onenashev/demo-jenkins-config-as-code .
```

### Release notes

See [GitHub releases](https://github.com/oleg-nenashev/demo-jenkins-config-as-code/releases).
