#!/bin/sh euw

docker rmi $(docker images | awk '/^<none>/ {print $3}')
