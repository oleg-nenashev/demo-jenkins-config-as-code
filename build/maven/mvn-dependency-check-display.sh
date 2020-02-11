#!/bin/sh -e
BASEDIR=$(dirname "$0")

. ${BASEDIR}/mvn-init.sh "$@"

runMvnw 'versions:update-parent'
runMvnw 'versions:display-plugin-updates'
runMvnw 'versions:display-dependency-updates'
