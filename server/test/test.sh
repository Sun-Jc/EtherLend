addr="0x9da1b32498940729a80b4a1a5ce7e83b7dd12557"
curl http://taoli.tsinghuax.org:8085/getEvents/$addr
curl http://taoli.tsinghuax.org:8085/check/$addr/0

curl http://taoli.tsinghuax.org:8085/set/$addr/0/20/20
sleep 3
curl http://taoli.tsinghuax.org:8085/join/$addr/1
curl http://taoli.tsinghuax.org:8085/join/$addr/2
curl http://taoli.tsinghuax.org:8085/join/$addr/3
sleep 3
curl http://taoli.tsinghuax.org:8085/accept/$addr/1/0
curl http://taoli.tsinghuax.org:8085/accept/$addr/2/0
curl http://taoli.tsinghuax.org:8085/accept/$addr/3/0
sleep 10 
curl http://taoli.tsinghuax.org:8085/push/$addr/0

curl http://taoli.tsinghuax.org:8085/suggest/$addr/0/60/10
sleep 3
curl http://taoli.tsinghuax.org:8085/vote/$addr/1/aye
curl http://taoli.tsinghuax.org:8085/vote/$addr/2/aye
curl http://taoli.tsinghuax.org:8085/vote/$addr/3/aye
sleep 3
curl http://taoli.tsinghuax.org:8085/push/$addr/0

curl http://taoli.tsinghuax.org:8085/bid/$addr/1/1/13/13
curl http://taoli.tsinghuax.org:8085/bid/$addr/3/1/11/11
curl http://taoli.tsinghuax.org:8085/bid/$addr/2/1/12/12
sleep 3
curl http://taoli.tsinghuax.org:8085/reaveal/$addr/1/1/13
curl http://taoli.tsinghuax.org:8085/reaveal/$addr/3/1/11
curl http://taoli.tsinghuax.org:8085/reaveal/$addr/2/1/12
sleep 3
curl http://taoli.tsinghuax.org:8085/push/$addr/0
