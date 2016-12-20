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
- pluggable relations: =, <, >, <=, >=, ~, in/contains
- schema validation for dimension and key values

## Comparison

### Config Service (internal)

- without projects
- without authentication/authorization (for now)
- without pre-defined *contexts*
- without central deployment
- without logs (history later!)
- with thin client
  - without bulk API
  - without caching
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

- equality
- greater-than
- less-than
- greater-than-or-equal
- less-than-or-equal

- Plugin SPI, e.g.
  - pattern
  - range, e.g. `[1.0,2.0)` (dynamic values, e.g. integers, versions, ...)
  - prefix match (e.g. locale, geohash)
- dimension values should not *overlap*, how can this be verified?
- maybe every custom dimension implementation needs to implement:
  - name of the dimension
  - *match* operation between dimension value and actual value
  - *overlap* operation between different dimension values
- validation in general?
  - e.g. invalid patterns?

## API

- list of entries is order by specificity
- dimensions are ordered by priority
  - by key?
- same dimensions are ordered depending on the rules of that dimension
- linear search
- first match -> return
- no match -> throw
- TODO what if a dimension is not provided by the caller?
  - required dimensions per key?

## Backlog

- encryption
- authentication/authorization
- caching
- preconditions (ETag + If-Match)
- PATCH
- history
  - require comment for every change?
  - persist every change (including old value)
  - /*/history in API 
  - 410 Gone + Latest known version as body?!

## Known Issues

- ISO 8601 time zones

## Alternatives

- [YCB](https://github.com/yahoo/ycb-java)