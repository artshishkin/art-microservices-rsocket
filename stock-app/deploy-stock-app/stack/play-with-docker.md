[play-with-docker](https://labs.play-with-docker.com/)

1.  Start `portainer` on manager node
```shell script
docker service create \
--name portainer \
--publish 9000:9000 \
--constraint 'node.role == manager' \
--mount type=bind,src=/var/run/docker.sock,dst=/var/run/docker.sock \
portainer/portainer \
-H unix:///var/run/docker.sock
```
2.  Portainer console
    -  on `server:9000`
    -  deploy stack
3.  Test it
    -  use IP something like this
    -  http://ip172-18-0-27-c1stfvje75e000a4m7ag-8080.direct.labs.play-with-docker.com/
       