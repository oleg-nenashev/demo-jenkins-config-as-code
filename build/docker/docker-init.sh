#!/bin/bash -ex
BASEDIR=$(dirname "$0")

WORKSPACE_DEFAULT='../..'


WORKSPACE=${1:-${WORKSPACE_DEFAULT}}

DOCKER_IMAGE_NAME='continuous-x/jenkins-cx'
DOCKER_IMAGE_VERSION=

runDocker() {
 GOALS=${1}
 docker ${GOALS} ${DOCKER_IMAGE_NAME} ${WORKSPACE}
}
