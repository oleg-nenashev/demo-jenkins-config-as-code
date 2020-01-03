#!/bin/sh euw
BASEDIR=$(dirname "$0")

WORKSPACE_DEFAULT='../..'
WORKSPACE=${1:-${WORKSPACE_DEFAULT}}

docker build --no-cache --pull -t jenkins-cx ${WORKSPACE}