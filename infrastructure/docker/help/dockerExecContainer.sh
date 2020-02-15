#!/bin/bash

CONTAINER_ID=''

source ./colors.sh

if [ "$#" -eq 0 ]; then
  printf "${RED}use your conainer id${NC}\n"
  docker ps
  exit 1
else
  CONTAINER_ID=$1
fi

docker exec -ti ${CONTAINER_ID} /bin/bash