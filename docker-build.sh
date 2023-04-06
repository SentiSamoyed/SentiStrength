#!/usr/bin/env bash

load_variable () {
  sed 's/.*=[ ]*\(.*\)/\1/g' < "$1"
}

VERSION="$(load_variable ./gradle.properties)"
NAME=sentistrength

JAR_NAME="$NAME"-"$VERSION".jar

docker build -t "$NAME":"$VERSION" --build-arg JAR_NAME="$JAR_NAME" .
docker tag "$NAME":"$VERSION" "$NAME":latest