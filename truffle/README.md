## Truffle

[demo-video](https://www.youtube.com/watch?v=GPP6uAq15d8#t=382.565145).

* `https://www.ethereum.org/token`
* `https://www.ethereum.org/crowdsale`
* `https://solidity.readthedocs.io/en/latest/introduction-to-smart-contracts.html`

```
curl -sL https://deb.nodesource.com/setup_7.x | sudo -E bash -
sudo apt-get install -y nodejs
sudo npm install -g truffle
```

[documentation](https://truffle.readthedocs.io/en/latest/).

```
truffle init
truffle compile
```

```
sudo npm install -g ethereumjs-testrpc
# pip install testrpc
testrpc
```

Remember to add your contract to `migrate/` before `truffle serve`.

```
truffle migrate
truffle build
truffle serve
```

```
truffle test
```

```
truffle console
```

## Interaction

* web3 API: [https://github.com/ethereum/wiki/wiki/JavaScript-API](https://github.com/ethereum/wiki/wiki/JavaScript-API).
* MetaCoin:[http://truffleframework.com/docs/getting_started/contracts](http://truffleframework.com/docs/getting_started/contracts)
