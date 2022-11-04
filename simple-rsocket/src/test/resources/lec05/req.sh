### request-stream without setupData - limited functionality example
rsc --stream -d 14 --delayElements 1000 --log DEBUG tcp://localhost:6565

### request-stream with setupData
rsc --stream -d 14 --setupData 'user:password' --delayElements 1000 --log DEBUG --debug tcp://localhost:6565

### Setup Metadata
### Setup Data
### route - in Metadata
### Metadata
### Data
rsc --stream -d 14 --setupMetadata 'metaSetup' --setupData 'user:password' --metadata 'meta1'  --route 'route1' --delayElements 1000 --log DEBUG --debug tcp://localhost:6565

#Frame => Stream ID: 0 Type: SETUP Flags: 0b100000000 Length: 120
#Metadata:
#         +-------------------------------------------------+
#         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
#+--------+-------------------------------------------------+----------------+
#|00000000| 0f 61 70 70 6c 69 63 61 74 69 6f 6e 2f 6a 73 6f |.application/jso|
#|00000010| 6e 00 00 09 6d 65 74 61 53 65 74 75 70          |n...metaSetup   |
#+--------+-------------------------------------------------+----------------+
#Data:
#         +-------------------------------------------------+
#         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
#+--------+-------------------------------------------------+----------------+
#|00000000| 75 73 65 72 3a 70 61 73 73 77 6f 72 64          |user:password   |
#+--------+-------------------------------------------------+----------------+
#2022-11-04 08:38:17.894  INFO 21492 --- [actor-tcp-nio-2] DEBUG                                    : onSubscribe(FluxMap.MapSubscriber)
#2022-11-04 08:38:17.897  INFO 21492 --- [actor-tcp-nio-2] DEBUG                                    : request(32)
#2022-11-04 08:38:17.898 DEBUG 21492 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : sending ->
#Frame => Stream ID: 1 Type: REQUEST_STREAM Flags: 0b100000000 Length: 35 InitialRequestN: 32
#Metadata:
#         +-------------------------------------------------+
#         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
#+--------+-------------------------------------------------+----------------+
#|00000000| fe 00 00 07 06 72 6f 75 74 65 31 85 00 00 05 6d |.....route1....m|
#|00000010| 65 74 61 31                                     |eta1            |
#+--------+-------------------------------------------------+----------------+
#Data:
#         +-------------------------------------------------+
#         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
#+--------+-------------------------------------------------+----------------+
#|00000000| 31 34                                           |14              |
#+--------+-------------------------------------------------+----------------+
#2022-11-04 08:38:18.268 DEBUG 21492 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
#Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 30
#Data:
#         +-------------------------------------------------+
#         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
#+--------+-------------------------------------------------+----------------+
#|00000000| 7b 22 69 6e 70 75 74 22 3a 31 34 2c 22 6f 75 74 |{"input":14,"out|
#|00000010| 70 75 74 22 3a 31 34 7d                         |put":14}        |
#+--------+-------------------------------------------------+----------------+
#2022-11-04 08:38:18.269  INFO 21492 --- [actor-tcp-nio-2] DEBUG                                    : onNext({"input":14,"output":14})

