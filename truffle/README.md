## Interaction

* web3 API: [https://github.com/ethereum/wiki/wiki/JavaScript-API](https://github.com/ethereum/wiki/wiki/JavaScript-API).
* MetaCoin:[http://truffleframework.com/docs/getting_started/contracts](http://truffleframework.com/docs/getting_started/contracts)

Here is how you would test the `greeter`:
* `$ vim contracts/mortal.sol`
* `$ vim migrations/3_deploy_mortal.js`
* `$ truffle migrate`
* refresh your browser with address `localhost:8090`(the port you set with `truffle serve -p 8090`)
* in the console(after you press `F12` in browser): `greeter = greeter.deployed(); greeter.greet.call().then(function(response) {console.log(response)})`
* wait a second, you can see `hello`

## Truffle

[demo-video](https://www.youtube.com/watch?v=GPP6uAq15d8#t=382.565145).

* `https://www.ethereum.org/token`
* `https://www.ethereum.org/crowdsale`
* `https://solidity.readthedocs.io/en/latest/introduction-to-smart-contracts.html`

### install

```
apt-get install --user nodejs
npm install truffle
```

[documentation](https://truffle.readthedocs.io/en/latest/).

```
truffle init
```

### testrpc

```
npm install ethereumjs-testrpc
testrpc
```

### deploy contract

Remember to write a new migrate about your contract in `migrations/`.

```
truffle compile
truffle migrate
```

### server

```
truffle build
truffle serve -p 8090
```

### test

```
truffle test
```

```
truffle console
```
