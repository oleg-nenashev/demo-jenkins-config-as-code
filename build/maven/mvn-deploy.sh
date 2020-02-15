#!/bin/sh -ex
BASEDIR=$(dirname "$0")

. ${BASEDIR}/mvn-init.sh "$@"

runMvnw '-Dmaven.wagon.http.pool=false clean deploy'
