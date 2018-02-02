#!/bin/sh -ex

alias http='http --check-status'

echo '{
  "schema": {
    "type": "string"
  },
  "relation": "=",
  "description": ".."
}' | http PUT :8080/dimensions/country

echo '{
  "schema": {
    "type": "string",
    "pattern": "^[A-Z]{2}$"
  },
  "relation": "=",
  "description": ".."
}' | http PUT :8080/dimensions/country

echo '{
  "schema": {
    "type": "string",
    "format": "date-time"
  },
  "relation": ">",
  "description": ".."
}' | http PUT :8080/dimensions/after

echo '{
  "schema": {
    "type": "string",
    "format": "date-time"
  },
  "relation": "<",
  "description": ".."
}' | http PUT :8080/dimensions/before

http DELETE :8080/dimensions/before

echo '{
  "schema": {
    "type": "number",
    "format": "decimal"
  },
  "description": "The sales tax rate expressed as a factor."
}' | http PUT :8080/keys/tax-rate

echo '{
  "schema": {
    "type": "number",
    "format": "decimal",
    "minimum": 0.0,
    "maximum": 1.0
  },
  "description": "The sales tax rate expressed as a factor."
}' | http PUT :8080/keys/tax-rate

echo '{
  "values": [
    {
      "dimensions": {
        "country": "AT"
      },
      "value": 0.2
    },
    {
      "dimensions": {
        "country": "BE"
      },
      "value": 0.21
    },
    {
      "dimensions": {
        "country": "CH"
      },
      "value": 0.08
    },
    {
      "dimensions": {
        "country": "DE",
        "after": "2017-01-01T00:00:00Z"
      },
      "value": 0.2
    },
    {
      "dimensions": {
        "country": "DE"
      },
      "value": 0.19
    },
    {
      "value": 1.0
    }
  ]
}' | http PUT :8080/keys/tax-rate/values

while read -r path; do
    mkdir -p $(dirname ${path})
    http --ignore-stdin --pretty=format :8080/${path} > ${path}.json
done << EOM
relations
relations/=
dimensions
dimensions/revisions
dimensions/revisions/1
dimensions/revisions/2
dimensions/revisions/3
dimensions/revisions/4
dimensions/revisions/5
dimensions/country
dimensions/country/revisions
dimensions/country/revisions/1
dimensions/country/revisions/2
dimensions/after/revisions
dimensions/after/revisions/3
dimensions/before/revisions
dimensions/before/revisions/4
dimensions/before/revisions/5
keys
keys/revisions
keys/revisions/6
keys/revisions/7
keys/tax-rate
keys/tax-rate/revisions
keys/tax-rate/revisions/6
keys/tax-rate/revisions/7
keys/tax-rate/value
keys/tax-rate/value/revisions
keys/tax-rate/value/revisions/8
keys/tax-rate/values
keys/tax-rate/values/revisions
keys/tax-rate/values/revisions/8
EOM
