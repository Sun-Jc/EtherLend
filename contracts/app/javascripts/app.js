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
      console.log("wrong");
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
          console.log("wrongs");
          console.error(error);
        }
      });
  }).catch(function(e){console.error(e)});
}

function set_recurit_vote_auction_time(){
  startTime = lastEvent.args.startTime;
  meeting.setRecuritAndVoteAuctionTime(startTime.toNumber() + 5, 5,{from:account, gas:3000000});
}

function join_all(){
  meeting.join("I AM FIRST",{from:accounts[1], gas:3000000});
  meeting.join("I AM SECOND",{from:accounts[2], gas:3000000});
  meeting.join("I AM THIRD",{from:accounts[3], gas:3000000});
}

function accept_all(){
  meeting.accept(accounts[1],{from:account, gas:3000000});
  meeting.accept(accounts[2],{from:account, gas:3000000});
  meeting.accept(accounts[3],{from:account, gas:3000000});
}

function suggest1(){
  meeting.suggestAttr(600,100,{from:accounts[1], gas:3000000});
}

function vote_all(){
  meeting.vote(true,{from:accounts[1], gas:3000000});
  meeting.vote(true,{from:accounts[2], gas:3000000});
  meeting.vote(true,{from:accounts[3], gas:3000000});
}

var round = 1;

function bid_next(){
  meeting.bid(round,web3.sha3(toUint256(130),{encoding:'hex'}),{from:accounts[1], gas:3000000, value: 130});
  meeting.bid(round,web3.sha3(toUint256(110),{encoding:'hex'}),{from:accounts[3], gas:3000000, value: 110});
  meeting.bid(round,web3.sha3(toUint256(120),{encoding:'hex'}),{from:accounts[2], gas:3000000, value: 120});
}

function toUint256(x){
  var y = x.toString(16);
  var z = "";
  var n = y.length;
  for(var i =n ;i<64;i++){z += "0";}
  return "0x"+z+y;
}

function reveal_next(){
  meeting.showBid(130,round,{from:accounts[1], gas:4000000}).then(function(){console.log("revealed");});
  meeting.showBid(110,round,{from:accounts[3], gas:4000000}).then(function(){console.log("revealed");});
  meeting.showBid(120,round,{from:accounts[2], gas:4000000}).then(function(){console.log("revealed");});
}

function push_try(){
  meeting.trypush({from:account, gas:3000000})
}

function get_stage(){
  meeting.getState.call({from:account}).then(
    function(v){
      console.log("\n")
      var a = ["stage", "finishedOrVoting", "auctionStage", "startTime", "recuritingEndTime", "firstAuctionTime", "thisVoteEndTime", "period", "base", "period_s", "base_s", "auctionVoteDuration", "checked", "doubleChecked"]
      for(var i = 0;i<a.length;i++){
        console.log(a[i].toString()+": "+v[i].toString());
      }
      //console.log(v.toString());
    }).catch(function(e){
      console.log("bad");
      console.error(e);});
}

function push_get(){
  push_try();
  get_stage();
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
