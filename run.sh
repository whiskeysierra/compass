#!/bin/sh -ex

mvn package -P release -D skipTests
docker-compose rm --stop --force
docker-compose up
