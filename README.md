# art-microservices-rsocket
Tutorial - Develop Reactive Microservices With RSocket - from Vinoth Selvaraj (Udemy)

####  Section 1: Introduction

#####  2. [THEORY] - Need For RSocket

Problems
1.  HTTP
    -  3-way handshake to establish reliable TCP connection between the client and server (for every single request)
    -  then client send the request and get the response
    -  then wait for response before send another request
    -  RSocket uses persistent TCP connection 
2.  Request & Response Protocol
    -  server can not notify client if something has changed
    -  only client initiate request
    -  RSocket - 2way connection - any side can be client or server
3.  Serialization and Deserialization
4.  Blocking Thread
    -  service A calls service B - thread is blocked 

####  Section 3: RSocket - Getting Started

#####  26. Persistent Connection Issue

-  Start Server
-  Start Test
-  During 15s pause restart server - simulating server update/restart
-  Will receive `java.nio.channels.ClosedChannelException`

####  Section 4: RSocket With Spring

####  Section 5: Game - Guess A Number - Assignment
      
####  Section 6: RSocket - Connection Setup / Retry / Resumption

#####  62. Session Resumption - Part 3

Retry works only for new requests. For broken streams it does not work.

####  Section 7: Connection/Load Balancing

#####  66. Running Multiple Instances

1.  Vinoth approach
    -  package project
        -  `mvn clean package -DskipTests`
    -  run 3 different instances
        -  `java -jar target/spring-rsocket-0.0.1-SNAPSHOT.jar --spring.rsocket.server.port=6563`
        -  `java -jar target/spring-rsocket-0.0.1-SNAPSHOT.jar --spring.rsocket.server.port=6564`
        -  `java -jar target/spring-rsocket-0.0.1-SNAPSHOT.jar --spring.rsocket.server.port=6565`
2.  My approach
    -  use Intellij - Environment variables **OR** VM Options
    -  create configs
        -  Edit Configurations
            -  spring-rsocket-6563
                -  Environment variables: `spring.rsocket.server.port=6563`
            -  spring-rsocket-6564
                -  VM Options: `-Dspring.rsocket.server.port=6564`
            -  spring-rsocket-6565
                -  do nothing - just use default (6565 from application.yml)
            
####  Section 8: RSocket Security

#####  77. SSL/TLS - Generating Certificates

1.  Generate Key Pair
    -  `keytool -genkeypair -alias rsocket -keyalg RSA -keysize 2048 -storetype PKCS12 -validity 3650 -keystore rsocket-server.p12 -storepass password`
        -  `genkeypair` - generate public and private keys
        -  `keyalg` - algorithm RSA
        -  `storetype` - PKCS12 - Public-Key Cryptography Standards (earlier we used JKS (Java KeyStore))
        -  `validity` - 3650 - 10 years
        -  `keystore` - rsocket-server.p12 (store file)
        -  `storepass` - password
    -  First name and Last name - no matters
    -  Organizational Unit - `localhost`
    -  Other - no matter
    -  yes at the end
        -  created `rsocket-server.p12`
2.  Export Public Key
    -  `keytool -exportcert -alias rsocket -keystore rsocket-server.p12 -storepass password -file cert.pem`
    -  `file` - cert.pem - save exported key into this file
        -  created `cert.pem`
3.  Import certificate into client.truststore
    -  `keytool -importcert -alias rsocket -keystore client.truststore -storepass password -file cert.pem`
    -  Trust this certificate? [no]:  yes
    -  Certificate was added to keystore
        -  created `client.truststore`
        
        
        
        
        
        
        
        
              