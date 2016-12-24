## truffle

* copy the functions part from truffle here: `cp ../contracts/app/javascripts/app.js .`
* copy the contracts require part from `../contracts/build/app.js`
* copy the button and onclick part from `../contracts/app/index.html`
* integrate the functions and contracts to our `server.js`
* config url to functions
* run the nodejs server

## server.js

### private chain

run testrpc as a private chain: 

```
testrpc
```

now we have a rpc listening at port `8545`.

### contract deploy

first you need to create new migrate file in `truffle/migrations/`.

```
vi truffle/migrations/3_deploy_greeter.js
```

we can use truffle to deploy current contracts to the private chain (testrpc):

```
cd truffle && truffle migrate
```

Also, if you run 

```
truffle serve
```

you have a truffle application serving at port `8090`

### node.js server

run a node.js server to receive http request from Android app, interact with testrpc by web3.js, return the result to Android

```
cd server && nodemon server.js
```

now we have a node.js serving at port `8085`.

### Android app

the Android app can now send http request to the node.js server and wait for response.

## public ip

`curl ipinfo.io/ip`

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
