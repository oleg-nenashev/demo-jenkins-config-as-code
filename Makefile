# Just a Makefile for manual testing
.PHONY: all

ARTIFACT_ID = jenkinsfile-runner-demo
VERSION = 256.0-test
CWP_VERSION = 1.4
DOCKER_TAG=onenashev/ci.jenkins.io-runner

all: clean build

clean:
	rm -rf tmp

.build/cwp-cli-${CWP_VERSION}.jar:
	rm -rf .build
	mkdir -p .build
	wget -O .build/cwp-cli-${CWP_VERSION}.jar https://repo.jenkins-ci.org/releases/io/jenkins/tools/custom-war-packager/custom-war-packager-cli/${CWP_VERSION}/custom-war-packager-cli-${CWP_VERSION}-jar-with-dependencies.jar
	touch .build/cwp-cli-${CWP_VERSION}.jar

build: .build/cwp-cli-${CWP_VERSION}.jar
	java -jar .build/cwp-cli-${CWP_VERSION}.jar \
	     -configPath packager-config.yml -version ${VERSION}

run:
	docker run --rm -v $(shell pwd)/demo/simple/:/workspace/ \
	    $(DOCKER_TAG)
