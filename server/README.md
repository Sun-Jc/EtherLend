## server.js

1. run testrpc: `testrpc`, at `taoli.tsinghuax.org:8545`
2. deploy current contracts to testrpc: `cd truffle && truffle migrate`, at `taoli.tsinghuax.org:8090`
3. run server: `cd server && nodemon server.js`, at `taoli.tsinghuax.org:8085`
4. find public ip: `curl ipinfo.io/ip`

## node-debug

```
$ npm install -g node-inspector
$ node-debug app.js
```

## nodemon

```
$ npm install nodemon -g
$ nodemon app.js
```
