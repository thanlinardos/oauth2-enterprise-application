#!/bin/bash

echo "Installing the certificates to local machine & chrome..." &&

if [[ $(uname -s) = *"NT"* ]]; then
    echo "Running on Windows"

    # Import the certificates to the Current User's Personal certificate store using PowerShell
    powershell.exe -Command "\
        \$certFilePath = 'cert/intermediate.crt'; \
        \$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2; \
        \$cert.Import(\$certFilePath); \
        \$store = New-Object System.Security.Cryptography.X509Certificates.X509Store -ArgumentList 'Root', 'CurrentUser'; \
        \$store.Open('ReadWrite'); \
        \$store.Add(\$cert); \
        \$store.Close(); \
        "
    # install for client p12 certs
else
    echo "Running on Linux." &&
    source ../prod.env &&

    echo "Installing server certificate..." &&
    cd "cert" &&
    sudo rm /usr/local/share/ca-certificates/local-client.p12 || echo "Tried to delete resourceserver.local-client from ca-certificates but not found." &&
    sudo cp local-client.p12  /usr/local/share/ca-certificates/ &&
    echo "Installed resourceserver.local-client certificate in ca-certificates." &&

    certutil -d sql:"$HOME"/.pki/nssdb -D -n local-client || echo "Tried to delete resourceserver.local local-client certificate but not found." &&
    certutil -d sql:"$HOME"/.pki/nssdb -D -n CA-resourceserver.local || echo "Tried to delete resourceserver.local certificate but not found." &&
    pk12util -d sql:"$HOME"/.pki/nssdb -i local-client.p12 -W "${KEYSTORE_CLIENT_PASS}" &&
    echo "Installed resourceserver.local-client certificate in NSSDB." &&

    sudo rm /usr/local/share/ca-certificates/intermediate.crt || echo "Tried to delete server crt from ca-certificates but not found." &&
    sudo cp intermediate.crt  /usr/local/share/ca-certificates/ &&
    echo "Installed server certificate in ca-certificates." &&

    certutil -d sql:"$HOME"/.pki/nssdb -A -t "CT,C,C" -n CA-resourceserver.local -i intermediate.crt &&
    echo "Installed server certificate in NSSDB." &&

    # install client certs
    client_list=()
    while IFS= read -r line || [ -n "$line" ]; do
        client_list+=("$line")
    done < ../clients.csv &&
    for str in "${client_list[@]}"; do
        IFS=',' read -r -a array <<< "$str"
        NAME="${array[0]}"
        KEYSTORE_CLIENT_PASS="${array[2]}"
        CN="${array[3]}"
        san_list=("${array[@]:4}")
        echo "Installing certificate for $NAME with CN $CN and SANS: ${san_list[*]}"
        if [ -d "${NAME}" ]; then
            sudo rm /usr/local/share/ca-certificates/"${NAME}".p12 || echo "Tried to delete $NAME from ca-certificates but not found." &&
            sudo cp "${NAME}"/"${NAME}".p12  /usr/local/share/ca-certificates/ &&
            echo "Installed $NAME certificate in ca-certificates." &&

            certutil -d sql:"$HOME"/.pki/nssdb -D -n "${NAME}" || echo "Tried to delete $NAME from NSSDB but not found." &&
            pk12util -d sql:"$HOME"/.pki/nssdb -i "${NAME}"/"${NAME}".p12 -W "${KEYSTORE_CLIENT_PASS}" &&
            echo "Installed $NAME certificate in NSSDB."
        else
            echo "Client directory $NAME does not exist. Skipping..."
        fi
    done &&

    sudo update-ca-certificates &&
    echo "Updated ca-certificates." &&
    cd - || exit
fi