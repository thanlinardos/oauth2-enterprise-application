#!/bin/bash

source prod.env &&

rm -rf "cert"
mkdir "cert"
cd "cert" &&
echo "authorityKeyIdentifier=keyid,issuer" > localhost.ext &&
echo "basicConstraints=CA:FALSE" >> localhost.ext &&
echo "subjectAltName = @alt_names" >> localhost.ext &&
echo "[alt_names]" >> localhost.ext &&
echo "DNS.1 = $HOSTNAME" >> localhost.ext &&
echo "DNS.2 = $HOST_IP" >> localhost.ext &&
echo "DNS.3 = localhost" >> localhost.ext &&
echo "DNS.4 = 127.0.0.1" >> localhost.ext &&
echo "DNS.5 = 0.0.0.0" >> localhost.ext &&
chmod +777 localhost.ext &&

openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt -subj "$ROOT_CA_SUBJ" -addext "subjectAltName = DNS:localhost, IP:127.0.0.1, IP:0.0.0.0, IP:$HOST_IP" -passin pass:${ROOT_CA_PASS} -passout pass:${ROOT_CA_PASS} &&
openssl req -new -newkey rsa:4096 -keyout server.key -out server.csr -subj="$SERVER_SUBJ" -passin pass:${SERVER_PEM_PASS} -passout pass:${SERVER_PEM_PASS} &&
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in server.csr -out server.crt -days 365 -CAcreateserial -extfile localhost.ext -passin pass:${ROOT_CA_PASS} &&
openssl pkcs12 -export -out server.p12 -name "server" -inkey server.key -in server.crt -passin pass:${SERVER_PEM_PASS} -passout pass:${KEYSTORE_SERVER_PASS} &&
openssl req -new -newkey rsa:4096 -nodes -keyout local-client.key -out local-client.csr -subj "$CLIENT_SUBJ" -passin pass:${CLIENT_PEM_PASS} -passout pass:${CLIENT_PEM_PASS} &&
keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1,ip:0.0.0.0 -file rootCA.crt -keystore truststore.p12 -deststorepass password &&
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in local-client.csr -out local-client.crt -days 365 -CAcreateserial -passin pass:${ROOT_CA_PASS} &&
openssl pkcs12 -export -out local-client.p12 -name "local-client" -inkey local-client.key -in local-client.crt -passin pass:${CLIENT_PEM_PASS} -passout pass:${KEYSTORE_CLIENT_PASS}

cd - || exit