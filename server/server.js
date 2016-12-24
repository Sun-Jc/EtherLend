
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

//Lets require/import the HTTP module
var http = require('http');
var HttpDispatcher = require('httpdispatcher');
var dispatcher = new HttpDispatcher();

var Web3 = require('web3');
var web3 = new Web3();

var provider = 'http://taoli.tsinghuax.org:8545'
web3.setProvider(new web3.providers.HttpProvider(provider));
[Auction,ConvertLib,CreditRecorder,EthLendService,Insurance,Meeting,MetaCoin,Migrations,SmallAuction,SmallCreditRecorder,SmallEthLendService,SmallInsurance,SmallMeeting,SmallVote,Timer,Traits,Vote,membered,owned].forEach(
    function(contract) {
        contract.setProvider(web3.currentProvider);
})

//Lets define a port we want to listen to
const PORT=8085; 

//We need a function which handles requests and send response
function handleRequest(request, response){
    //response.end('It Works!! Path Hit: ' + request.url);
    try {
        //log the request on console
        console.log(request.url);
        //Disptach
        dispatcher.dispatch(request, response);
    } catch(err) {
        console.log(err);
    }
}

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

var accounts;
var account;
function js_getAccount(_i){
  return accounts[_i];
}
function js_getAccounts(){
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

function new_meeting(req, res){
  var service = SmallEthLendService.deployed();
  console.log(service);

  js_allEvents(service,0,true);

  js_applyMeeting(service,account).then(
    function(){
      console.log('new meeting got');

      meetingAddr = js_lastEventsOf(0).args.newContract;
      console.log(meetingAddr);

      meeting = SmallMeeting.at(meetingAddr);

      js_allEvents(meeting,1,true);
      //res.writeHead(200, {'Content-Type': 'text/plain'});
      res.end(JSON.stringify({'meetingAddr': meetingAddr}));
  }).catch(function(e){
    console.error(e.stack)
  });
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
  js_suggest(meeting, accounts[1], 60, 10);
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

////////////////////////////////////////

//For all your static (js/css/images/etc.) set the directory name (relative path).
dispatcher.setStatic('resources');

dispatcher.onGet("/balance", function(req, res) {
    var account = web3.eth.accounts[0];
    var balance = web3.eth.getBalance(account);
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end(JSON.stringify({'balance': balance.toString()}));
});    

dispatcher.onGet("/newMeeting", function(req, res) {new_meeting(req, res)})
dispatcher.onGet("/check", function(req, res) {get_stage()})
dispatcher.onGet("/set1", function(req, res) {set_recurit_vote_auction_time()})
dispatcher.onGet("/check", function(req, res) {get_stage()})
dispatcher.onGet("/join_all", function(req, res) {join_all()})
dispatcher.onGet("/accept_all", function(req, res) {accept_all()})
dispatcher.onGet("/push_get", function(req, res) {push_get()})
dispatcher.onGet("/suggest1", function(req, res) {suggest1()})
dispatcher.onGet("/vote", function(req, res) {vote_all()})
//dispatcher.onGet("/push_get", function(req, res) {push_get()})
dispatcher.onGet("/bid", function(req, res) {bid_next()})
dispatcher.onGet("/reaveal", function(req, res) {reveal_next()})
//dispatcher.onGet("/push_get", function(req, res) {push_get()})
dispatcher.onGet("/push", function(req, res) {push_try()})

dispatcher.onGet("/get_accounts", function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    js_getAccounts();
    res.end(JSON.stringify({'accounts': accounts}));
})

////////////////////////////////////////

js_getAccounts();
//Create a server
var server = http.createServer(handleRequest);

//Lets start our server
server.listen(PORT, function(){
    //Callback triggered when server is successfully listening. Hurray!
    console.log("Server listening on: http://localhost:%s", PORT);
});

server.on('error', function(e){
    console.error(e.stack)
})
