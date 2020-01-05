#!/bin/sh
BASEDIR=$(dirname "$0")

source "${BASEDIR}/help/colors.sh"

docker run -p 80:8080 -p 50000:50000 continuous-x/jenkins-cx:latest
