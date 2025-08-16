#!/bin/bash

echo "Exporting app configuration to JSON files..."
set -o allexport &&

source prod.env &&
envsubst < application.yml | yq -o=json > temp1.json &&
envsubst < application-prod.yml | yq -o=json > temp2.json &&
jq -s 'reduce .[] as $item ({}; . * $item)' temp1.json temp2.json > application-prod.json &&
rm -rf temp1.json temp2.json &&

source dev.env &&
envsubst < application.yml | yq -o=json > temp1.json &&
envsubst < application-dev.yml | yq -o=json > temp2.json &&
jq -s 'reduce .[] as $item ({}; . * $item)' temp1.json temp2.json > application-dev.json &&
rm -rf temp1.json temp2.json &&

set +o allexport &&
echo "...done" &&
cd - || exit