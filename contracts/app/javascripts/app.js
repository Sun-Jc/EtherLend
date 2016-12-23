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

var meetingAddr;
var meeting;

var startTime;

var accounts;
var account;

var round = 1;


/**
*/

function js_applyMeeting(_service, _managerSelf){
  return _service.applyMeeting({from:_managerSelf, gas:3000000})
}

// when: seconds
function js_setBasicTime(_meeting, _managerSelf, _whenEndRecruiting, _howLongAuctionVote){
  _meeting.setRecuritAndVoteAuctionTime(_whenEndRecruiting , _howLongAuctionVote , {from:_managerSelf, gas:3000000});
}

function js_join(_meeting, _memberSelf, _selfIntro){
  return _meeting.join(_selfIntro,{from:_memberSelf, gas:3000000});
}

function js_accept(_meeting, _member, _managerSelf){
  return _meeting.accept(_member,{from:_managerSelf, gas:3000000});
}

// how long: seconds
function js_suggest(_meeting, _account, _howLong, _howMuch){
  return _meeting.suggestAttr(_howLong, web3.toBigNumber(web3.toWei(_howMuch)),{from:_account, gas:3000000});
}

function js_voteFor(_meeting, _account, _ayeOrNay){
  return _meeting.vote(_ayeOrNay,{from:_account, gas:3000000});
}

function js_bidEther(_meeting, _account, _round, _bidHowMuch, _payHowMuch){
  return _meeting.bid(_round,web3.sha3(toUint256(web3.toBigNumber(web3.toWei(_bidHowMuch))),{encoding:'hex'} ),{from:_account, gas:3000000, value: web3.toBigNumber(web3.toWei(_payHowMuch)) } );
}

function js_showBidEther(_meeting,_account, _round, _revealHowMuch){
  return _meeting.showBid(web3.toBigNumber(web3.toWei(_revealHowMuch)),_round,{from:_account, gas:4000000});
}

function js_push(_meeting,_account){
  return _meeting.trypush({from:_account, gas:3000000})
}


var js_events = new Array();

function js_allEvents(_contract, _whichJsEvents, _logOut){
  js_events[_whichJsEvents] = new Array();

  var events = _contract.allEvents({fromBlock: 0, toBlock: 'latest'});

  events.watch(function(error, result){
    if (!error){
      if(_logOut){
        console.log('event at '+_contract)
        console.log(result);
      }
      js_events[_whichJsEvents].push(result);
    }else{
      console.log("wrong event at "+_contract);
      console.error(error);
    }
  });
}

function js_lastEventsOf(_whichJsEvent){
  return js_events[_whichJsEvent][js_events[_whichJsEvent].length-1]
}

function js_whoseEther(_who){
  return web3.eth.getBalance(_who).toString()/(web3.toBigNumber(web3.toWei(1)));
}

function toUint256(x){
  var y = x.toString(16);
  var z = "";
  var n = y.length;
  for(var i =n ;i<64;i++){z += "0";}
  return "0x"+z+y;
}

function js_getService(){
  return SmallEthLendService.deployed();
}

function js_getAccount(_i){
  var ac;
  web3.eth.getAccounts(function(err, accs) {
    if (err != null) {
      alert("There was an error fetching your accounts.");
      return;
    }

    if (accs.length == 0) {
      alert("Couldn't get any accounts! Make sure your Ethereum client is configured correctly.");
      return;
    }

    ac = accs[_i];

    //refreshBalance();
  });
  return ac;
}

////////

function new_meeting(){
  var service = SmallEthLendService.deployed();

  js_allEvents(service,0,true);

  js_applyMeeting(service,account).then(
    function(){
      console.log('new meeting got');

      meetingAddr = js_lastEventsOf(0).args.newContract;
      console.log(meetingAddr);

      meeting = SmallMeeting.at(meetingAddr);

      js_allEvents(meeting,1,true);
  }).catch(function(e){console.error(e)});
}

function set_recurit_vote_auction_time(){
  startTime = js_lastEventsOf(1).args.startTime;
  js_setBasicTime(meeting, account, startTime.toNumber() + 5 , 5);
}

function join_all(){
  js_join(meeting, accounts[1], "I AM FIRST");
  js_join(meeting, accounts[2], "I AM SECOND");
  js_join(meeting, accounts[3], "I AM THIRD");
}

function accept_all(){
  js_accept(meeting, accounts[1], account);
  js_accept(meeting, accounts[2], account);
  js_accept(meeting, accounts[3], account);
}

function suggest1(){
  js_suggest(meeting, accounts[1], 600, 10);
}

function vote_all(){
  js_voteFor(meeting, accounts[1], true);
  js_voteFor(meeting, accounts[2], true);
  js_voteFor(meeting, accounts[3], true);
}

function bid_next(){
  js_bidEther(meeting,accounts[1],round,13,13);
  js_bidEther(meeting,accounts[3],round,11,11);
  js_bidEther(meeting,accounts[2],round,12,12);
}

function reveal_next(){
  js_showBidEther(meeting,accounts[1], round, 13).then(function(){console.log("revealed");});
  js_showBidEther(meeting,accounts[3], round, 11).then(function(){console.log("revealed");});
  js_showBidEther(meeting,accounts[2], round, 12).then(function(){console.log("revealed");});
  round ++;
}

function push_try(){
  js_push(meeting,account);
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
