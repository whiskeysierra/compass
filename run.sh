#!/bin/sh -ex

while getopts :b opt; do
  case "$opt" in
    b)
        ./mvnw clean package -P release -D skipTests
        ;;
    \?)
        echo "Invalid option: -$OPTARG" >&2
        ;;
    esac
done

docker-compose down
docker-compose up --build --force-recreate
