# Compass

[![Compass](docs/compass.jpg)](http://pixabay.com/en/map-of-the-world-compass-antique-429784/)

[![Build Status](https://img.shields.io/travis/zalando/compass.svg)](https://travis-ci.org/zalando/compass)
[![Coverage Status](https://img.shields.io/coveralls/zalando/compass.svg)](https://coveralls.io/r/zalando/compass)
[![Release](https://img.shields.io/github/release/zalando/compass.svg)](https://github.com/zalando/compass/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/compass-parent.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/compass-parent)

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
- without value hierarchies (???)
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
- pattern
- locale
- dimension values should not *overlap*, how can this be verified?
- maybe every custom dimension implementation needs to implement:
  - name of the dimension
  - *match* operation between dimension value and actual value
  - *overlap* operation between different dimension values
- validation in general?
  - e.g. invalid patterns?

## API

- history
  - require comment for every change
  - persist every change (including old value)
  - /*/history in API 
  - 410 Gone + Latest known version as body?!
- algorithm
  - list of entries is order by specificity
    - dimensions are ordered by priority
    - same dimensions are ordered depending on the rules of that dimension
  - linear search
  - first match -> return
  - no match -> throw
  - TODO what if a dimension is not provided by the caller?

## Backlog

- encryption
- authentication/authorization
- caching
- preconditions
- PATCH

## Alternatives

- [YCB](https://github.com/yahoo/ycb-java)