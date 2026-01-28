#!/bin/bash

source ../prod.env &&

cd "cert" &&

rm server.csr server.crt server.ext server.key server.p12 local-client.csr local-client.crt local-client.key local-client.p12 truststore.p12

openssl req -new -newkey rsa:4096 -keyout server.key -out server.csr -subj="$SERVER_SUBJ" -passin pass:"${SERVER_PEM_PASS}" -passout pass:"${SERVER_PEM_PASS}" &&
cat > server.ext <<EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature,keyEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = $HOSTNAME
IP.1 = $HOST_IP
EOF
openssl x509 -req -CA intermediate.crt -CAkey intermediate.key -in server.csr -out server.crt -days "$DAYS_VALID" -CAcreateserial -extfile server.ext -passin pass:"${INTERMEDIATE_CA_PASS}" &&
openssl pkcs12 -export -out server.p12 -name "server" -inkey server.key -in server.crt -passin pass:"${SERVER_PEM_PASS}" -passout pass:"${KEYSTORE_SERVER_PASS}" &&
chmod +777 server.crt
chmod +777 server.key

# client cert & truststore
openssl req -new -newkey rsa:4096 -nodes -keyout local-client.key -out local-client.csr -subj "$CLIENT_SUBJ" -passin pass:"${CLIENT_PEM_PASS}" -passout pass:"${CLIENT_PEM_PASS}" &&
keytool -import -trustcacerts -noprompt -alias ca -file intermediate.crt -keystore truststore.p12 -deststorepass "${TRUSTSTORE_PASS}" &&
openssl x509 -req -CA intermediate.crt -CAkey intermediate.key -in local-client.csr -out local-client.crt -days "$DAYS_VALID" -CAcreateserial -passin pass:"${INTERMEDIATE_CA_PASS}" &&
openssl pkcs12 -export -out local-client.p12 -name "local-client" -inkey local-client.key -in local-client.crt -passin pass:"${CLIENT_PEM_PASS}" -passout pass:"${KEYSTORE_CLIENT_PASS}"
chmod +777 local-client.key
chmod +777 local-client.p12
chmod +777 local-client.crt

rm ../../keycloak/truststore.p12
rm "${CLOUD_CONFIG_SERVER_CLASSPATH_RELATIVE_TO_CERT_DIR}"/truststore.p12
cp truststore.p12 ../../keycloak/
cp truststore.p12 "${CLOUD_CONFIG_SERVER_CLASSPATH_RELATIVE_TO_CERT_DIR}"

cd - || exit