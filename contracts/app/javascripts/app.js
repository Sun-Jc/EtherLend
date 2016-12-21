/*function setStatus(message) {
  var status = document.getElementById("status");
  status.innerHTML = message;
};

function refreshBalance() {
  var meta = MetaCoin.deployed();

  meta.getBalance.call(account, {from: account}).then(function(value) {
    var balance_element = document.getElementById("balance");
    balance_element.innerHTML = value.valueOf();
  }).catch(function(e) {
    console.log(e);
    setStatus("Error getting balance; see log.");
  });
};

function sendCoin() {
  var meta = MetaCoin.deployed();

  var amount = parseInt(document.getElementById("amount").value);
  var receiver = document.getElementById("receiver").value;

  setStatus("Initiating transaction... (please wait)");

  meta.sendCoin(receiver, amount, {from: account}).then(function() {
    setStatus("Transaction complete!");
    refreshBalance();
  }).catch(function(e) {
    console.log(e);
    setStatus("Error sending coin; see log.");
  });
};
*/

var lastEvent;
var meetingAddr;
var meeting;
var startTime;
var accounts;
var account;

function new_meeting(){
  var service = SmallEthLendService.deployed();

  var events = service.allEvents({fromBlock: 0, toBlock: 'latest'});
  events.watch(function(error, result){
    console.log('event')
    if (!error){
      console.log(result);
      lastEvent = result;
    }else{
      console.error(error);
    }
  });

  service.applyMeeting({from:account, gas:3000000}).then(
    function(){
      console.log('new meeting got');

      meetingAddr = lastEvent.args.newContract;
      console.log(meetingAddr);

      meeting = SmallMeeting.at(meetingAddr);
      var events1 = meeting.allEvents({fromBlock: 0, toBlock: 'latest'});
      events1.watch(function(error, result){
        console.log('events')
        if (!error){
          console.log(result);
          lastEvent = result;
        }else{
          console.error(error);
        }
      });
  }).catch(function(e){console.error(e)});
}

function set_recurit_vote_auction_time(){
  startTime = lastEvent.args.startTime;
  meeting.setRecuritAndVoteAuctionTime(startTime + 60, 60,{from:account});
}

function get_stage(){
  meeting.getState({from:account}).then(
    function(v){
      //var a = ["stage", "finishedOrVoting", "auctionStage", "startTime", "recuritingEndTime", "firstAuctionTime", "thisVoteEndTime", "period", "base", "period_s", "base_s", "auctionVoteDuration", "checked", "doubleChecked"]
      //for(var i = 0;i<a.length;i++){
      //  console.log(a[i].toString()+": "+v[i].toString());
      //}
      console.log(v.toString());
    }).catch(function(e){console.error(e);});
}


window.onload = function() {
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

    //refreshBalance();
  });
}
