#!/bin/bash

ufw allow in on eth1 to any port 2377 proto tcp
ufw allow in on eth1 to any port 7946
ufw allow in on eth1 to any port 4789 proto udp

# You need to SSH into node1 and `docker swarm join-token worker` to get join-token
docker swarm join --token SWMTKN-1-226w6frnz5w8da3mjobu5gb3mu4j879ast7swhom9jr1k0qw40-7htgnkwzqpdvitu4xhspr5kkp 10.114.16.2:2377