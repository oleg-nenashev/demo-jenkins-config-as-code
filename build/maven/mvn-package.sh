#!/bin/sh -e
BASEDIR=$(dirname "$0")

MAVEN_SETTINGS_DEFAULT='.mvn/wrapper/settings.xml'
WORKSPACE_DEFAULT='../..'
WORKSPACE=${1:-${WORKSPACE_DEFAULT}}
MAVEN_SETTINGS=${2:-${MAVEN_SETTINGS_DEFAULT}}

chmod 777 ${WORKSPACE}/mvnw

${WORKSPACE}/mvnw clean package -s ${WORKSPACE}/${MAVEN_SETTINGS}