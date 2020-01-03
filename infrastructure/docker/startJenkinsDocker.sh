#!/bin/sh
BASEDIR=$(dirname "$0")

source "${BASEDIR}/colors.sh"

docker run -p 8080:8080 -p 50000:50000 jenkins-cx:latest
