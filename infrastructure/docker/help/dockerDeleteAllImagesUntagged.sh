#!/bin/bash euw

docker rmi $(docker images | awk '/^<none>/ {print $3}')
