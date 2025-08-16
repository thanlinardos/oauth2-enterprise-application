#!/bin/bash

echo "Installing the certificates to local machine & chrome..." &&

if [[ $(uname -s) = *"NT"* ]]; then
    echo "Running on Windows"

    # Import the certificate to the Current User's Personal certificate store using PowerShell
    powershell.exe -Command "\
        \$certFilePath = 'cert/server.crt'; \
        \$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2; \
        \$cert.Import(\$certFilePath); \
        \$store = New-Object System.Security.Cryptography.X509Certificates.X509Store -ArgumentList 'Root', 'CurrentUser'; \
        \$store.Open('ReadWrite'); \
        \$store.Add(\$cert); \
        \$store.Close(); \
        "
    powershell.exe -Command "\
        \$certFilePath = 'cert/client.crt'; \
        \$cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2; \
        \$cert.Import(\$certFilePath); \
        \$store = New-Object System.Security.Cryptography.X509Certificates.X509Store -ArgumentList 'Root', 'CurrentUser'; \
        \$store.Open('ReadWrite'); \
        \$store.Add(\$cert); \
        \$store.Close(); \
        "
else
    echo "Running on Linux?"
    sudo chmod +777 cert/server.crt &&
    sudo chmod +777 cert/client.crt &&
    sudo chmod +777 cert/keystore-client.p12 &&
    cd /usr/local/share/ca-certificates/ &&
    sudo rm client.crt server.crt keystore-client.p12
    cd - &&
    sudo cp cert/client.crt cert/server.crt cert/keystore-client.p12 /usr/local/share/ca-certificates/ &&
    sudo update-ca-certificates &&
    sudo apt-get update &&
    sudo apt-get install libnss3-tools &&
    sudo echo "pass" > cert/certdb.pass &&
    sudo echo "pass" > cert/keystore-client-p12.pass &&
    sudo chmod +777 cert/certdb.pass &&
    sudo chmod +777 cert/keystore-client-p12.pass &&

    while read -r -d $'\0' i ; do    sudo certutil -d 'sql:'"$i" -D -n local_cert; done < <(sudo find "$HOME" -type f -iregex '.*[/]cert[89][.]db' -printf '%h\0')
    while read -r -d $'\0' i ; do    sudo certutil -d 'sql:'"$i" -D -n local_client_cert; done < <(sudo find "$HOME" -type f -iregex '.*[/]cert[89][.]db' -printf '%h\0')
#    while read -r -d $'\0' i ; do    sudo certutil -d 'sql:'"$i" -n keystore-client; done < <(sudo find "$HOME" -type f -iregex '.*[/]cert[89][.]db' -printf '%h\0')

    echo "Removed existing certificates from chrome" &&

    while read -r -d $'\0' i ; do    sudo certutil -d 'sql:'"$i" -A -t "CT,C,C" -n local_cert -i cert/server.crt -f cert/certdb.pass; done < <(sudo find "$HOME" -type f -iregex '.*[/]cert[89][.]db' -printf '%h\0') &&
    while read -r -d $'\0' i ; do    sudo certutil -d 'sql:'"$i" -A -t "CT,C,C" -n local_client_cert -i cert/client.crt -f cert/certdb.pass; done < <(sudo find "$HOME" -type f -iregex '.*[/]cert[89][.]db' -printf '%h\0') &&
    while read -r -d $'\0' i ; do    sudo pk12util -d 'sql:'"$i" -i cert/keystore-client.p12 -w cert/keystore-client-p12.pass -k cert/certdb.pass; done &&

    sudo rm pkcs11.txt
    echo "Added new certificates"
fi
