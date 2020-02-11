#!/bin/sh -ex
BASEDIR=$(dirname "$0")

source ${BASEDIR}/mvn-init.sh "$@"

runMvnw 'clean package'