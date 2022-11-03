### request-stream with delay and log DEBUG (for backpressure demo)
rsc --stream -d 14 --delayElements 1000 --limitRate 16 --log DEBUG tcp://localhost:6565

