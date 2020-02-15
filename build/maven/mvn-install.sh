#!/bin/bash -ex
BASEDIR=$(dirname "$0")

bash ${BASEDIR}/mvn-init.sh "$@"

runMvnw 'clean install'