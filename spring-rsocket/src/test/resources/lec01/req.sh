### All the commands should be run with turned off authentication (run app without profile `secure`)

### fire and forget
rsc --fnf -d 13 --debug --route 'math.service.print' --log DEBUG tcp://localhost:6565

### request-response
rsc --request -d 14 --debug --route 'math.service.find_square' --log DEBUG tcp://localhost:6565

### request-stream `math.service.table`
rsc --stream -d 5 --debug --route 'math.service.table' --log DEBUG tcp://localhost:6565

### request-stream `math.service.table_mono`
rsc --stream -d 6 --debug --route 'math.service.table_mono' --log DEBUG tcp://localhost:6565

### request-stream `math.service.table_convert`
rsc --stream -d 7 --debug --route 'math.service.table_convert' --log DEBUG tcp://localhost:6565

### channel `math.service.table_convert`
rsc --channel -d - --debug --route 'math.service.chart' --log DEBUG tcp://localhost:6565



