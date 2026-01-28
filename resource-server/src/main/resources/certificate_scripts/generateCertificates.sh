#!/bin/bash

source ../prod.env &&

rm -rf "cert"
mkdir "cert"
cd "cert" &&

# root CA (valid for 10 years)
openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt -subj "$ROOT_CA_SUBJ" -addext "subjectAltName = IP:$HOST_IP" -passin pass:"${ROOT_CA_PASS}" -passout pass:"${ROOT_CA_PASS}" &&
# intermediate CA (valid for 5 years)
openssl req -new -newkey rsa:4096 -keyout intermediate.key -subj "$INTERMEDIATE_SUBJ" -out intermediate.csr -passin pass:"${INTERMEDIATE_CA_PASS}" -passout pass:"${INTERMEDIATE_CA_PASS}"
cat > intermediate.ext <<EOF
basicConstraints=CA:TRUE,pathlen:0
keyUsage=keyCertSign,cRLSign
subjectKeyIdentifier=hash
EOF
openssl x509 -req -in intermediate.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out intermediate.crt -days 1825 -extfile intermediate.ext -passin pass:"${ROOT_CA_PASS}"
chmod +777 intermediate.crt
# server cert (valid for usually 1 year - set by $DAYS_VALID)
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

cd - || exit

cat <<EOL > clients.csv
local_angular_ui_client,pass,pass,angularui.local,$HOST_IP
local_keycloak_client,pass,pass,keycloak.local,$HOST_IP
local_vault_client,pass,pass,vault.local,$HOST_IP
EOL
echo "Generated certs and default clients.csv in cert dir. Add more clients to clients.csv as needed with the format: alias,common_name,IP1,IP2,..."