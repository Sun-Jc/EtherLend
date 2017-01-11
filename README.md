
## EtherLend

This repository contains a demo DApp that simulates meetings of Rotating Credit And Savings Association (ROSCA). The technologies used include:
* Solidity
* Truffle
* Testrpc
* Node.js (with Express)
* Android

## Installation

* install nodejs: `sudo apt install nodejs`
* install npm: `sudo apt install npm`
* install testrpc: `npm install -g ethereumjs-testrpc`
* install express: `npm install express`

## Usage 

To make the whole process run normally, the following steps need to be taken:
* Run a Testrpc Client
* Deploy the Contracts
* Run a Node.js Server
* Run a Android Client

### Run Testrpc Client

```
testrpc
```

Now we have a brand new private chain Ehtereum client listening at default `localhost:8545`.

### Deploy the Contracts

You can config the Ethereum client in `truffle.js` like

```
rpc: {
	host: "localhost",
	port: 8545
}
```

Then just simple deploy:

```
cd truffle && rm -r build && truffle migrate
```

Now the compiled smart contracts are already deployed onto the blockchain. 

### Run Node.js Server

In `server.js`, you can config the client port number

```
var provider = 'http://localhost:8545'
```

Also remember to set the server port number like 

```
const PORT=8085; 
```

Then simply run:

```
cd server && node server.js
```

We will have a Node.js server listening at `localhost:8085`.

## Run the Android app

Set the Node.js server url in `EtherLendAndroid\app\src\main\java\edu\tsinghua\iiis\AccountModel.java`: 

```
public String BASEURL = "http://taoli.tsinghuax.org:8085/";
```

After a `apk` is compiled, you can just install that on your Android phone.

Then take a look at the tutorial video and have fun.