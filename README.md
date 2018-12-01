ci.jenkins.io-runner
===

This project offers environment for running Jenkinsfile instances from ci.jenkins.io locally.
It is powered by Jenkinsfile Runner and Custom WAR Packager.

### Quickstart

1. Checkout this repo
2. Run `make docker` to build the base image
3. Run `make clean build` to build the Jenkinsfile Runner image
4. Run `make run` to run a simple demo
5. Run `make demo-plugin` to run a demo of the plugin build
  * It will not work right now, see Limitations

### Developing Jenkins Pipeline library

Jenkins Pipeline library may be passed from a volume so that it is possible to test a local snapshot.

```
	docker run --rm -v maven-repo:/root/.m2 \
	    -v ${MY_PIPELINE_LIBRARY_DIR}:/var/jenkins_home/pipeline-library \
	    -v $(shell pwd)/demo/locale-plugin/:/workspace/ \
	    $(DOCKER_TAG)
```

### Limitations

* A custom fork of Jenkins Pipeline Library is needed to run it
  * Follow https://github.com/jenkins-infra/pipeline-library/pull/78
* `ci.jenkins.io-runner` is a single-container package with only 1 executor
* Only JDK8 and JDK11 are provided in the image
* Windows steps are not supported
* Docker-in-Docker is not supported. Steps like `runATH()` and `runPCT()` will not work
