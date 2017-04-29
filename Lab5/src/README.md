### Commands to execute both main Classes

#### Server
```
java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=123456 -Djavax.net.ssl.keyStore=server.keys -Djavax.net.ssl.keyStorePassword=123456 SSLServer <port> <cypher-suite>*
```

#### Client
```
java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=123456 -Djavax.net.ssl.keyStore=client.keys -Djavax.net.ssl.keyStorePassword=123456 SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*
```

#### Examples of Cyphers
* SSL_RSA_WITH_RC4_128_MD5
* SSL_RSA_WITH_RC4_128_SHA
* TLS_RSA_WITH_AES_128_CBC_SHA
* TLS_DHE_RSA_WITH_AES_128_CBC_SHA
