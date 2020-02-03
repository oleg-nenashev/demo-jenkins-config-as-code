#!/bin/sh -e
BASEDIR=$(dirname "$0")

MAVEN_SETTINGS_DEFAULT='.mvn/wrapper/settings.xml'
WORKSPACE_DEFAULT='../..'
GITHUB_USER_DEFAULT='username'
GITHUB_PWD_DEFAULT='xxxxxx'
WORKSPACE=${1:-${WORKSPACE_DEFAULT}}
GITHUB_USER=${2:-${GITHUB_USER_DEFAULT}}
GITHUB_PWD=${3:-${GITHUB_PWD_DEFAULT}}
MAVEN_SETTINGS=${4:-${MAVEN_SETTINGS_DEFAULT}}



${WORKSPACE}/mvnw -Dmaven.wagon.http.pool=false clean deploy -s ${WORKSPACE}/${MAVEN_SETTINGS} -e  -Dgithub.username=${GITHUB_USER} -Dgithub.password=${GITHUB_PWD}