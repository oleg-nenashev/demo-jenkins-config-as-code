#!/bin/bash -ex
BASEDIR=$(dirname "$0")

VERSION_FILE='.version'

. ${BASEDIR}/mvn-init.sh "$@"

runMvnw "help:evaluate -Dexpression=project.version -Doutput=${VERSION_FILE}"

MAVEN_RELEASE_VERSION=$(cat ${VERSION_FILE} | cut -d'-' -f1)
echo ${MAVEN_RELEASE_VERSION} > ${VERSION_FILE}

runMvnw "versions:set -DnewVersion=${MAVEN_RELEASE_VERSION}"
