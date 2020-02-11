#!/bin/sh -ex
BASEDIR=$(dirname "$0")

. ${BASEDIR}/mvn-init.sh "$@"

runMvnw 'clean package'