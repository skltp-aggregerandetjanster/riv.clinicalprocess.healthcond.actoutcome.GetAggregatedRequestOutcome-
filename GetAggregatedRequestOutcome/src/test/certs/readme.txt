====
    Copyright (c) 2014 Inera AB, <http://inera.se/>

    This file is part of SKLTP.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
====

# ==============================================
# Create keystores using openssl and keytool
# ==============================================
mkdir private
mkdir public
mkdir client

# CA
openssl genrsa -des3 -passout pass:password -out private/ca.key 1024
openssl req -verbose -new -x509 -key private/ca.key -passin pass:password -out public/ca.crt -days 7300 -set_serial 1234 -subj "/CN=SOI Toolkit CA/OU=NOT FOR PRODUCTION/O=TEST/ST=No-state/C=SE"
  
# 2. Create and sign your server certificate
# 2.1 Creation of the server certificate 
openssl genrsa -des3 -passout pass:password -out private/server.key 1024
openssl req -new -key private/server.key -passin pass:password -out server.csr -days 7300 -subj "/CN=SOI Toolkit Server/OU=NOT FOR PRODUCTION/O=TEST/ST=No-state/C=SE"
openssl x509 -req -in server.csr -CA public/ca.crt -CAkey private/ca.key -CAcreateserial -out public/server.crt -passin pass:password -days 7300
openssl pkcs12 -export -in public/server.crt -inkey private/server.key -out server.p12 -name producer -passin pass:password -passout pass:password

keytool -v -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 -destkeystore server.jks -deststoretype JKS -srcstorepass password -deststorepass password

# 3. Client authentication
# 3.1 Client certificate creation and signing
openssl req -new -newkey rsa:1024 -nodes -passout pass:password -out client/client.req -keyout client/client.key  -days 7300 -subj "/CN=SOI Toolkit Client/OU=NOT FOR PRODUCTION/O=TEST/ST=No-state/C=SE"
openssl x509 -CA public/ca.crt -CAkey private/ca.key -CAserial public/ca.srl -req -in client/client.req -passin pass:password -out client/client.pem -days 7300

# 3.2 Export client certificate as keychain in pkcs12/java keystore
openssl pkcs12 -export -clcerts -in client/client.pem -inkey client/client.key -out client/client.p12 -name consumer -passout pass:password

keytool -v -importkeystore -srckeystore client/client.p12 -srcstoretype PKCS12 -destkeystore client.jks -deststoretype JKS -srcstorepass password -deststorepass password

# create truststore with dummy key, cannot import into truststore if it doesn't exist
keytool -genkey -alias dummy -keyalg RSA -keystore truststore.jks -storepass password -keypass password -dname "CN=SOI Toolkit TruststoreDummyKey, OU=NOT FOR PRODUCTION, O=TEST, ST=No-state, C=SE"
# will ask if we trust the cert
keytool -import -v -trustcacerts -alias my_ca -file public/ca.crt -keystore truststore.jks -storepass password
keytool -v -delete -alias dummy -keystore truststore.jks -storepass password

# inspect results 
keytool -v -list -keystore truststore.jks -storepass password
keytool -v -list -keystore client.jks -storepass password
keytool -v -list -keystore server.jks -storepass password

