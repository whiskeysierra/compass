# Compass

[![Compass](docs/compass.jpg)](http://pixabay.com/en/map-of-the-world-compass-antique-429784/)

[![Build Status](https://img.shields.io/travis/zalando/compass.svg)](https://travis-ci.org/zalando/compass)
[![Coverage Status](https://img.shields.io/coveralls/zalando/compass.svg)](https://coveralls.io/r/zalando/compass)
[![Release](https://img.shields.io/github/release/zalando/compass.svg)](https://github.com/zalando/compass/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/compass-parent.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/compass-parent)

## Features

- configuration management
- n-dimensional, user-defined key-space a.k.a. *dimensions*
- REST API
- pluggable relations: =, <, >, <=, >=, ~

## Comparison

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

## Concepts

### Keys and Values

TODO

### Dimensions

TODO

### Relations

TODO

## Known Issues

- ISO 8601 time zones

## Alternatives

- [YCB](https://github.com/yahoo/ycb-java)