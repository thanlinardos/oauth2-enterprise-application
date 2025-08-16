#!/bin/bash

source prod.env &&
cd "cert" &&

env_not_set=()
for var in ROOT_CA_PASS SERVER_PEM_PASS CLIENT_PEM_PASS KEYSTORE_SERVER_PASS KEYSTORE_CLIENT_PASS CSTLO; do
    if [ -z "${!var}" ]; then
        env_not_set+=("$var")
    fi
done
if [ ${#env_not_set[@]} -ne 0 ]; then
    echo "The following environment variables are not set: ${env_not_set[*]}"
    exit 1
fi

client_list=()
while IFS= read -r line || [ -n "$line" ]; do
    client_list+=("$line")
done < clients.csv &&
for str in "${client_list[@]}"; do
    IFS=',' read -r -a array <<< "$str"
    NAME="${array[0]}"
    CN="${array[1]}"
    SAN1="${array[2]}"
    SAN2="${array[3]}"
    SAN3="${array[4]}"
    echo "Generating certificate for $NAME with SAN1 $SAN1, SAN2 $SAN2, SAN3 $SAN3 and CN $CN"

    rm -rf "$NAME"
    mkdir "$NAME" &&
    rm -rf "san.ext"
    echo "authorityKeyIdentifier=keyid,issuer" > san.ext &&
    echo "basicConstraints=CA:FALSE" >> san.ext &&
    echo "subjectAltName = @alt_names" >> san.ext &&
    echo "[alt_names]" >> san.ext &&
    echo "DNS.1 = $CN" >> san.ext &&
    echo "DNS.2 = $SAN1" >> san.ext &&
    echo "DNS.3 = $SAN2" >> san.ext &&
    echo "DNS.4 = $SAN3" >> san.ext &&
    chmod +777 san.ext &&

    openssl req -new -newkey rsa:4096 -nodes -keyout ${NAME}/${NAME}.key -out ${NAME}/${NAME}.csr -subj "$CSTLO AE/OU=AE/CN=$CN" -passin pass:${CLIENT_PEM_PASS} -passout pass:${CLIENT_PEM_PASS} &&
    openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in ${NAME}/${NAME}.csr -out ${NAME}/${NAME}.crt -extfile san.ext -days 365 -CAcreateserial -passin pass:${ROOT_CA_PASS} &&
    openssl pkcs12 -export -out ${NAME}/${NAME}.p12 -name "$NAME" -inkey ${NAME}/${NAME}.key -in ${NAME}/${NAME}.crt -passin pass:${CLIENT_PEM_PASS} -passout pass:${KEYSTORE_CLIENT_PASS}
    openssl pkcs12 -in ${NAME}/${NAME}.p12 -out ${NAME}/${NAME}.pem -passin pass:${KEYSTORE_CLIENT_PASS} -passout pass:${CLIENT_PEM_PASS}
done &&
cd - || exit