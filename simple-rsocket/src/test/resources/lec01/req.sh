### fire and forget
rsc --fnf -d 13 tcp://localhost:6565

### fire and forget with debug
rsc --fnf -d 13 --debug tcp://localhost:6565

### request-response with debug
rsc --request -d 14 --debug tcp://localhost:6565

### request-stream with debug
rsc --stream -d 14 --debug tcp://localhost:6565

### request-stream with delay
rsc --stream -d 14 --delayElements 1000 tcp://localhost:6565

### request-stream with delay and log DEBUG
rsc --stream -d 14 --delayElements 1000 --log DEBUG tcp://localhost:6565

### request-stream with loading data from file
rsc --stream --load ./data.txt --delayElements 1000 --limitRate 4 --log DEBUG tcp://localhost:6565

### channel with data from standard input
rsc --channel -d - --delayElements 1000 --log debug tcp://localhost:6565
