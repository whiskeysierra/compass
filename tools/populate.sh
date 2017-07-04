#!/bin/sh -e

jq -n '{schema:{}, relation:"=", description:".."}' | http PUT :8080/dimensions/country
jq -n '{schema:{}, relation:"<", description:".."}' | http PUT :8080/dimensions/before
jq -n '{schema:{}, relation:">", description:".."}' | http PuT :8080/dimensions/after

jq -n '{schema:{type:"number"}, description:".."}' | http PUT :8080/keys/tax-rate

jq -n '{value:0.19}' | http PUT :8080/keys/tax-rate/value country==AT
jq -n '{value:0.08}' | http PUT :8080/keys/tax-rate/value country==CH
jq -n '{value:0.19}' | http PUT :8080/keys/tax-rate/value country==DE
jq -n '{value:0.2}' | http PUT :8080/keys/tax-rate/value country==FR