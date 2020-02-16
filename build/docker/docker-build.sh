#!/bin/bash euw
BASEDIR=$(dirname "$0")

. ${BASEDIR}/docker-init.sh "$@"

DOCKER_PARAMS='--no-cache --pull -t'

runDocker "build ${DOCKER_PARAMS}"