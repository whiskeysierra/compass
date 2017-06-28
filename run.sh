#!/bin/sh -ex

DB=$(docker run -d -p 5432:5432 postgres:9.6)
trap "docker stop ${DB}" EXIT INT TERM

until curl -s "http://localhost:5432/" || [ $? -eq 52 ]; do sleep 1; done;

mvn clean spring-boot:run -D skipTests
