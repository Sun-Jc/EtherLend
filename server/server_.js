
var Auction = require("/home/songzy/EtherLend/truffle/build/contracts/Auction.sol.js")
var ConvertLib = require("/home/songzy/EtherLend/truffle/build/contracts/ConvertLib.sol.js")
var CreditRecorder = require("/home/songzy/EtherLend/truffle/build/contracts/CreditRecorder.sol.js")
var EthLendService = require("/home/songzy/EtherLend/truffle/build/contracts/EthLendService.sol.js")
var Insurance = require("/home/songzy/EtherLend/truffle/build/contracts/Insurance.sol.js")
var Meeting = require("/home/songzy/EtherLend/truffle/build/contracts/Meeting.sol.js")
var Migrations = require("/home/songzy/EtherLend/truffle/build/contracts/Migrations.sol.js")
var MetaCoin = require("/home/songzy/EtherLend/truffle/build/contracts/MetaCoin.sol.js")
var SmallAuction = require("/home/songzy/EtherLend/truffle/build/contracts/SmallAuction.sol.js")
var SmallCreditRecorder = require("/home/songzy/EtherLend/truffle/build/contracts/SmallCreditRecorder.sol.js")
var SmallEthLendService = require("/home/songzy/EtherLend/truffle/build/contracts/SmallEthLendService.sol.js")
var SmallInsurance = require("/home/songzy/EtherLend/truffle/build/contracts/SmallInsurance.sol.js")
var SmallMeeting = require("/home/songzy/EtherLend/truffle/build/contracts/SmallMeeting.sol.js")
var SmallVote = require("/home/songzy/EtherLend/truffle/build/contracts/SmallVote.sol.js")
var Timer = require("/home/songzy/EtherLend/truffle/build/contracts/Timer.sol.js")
var Traits = require("/home/songzy/EtherLend/truffle/build/contracts/Traits.sol.js")
var Vote = require("/home/songzy/EtherLend/truffle/build/contracts/Vote.sol.js")
var membered = require("/home/songzy/EtherLend/truffle/build/contracts/membered.sol.js")
var owned = require("/home/songzy/EtherLend/truffle/build/contracts/owned.sol.js")

////////////////////////////////////////

var Web3 = require('web3');
var web3 = new Web3();

var provider = 'http://taoli.tsinghuax.org:8545'
web3.setProvider(new web3.providers.HttpProvider(provider));
[Auction,ConvertLib,CreditRecorder,EthLendService,Insurance,Meeting,MetaCoin,Migrations,SmallAuction,SmallCreditRecorder,SmallEthLendService,SmallInsurance,SmallMeeting,SmallVote,Timer,Traits,Vote,membered,owned].forEach(
    function(contract) {
        contract.setProvider(web3.currentProvider);
})

////////////////////////////////////////

var meetingAddr;
var meeting;

var startTime;

var round = 1;


function js_applyMeeting(_service, _managerSelf){
  return _service.applyMeeting({from:_managerSelf, gas:3000000})
}

// when: seconds
function js_setBasicTime(_meeting, _managerSelf, _whenEndRecruiting, _howLongAuctionVote){
  console.log(_managerSelf, _whenEndRecruiting, _howLongAuctionVote, _meeting.address)
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
  console.log(_meeting.address, _account, _howLong, _howMuch)
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
var meeting2index = {} 

function initMeetings(service){
  js_events[0] = new Array();
  var events = service.allEvents({fromBlock: 0, toBlock: 'latest'});
  events.watch(function(error, result){
    if (!error){
      if (result.event == 'NewMeeting') {
          index = Object.keys(meeting2index).length+1;
          meeting2index[result.args.newContract] = index;
          meeting = SmallMeeting.at(result.args.newContract);
          js_allEvents(meeting, index, true);
      }
    }else{
      console.log("wrong event at "+_contract.address);
      console.error(error);
    }
  });
}
var service = SmallEthLendService.deployed();
initMeetings(service);

function js_allEvents(_contract, _whichJsEvents, _logOut){
  console.log('whichJsEvents:', _whichJsEvents);
  if (js_events[_whichJsEvents] == undefined)
      js_events[_whichJsEvents] = new Array();

  var events = _contract.allEvents({fromBlock: 0, toBlock: 'latest'});

  events.watch(function(error, result){
    if (!error){
      if(_logOut){
        console.log('event at '+_contract.address)
        console.log(result);
      }
      js_events[_whichJsEvents].push(result);
    }else{
      console.log("wrong event at "+_contract.address);
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

function js_getService(req, res){
  //return SmallEthLendService.deployed();
  console.log(SmallEthLendService.deployed().address)
  res.end(JSON.stringify({'deployedAddress': SmallEthLendService.deployed().address}));
}

var accounts;
var account;
function js_getAccount(_i){
  return accounts[_i];
}
function js_getAccounts(req, res){
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
}

////////////////////////////////////////

//Lets define a port we want to listen to
const PORT=8085; 

var express = require("express");
var app = express();

app.get("/getAccounts", function(req, res) {
    res.end(JSON.stringify({'accounts': accounts}));
})

app.get("/getService", function(req, res) {
    js_getService(req, res)
})

app.get("/getBalance/:id", function(req, res) {
    var account = web3.eth.accounts[req.params.id];
    var balance = web3.eth.getBalance(account);
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end(JSON.stringify({'balance': balance.toString()}));
});    

app.get("/applyMeeting/:id", function(req, res) {
    var service = SmallEthLendService.deployed();

    js_applyMeeting(service,accounts[req.params.id]).then(
        function(){
          console.log('new meeting got');
          res.end(JSON.stringify({'res': 'done'}));
    }).catch(function(e){
        console.error(e.stack)
    });
})

app.get("/join/:meetingAddr/:id", function(req, res) {
  meeting = SmallMeeting.at(req.params.meetingAddr);
  ret = js_join(meeting, accounts[req.params.id], "I AM "+req.params.id);
  // TODO
  res.end(JSON.stringify({'res': 'done'}));
})

app.get("/getMeetings", function(req, res) {
  console.log(meeting2index);
  //res.end(JSON.stringify({'meetingAddrs': Object.keys(meeting2index)}));
  res.end(JSON.stringify(meeting2index));
})

app.get("/set/:meetingAddr/:id/:whenEnd/:howLong", function(req, res) {
  meeting = SmallMeeting.at(req.params.meetingAddr);
  startTime = js_events[meeting2index[req.params.meetingAddr]][0].args.startTime;
  whenEnd = parseInt(startTime) + parseInt(req.params.whenEnd)
  howLong = parseInt(req.params.howLong)
  //whenEnd = web3.toBigNumber(startTime) + web3.toBigNumber(req.params.whenEnd)
  //whenEnd = web3.toBigNumber(req.params.whenEnd)
  //howLong = web3.toBigNumber(req.params.howLong)
  js_setBasicTime(meeting, accounts[req.params.id], whenEnd, howLong);
  res.end(JSON.stringify({startTime: startTime, whenEnd: whenEnd, howLong: howLong}));
})

app.get("/suggest/:meetingAddr/:id/:howLong/:howMuch", function(req, res) {
    meeting = SmallMeeting.at(req.params.meetingAddr);
    js_suggest(meeting, accounts[req.params.id], parseInt(req.params.howLong), parseInt(req.params.howMuch))
  res.end(JSON.stringify({'res':'done'}));
})

app.get("/accept/:meetingAddr/:member/:manager", function(req, res) {
    meeting = SmallMeeting.at(req.params.meetingAddr);
    js_accept(meeting, accounts[req.params.member], accounts[req.params.manager]);
  res.end(JSON.stringify({'res':'done'}));
})

app.get("/bid/:meetingAddr/:id/:round/:bidHowMuch/:payHowMuch", function(req, res) {
    meeting = SmallMeeting.at(req.params.meetingAddr);
    js_bidEther(meeting, accounts[req.params.id], req.params.round, 
                req.params.bidHowMuch, req.params.payHowMuch).
                then(function(){ console.log("bidded");});
  res.end(JSON.stringify({'res':'done'}));
})

app.get("/reaveal/:meetingAddr/:id/:round/:revealHowMuch", function(req, res) {
  meeting = SmallMeeting.at(req.params.meetingAddr);
  js_showBidEther(meeting, accounts[req.params.id], req.params.round, 
                  req.params.revealHowMuch).
                  then(function(){ console.log("revealed");});
  res.end(JSON.stringify({'res':'done'}));
})


app.get("/getEvents/:meetingAddr", function(req, res) {
    index = meeting2index[req.params.meetingAddr];
    array = new Array();
    for (var i = 0; i < js_events[index].length; i++) {
        array.push(js_events[index][i]);
        //array.push(JSON.stringify(js_events[index][i]));
    }
    res.end(JSON.stringify({'events':array}));
})

app.get("/vote/:meetingAddr/:id/:aye", function(req, res) {
  meeting = SmallMeeting.at(req.params.meetingAddr);
  console.log(req.params.aye == 'aye')
  js_voteFor(meeting, accounts[req.params.id], req.params.aye == 'aye' ? true: false);
  res.end(JSON.stringify({'res':'done'}));
})

app.get("/push/:meetingAddr/:id", function(req, res) {
  meeting = SmallMeeting.at(req.params.meetingAddr);
  js_push(meeting,accounts[req.params.id]);
  res.redirect('/check/'+req.params.meetingAddr+'/'+req.params.id);
  //res.end(JSON.stringify({'res':'done'}));
})

app.get("/check/:meetingAddr/:id", function(req, res) {
   meeting = SmallMeeting.at(req.params.meetingAddr);
   meeting.getState.call({from:accounts[req.params.id]}).then(
    function(v){
      state = {}
      console.log("\n")
      var a = ["stage", "finishedOrVoting", "auctionStage", "startTime", "recuritingEndTime", "firstAuctionTime", "thisVoteEndTime", "period", "base", "period_s", "base_s", "auctionVoteDuration", "checked", "doubleChecked"]
      for(var i = 0;i<a.length;i++){
        state[a[i].toString()] = v[i].toString();
      }
      res.end(JSON.stringify({'state':state}));
    }).catch(function(e){
      console.log("bad");
      console.error(e);});
})

////////////////////////////////////////

js_getAccounts();
app.listen(PORT)
