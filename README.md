Demo image for Jenkins Configuration-as-Code
===

This demo image brings up environment which can be used for development of Jenkins Pipeline libraries for [ci.jenkins.io](https://ci.jenkins.io).

Features:
* Full configuration-as-code. It is possible to run image without users having `Jenkins/Administer` permissions

:exclamation: Warning! This image is not designed for any kinds of production use.
Use it at your own risk.
Prototyping is in progress.

### Usage

Build image:

```
docker build -t onenashev/ci-jenkins-io-dev .
```

Run image:

```
docker run --rm --name ci-jenkins-io-dev -v maven-repo:/root/.m2 -p 8080:8080 -p 50000:50000 -p 5005:5005 onenashev/ci-jenkins-io-dev 
```

Jenkins will need to connect to the Docker host to run agents.
If you use Docker for Mac, use `-Dio.jenkins.dev.host` and additional socat image for forwarding.

```
docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 2376:2375 bobrik/socat TCP4-LISTEN:2375,fork,reuseaddr UNIX-CONNECT:/var/run/docker.sock
```
