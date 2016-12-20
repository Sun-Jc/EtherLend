var accounts;
var account;

web3.eth.getAccounts(function(err, accs) {
  if (err != null) {
    alert("There was an error fetching your accounts.");
    return;
  }

  if (accs.length == 0) {
    alert("Couldn't get any accounts! Make sure your Ethereum client is configured correctly.");
    return;
  }

  accounts = accs;
  account = accounts[0];
});


var service = SmallEthLendService.deployed();

var events = service.allEvents({fromBlock: 0, toBlock: 'latest'});
events.watch(function(error, result){
  console.log('x')
  if (!error)
    console.log(result);
});

service.applyMeeting({from:account}).then(function(){console.log(finished);}).catch(function(e){console.log(e)});
