#!/bin/sh -e
BASEDIR=$(dirname "$0")

MAVEN_SETTINGS_DEFAULT='.mvn/wrapper/settings.xml'
WORKSPACE_DEFAULT='../..'
WORKSPACE=${1:-${WORKSPACE_DEFAULT}}
MAVEN_SETTINGS=${2:-${MAVEN_SETTINGS_DEFAULT}}

${WORKSPACE}/mvnw versions:update-parent -s ${WORKSPACE}/${MAVEN_SETTINGS}
${WORKSPACE}/mvnw versions:display-plugin-updates -s ${WORKSPACE}/${MAVEN_SETTINGS}
${WORKSPACE}/mvnw versions:display-dependency-updates -s ${WORKSPACE}/${MAVEN_SETTINGS}
