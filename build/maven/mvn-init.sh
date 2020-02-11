#!/bin/sh -ex
BASEDIR=$(dirname "$0")

MAVEN_SETTINGS_DEFAULT='.mvn/wrapper/settings.xml'
MAVEN_REPOSITORY_DEFAULT="${HOME}\.m2\repository"
WORKSPACE_DEFAULT='../..'
GITHUB_USER_DEFAULT='username'
GITHUB_PWD_DEFAULT='xxxxxx'

if test -v ${MAVEN_REPOSITORY}; then
    MAVEN_REPOSITORY_DEFAULT=${MAVEN_REPOSITORY}
fi
if test -v ${GITHUB_USERNAME}; then
    GITHUB_USER_DEFAULT=${GITHUB_USERNAME}
fi
if test -v ${GITHUB_PASSWORD}; then
    GITHUB_PWD_DEFAULT=${GITHUB_PASSWORD}
fi

export MAVEN_REPOSITORY=${MAVEN_REPOSITORY_DEFAULT}
export MAVEN_SETTINGS=${MAVEN_SETTINGS_DEFAULT}

WORKSPACE=${1:-${WORKSPACE_DEFAULT}}
GITHUB_USER=${2:-${GITHUB_USER_DEFAULT}}
GITHUB_PWD=${3:-${GITHUB_PWD_DEFAULT}}

MAVEN_PARAMS="-s ${WORKSPACE}/${MAVEN_SETTINGS} -e"

chmod 777 ${WORKSPACE}/mvnw

runMvnw() {
 GOALS=${1}
 ${WORKSPACE}/mvnw ${GOALS} ${MAVEN_PARAMS}
}
