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
    rm -rf ${path} ${path}.json
    mkdir -p ${path}
    http --ignore-stdin --pretty=format :8080/${path} > ${path}.json
    http --ignore-stdin :8080/${path} | jq -r ".${path}[].id" | while read id; do
        http --ignore-stdin --pretty=format :8080/${path}/${id} > ${path}/${id}.json
    done
done << EOM
relations
dimensions
keys
EOM

http --ignore-stdin :8080/keys | jq -r '.keys[].id' | while read key; do
    for resource in value values; do
        mkdir -p keys/${key}
        http --ignore-stdin --pretty=format :8080/keys/${key}/${resource} > keys/${key}/${resource}.json
    done
done

while read -r path; do
    mkdir -p ${path}
    http --ignore-stdin --pretty=format :8080/${path} > ${path}.json
    http --ignore-stdin :8080/${path} | jq -r '.revisions[].id' | while read id; do
        http --ignore-stdin --pretty=format :8080/${path}/${id} > ${path}/${id}.json
    done
done << EOM
dimensions/revisions
dimensions/country/revisions
dimensions/after/revisions
dimensions/before/revisions
keys/revisions
keys/tax-rate/revisions
keys/tax-rate/value/revisions
keys/tax-rate/values/revisions
EOM
