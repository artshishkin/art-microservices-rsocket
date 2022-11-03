### request-stream without setupData - limited functionality example
rsc --stream -d 14 --delayElements 1000 --log DEBUG tcp://localhost:6565

### request-stream with setupData
rsc --stream -d 14 --setupData 'user:password' --delayElements 1000 --log DEBUG --debug tcp://localhost:6565
