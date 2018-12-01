# Just a Makefile for manual testing
.PHONY: all

ARTIFACT_ID = jenkinsfile-runner-demo
VERSION = 256.0-test
CWP_MAVEN_REPO_PATH=io/jenkins/tools/custom-war-packager/custom-war-packager-cli
CWP_VERSION=1.5-20181201.004502-1
DOCKER_TAG=onenashev/ci.jenkins.io-runner

#TODO: Replace snapshot parsing by something more reliable
ifneq (,$(findstring 1.5-2018,$(CWP_VERSION)))
	CWP_MAVEN_REPO=https://repo.jenkins-ci.org/snapshots
	CWP_BASE_VERSION=1.5-SNAPSHOT
else
	CWP_MAVEN_REPO=https://repo.jenkins-ci.org/releases
	CWP_BASE_VERSION=$(CWP_VERSION)
endif

all: clean build

clean:
	rm -rf tmp

.build/cwp-cli-${CWP_VERSION}.jar:
	rm -rf .build
	mkdir -p .build
	wget -O .build/cwp-cli-${CWP_VERSION}.jar $(CWP_MAVEN_REPO)/${CWP_MAVEN_REPO_PATH}/${CWP_BASE_VERSION}/custom-war-packager-cli-${CWP_VERSION}-jar-with-dependencies.jar
	touch .build/cwp-cli-${CWP_VERSION}.jar

build: .build/cwp-cli-${CWP_VERSION}.jar
	java -jar .build/cwp-cli-${CWP_VERSION}.jar \
	     -configPath packager-config.yml -version ${VERSION}

run:
	docker run --rm -v $(shell pwd)/demo/simple/:/workspace/ \
	    $(DOCKER_TAG)
