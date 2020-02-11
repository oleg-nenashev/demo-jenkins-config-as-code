#!/bin/sh -ex
BASEDIR=$(dirname "$0")

. ${BASEDIR}/mvn-init.sh "$@"

#  -Dgithub.username=${GITHUB_USER} -Dgithub.password=${GITHUB_PWD}"

runMvnw '-Dmaven.wagon.http.pool=false clean deploy'
