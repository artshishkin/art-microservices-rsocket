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



