# Compass

[![Compass](docs/compass.jpg)](http://pixabay.com/en/map-of-the-world-compass-antique-429784/)

[![Stability: Experimental](https://masterminds.github.io/stability/experimental.svg)](https://masterminds.github.io/stability/experimental.html)
[![Build Status](https://img.shields.io/travis/zalando/compass.svg)](https://travis-ci.org/zalando/compass)
[![Coverage Status](https://img.shields.io/coveralls/zalando/compass.svg)](https://coveralls.io/r/zalando/compass)
[![Code Quality](https://img.shields.io/codacy/grade/ccbb2b8b85854dc6849c4b9de6fce224/master.svg)](https://www.codacy.com/app/whiskeysierra/compass)
[![Release](https://img.shields.io/github/release/zalando/compass.svg)](https://github.com/zalando/compass/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/compass-parent.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/compass-parent)

## Features

- configuration management
- n-dimensional, user-defined key-space a.k.a. *dimensions*
- REST API
- pluggable relations: =, <, >, <=, >=, ~

## Concepts

TODO

## Known Issues

- ISO 8601 time zones

## Alternatives

- [YCB](https://github.com/yahoo/ycb-java)
- [Decision Model and Notation (DMN)](https://en.wikipedia.org/wiki/Decision_Model_and_Notation) for example [Camunda DMN Engine](https://docs.camunda.org/manual/latest/user-guide/dmn-engine)

### Config Service (internal)

- without projects
- without authentication/authorization (for now, see #14)
- without pre-defined *contexts*
- without central deployment
- without logs (history later, see #13)
- with thin client
  - without bulk API
  - without caching (see #15)
- with write API

### YCB

- without pre-defined values for dimensions
- without value hierarchies
- with REST API
