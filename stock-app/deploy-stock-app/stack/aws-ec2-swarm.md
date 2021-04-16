[Digital Ocean](https://cloud.digitalocean.com/projects)

#####  Initialize Stack in AWS EC2s 

1.  Create Security group - `docker-swarm-cluster` like in [documentation](https://docs.docker.com/engine/swarm/swarm-tutorial/#open-protocols-and-ports-between-the-hosts)

| Type | Protocol | Port range | Source | Description - optional |
| ---- | -------- | ---------- | ------ | ---------------------- |    
| Custom TCP |	TCP	| 2377 |	sg-06c850567b98d800e (docker-swarm-cluster) |	Docker Swarm 2377 port - for cluster management communications |
| Custom UDP |	UDP	| 7946 |	sg-06c850567b98d800e (docker-swarm-cluster) |  Docker Swarm UDP 7946 - for communication among nodes |
| Custom TCP |	TCP	| 7946 |	sg-06c850567b98d800e (docker-swarm-cluster) |	Docker Swarm TCP 7946 - for communication among nodes |
| Custom UDP |  UDP	| 4789 |	sg-06c850567b98d800e (docker-swarm-cluster) |	Docker Swarm UDP 4789 - for overlay network traffic |
| ESP (50)   |	ESP (50) |	All	| sg-06c850567b98d800e (docker-swarm-cluster) |	IP Protocol 50 (ESP) if you plan on using overlay network with the encryption option |

2.  Provision EC2 instance for Node01
    -  for node1 choose t2.medium EC2 instance with [UserDataNode1.sh](ec2-swarm/UserDataNode1.sh)
    -  attach security groups
        -  `docker-swarm-cluster`
        -  `microservices-allow-ssh` (allow SSH 22)
3.  Provision EC2 instances for Node02...Node03
    -  node1 -> Actions -> Launch more like this ->
    -  modify UserData with [UserDataNode23.sh](ec2-swarm/UserDataNode23.sh)
    -  ssh to node01
        -  `docker swarm join-token worker`
        -  `docker swarm join --token SWMTKN-1-35tzt0vocmjs7fs8m6pze83udgzvdyeteveopyfjfyehylxtc2-69n9cg3pbowspyspoqdodnpjv 172.31.44.224:2377`
        -  insert it into UserData (modify existing)
    -  create
4.  Promote Node02 and Node03 (optional)
    -  `docker node ls` - list all nodes
    -  `docker node promote 5vf bzm` - make nodes managers with id stating with `5vf` and `bzm`
    -  `docker node ls` - make sure they became managers

#####  Deploy stack to AWS Swarm 

1.  Create security group `trading-service-sg` to access 8080 port and portainer and mongo-express from my IP zone

| Type | Protocol | Port range | Source | Description - optional |
| ---- | -------- | ---------- | ------ | ---------------------- |    
| Custom TCP |	TCP |	8080 |	0.0.0.0/0 |	Allow port 8080 from anywhere |
| Custom TCP |	TCP |	9000 |	93.170.219.0/24 |	Portainer |
| Custom TCP |	TCP |	8081 |	93.170.219.0/24 |	Mongo-Express |

2.  Add Security Group to Node01
3.  Deploy [docker-compose.yml](docker-compose.yml)
4.  Test it



