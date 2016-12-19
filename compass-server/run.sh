#!/bin/sh -ex

DB=$(docker run -d -p 5432:5432 postgres:9.5)
trap "docker stop ${DB}" EXIT INT TERM

until curl -s "http://localhost:5432/" || [ $? -eq 52 ]; do sleep 1; done;
# TODO psql -h localhost -U postgres -f src/main/resources/db/init/db_create.sql

mvn clean pre-integration-test spring-boot:run -D skipTests
