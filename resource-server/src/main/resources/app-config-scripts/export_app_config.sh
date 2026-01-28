#!/bin/bash

SUBTRACT_JSON_JQ=$(<subtract_json_remove_empty_obj_and_profiles_key_jq_command.txt)
echo "Exporting app configuration to JSON files..."
cd exports || exit
set -o allexport &&

# Process prod environment
source ../../prod.env &&
envsubst < ../../application.yml | yq -o=json > temp1.json &&
envsubst < ../../application-prod.yml | yq -o=json > temp2.json &&
envsubst < ../custom-datasource-props.yml | yq -o=json > temp3.json &&
jq -s 'reduce .[] as $item ({}; . * $item)' temp1.json temp2.json temp3.json > temp-application-prod.json &&
# remove the common properties set in common.json
jq -s "${SUBTRACT_JSON_JQ}" temp-application-prod.json ../common.json > application-prod.json &&
rm -f temp1.json temp2.json temp3.json &&
echo "Exported prod configuration." &&

# Process dev environment
source ../../dev.env &&
envsubst < ../../application.yml | yq -o=json > temp1.json &&
envsubst < ../../application-dev.yml | yq -o=json > temp2.json &&
envsubst < ../custom-datasource-props.yml | yq -o=json > temp3.json &&
jq -s 'reduce .[] as $item ({}; . * $item)' temp1.json temp2.json temp3.json > temp-application-dev.json &&
# remove the common properties set in common.json
jq -s "${SUBTRACT_JSON_JQ}" temp-application-dev.json ../common.json > application-dev.json &&
rm -f temp1.json temp2.json temp3.json &&
echo "Exported dev configuration." &&

# Common properties
jq -s "${SUBTRACT_JSON_JQ}" temp-application-prod.json application-prod.json > application.json &&
rm -f temp-application-prod.json temp-application-dev.json &&
echo "Exported common configuration." &&

# Clean up
set +o allexport &&

echo "...done" &&
cd - || exit