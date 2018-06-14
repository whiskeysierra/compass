#!/bin/sh -ex

./mvnw clean package -P release -D skipTests

docker-compose down
docker-compose up --build --force-recreate
