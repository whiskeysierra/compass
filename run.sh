#!/bin/sh -ex

while getopts :b opt; do
  case "$opt" in
    b)
        mvn package -P release -D skipTests
        ;;
    \?)
        echo "Invalid option: -$OPTARG" >&2
        ;;
    esac
done

docker-compose rm --stop --force
docker-compose up
