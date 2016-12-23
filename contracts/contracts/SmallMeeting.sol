pragma solidity ^0.4.2;

import "Traits.sol";
import "SmallVote.sol";
import "SmallAuction.sol";

contract SmallMeeting is Meeting{

  event Established(uint startTime,address manager);

  event RecruitAndDecisionTimeSet(uint endTimeRecruit, uint decisonTime);

  event JoinApplied(address agent, string introduction);

  event NewMember(address who);

  event RecruitFinished(uint numOfMember);

  event Suggested(uint period, uint base);

  event Voted(address agent);

  event FormSet(uint period, uint base);

  event FailSuggest();

  event RefundBid(address agent, uint amount);

  event GiveBid(address agent, uint amount);

//  event Msg(uint state, bool finished, uint arg1, uint arg2);

  event BeginAuction(uint round);

  event Bidded(address agent, uint stage);

  event Revealed(address agent, uint stage);

  event AuctionEnd(uint stage);

  event AuctionSuccess(address winner, uint interest, uint stage);

  event BadAgent(address agent, uint stage, uint why, uint amount);// why: 1 no blindbid, 2 no reaveal, 3 fake bid

  event Wrong();

  event Debug(string error);

  event Debug2(bytes32 error);

  int64 stage = -2; // -3: error, -2:waiting for setting recruit time, -1: recuriting, 0: setting times,  1,2,3...N: period

  bool finishedOrVoting = false; // 0 on auction, 1 finished or on voting

  SmallAuction currentAuction;

  uint auctionStage;

  SmallVote currentVote;

  mapping(address=> bool) pendingMember;

  mapping(address=>string) intro;

  address[] membersArray;

  mapping(address=>bool) borrowed;

  mapping(uint=>uint) interest; // per person

  mapping(address=>uint) balance;

  uint startTime = now;

  uint recuritingEndTime = now + 200;

  uint firstAuctionTime;

  uint thisVoteEndTime;

  uint period = 500;

  uint base = 10;

  uint period_s;

  uint base_s;

  uint auctionVoteDuration = 100;

  uint checked = 0;

  uint doubleChecked = 0;

  function SmallMeeting(address _manager){
    yingdian(_manager);
    Established(startTime, _manager);
    stage = -2;
  }

  function processVote() internal{
    var (c,cn) = currentVote.countVote();
    if(c > membersArray.length / 2){
      stage = 1;
      period = period_s;
      base = base_s;
      firstAuctionTime = now;
      auctionVoteDuration = auctionVoteDuration;
      FormSet(period,base);

      newAuction();
      checked = 0;
      doubleChecked = 0;

    }else{
      FailSuggest();
    }
  }

  function checkAuction() internal{
    var N = membersArray.length;
    if(checked < N){
      if(!check(checked)){
        wrong();
      }
      checked += 1;
    }else if(checked == N){
      if(auctionStage == N){

        doubleChecked = 0;
        checked += 1;

      }else{
        var (suc,addr,amount) =  currentAuction.BidResult();
        if(!suc){
          wrong();
        }
        else{
          borrowed[addr] = true;
          interest[auctionStage] = amount - base;
          var b = balance[addr] + base + (base - (amount - base)) * (N - auctionStage) ;
          if(addr.send(b)){
            GiveBid(addr,b);
            balance[addr] = 0;

            doubleChecked = 0;
            checked += 1;
            AuctionSuccess(addr,amount,auctionStage);
          }else{
            balance[addr] = b;
            Debug("failed sending winner value");
          }
        }
      }
    }else if(doubleChecked < N){
      if(auctionStage == N){
        var agent1 = membersArray[doubleChecked];
        if(!borrowed[agent1]){
          balance[agent1] += base * N;
        }
        if(agent1.send(balance[agent1])){
          RefundBid(agent1,balance[agent1]);
          balance[agent1] = 0;
          doubleChecked += 1;
        }else{
          Debug("failed refunding");
        }

      }else{

        var agent = membersArray[doubleChecked];
        if(!borrowed[agent]){
          balance[agent] += interest[auctionStage];
        }
        if(agent.send(balance[agent])){
          RefundBid(agent,balance[agent]);
          balance[agent] = 0;
          doubleChecked += 1;
        }else{
          Debug("failed refunding");
        }

      }

    }
  }

  function check(uint _i) internal returns(bool){
    var agent = membersArray[_i];
    var (noblindbid, noreveal, lessGuarantee, amount, checkedbefore) = currentAuction.checkBidderAfterEnded(agent);
    var (suc,addr,sucAmount) =  currentAuction.BidResult();
    if(noblindbid){
      BadAgent(agent,auctionStage,1,base);
      return false;
    }else if(noreveal){
      BadAgent(agent,auctionStage,2,base);
      return false;
    }else if(lessGuarantee){
      BadAgent(agent,auctionStage,3,amount);
      return false;
    }
    if(!checkedbefore){
      balance[agent] += amount - base;
    }
    return true;
  }

  function wrong() internal{
    stage = -3;
    check(0);
    checked = 1;
    Wrong();
  }

  function checkFinished() internal returns(bool){
    if(stage>0 && finishedOrVoting && doubleChecked >= membersArray.length){
      return true;
    }else{
      return false;
    }
  }

  function newAuction() internal{
    currentAuction = new SmallAuction();
    auctionStage = uint(stage);
    BeginAuction(auctionStage);
  }

  function stringToBytes32(string memory source) constant internal returns (bytes32 result) {
      assembly {
          result := mload(add(source, 32))
      }
  }


  function bid(uint _stage, bytes32 _blindBid) payable onlyMember{
    bytes32 bb = _blindBid;//stringToBytes32(_blindBid);
    Debug2(_blindBid);
    if(stage>0 && !finishedOrVoting){
      trypush();
      if(stage>0 && !finishedOrVoting){
        if(auctionStage != _stage){
          throw;
        }
        if(msg.value < base){
          throw;
        }
        if(borrowed[msg.sender]){
          balance[msg.sender] = msg.value - base;
          if(currentAuction.Bid(msg.sender,sha3(base),base)){
            Bidded(msg.sender,auctionStage);
          }else{
            Debug("fail to bid");
          }
        }else{
          if(msg.value == base){
            throw;
          }
          if(currentAuction.Bid(msg.sender,bb,msg.value)){
            Bidded(msg.sender,auctionStage);
          }else{
            Debug("fail to bid");
          }
        }
      }
    }else{
      throw;
    }
  }


  function showBid(uint _amount, uint _stage) onlyMember{
    Debug2(sha3(_amount));

    if(stage>0 && !finishedOrVoting){
      trypush();
      if(stage>0 && !finishedOrVoting){
        if(auctionStage != _stage){
          throw;
        }
        if(borrowed[msg.sender]){
          if(currentAuction.ShowBid(msg.sender, base)){
            Revealed(msg.sender,auctionStage);
          }else{
            //throw;
          }
        }else{
          if(currentAuction.ShowBid(msg.sender, _amount)){
            Revealed(msg.sender,auctionStage);
          }else{
            //throw;
          }
        }
      }
    }
  }

  function trypush(){
    if(stage == -3){
      if(checked < membersArray.length){
        check(checked);
        checked += 1;
      }
      return;
    }
    var t = now;

    if(stage == -1){
      if(now > recuritingEndTime){
        stage = 0;
        finishedOrVoting = false;
      }
    }else if(stage == 0 && finishedOrVoting){
      if(t > thisVoteEndTime){
        finishedOrVoting = false;
        processVote();
      }
    }else if(stage > 0){
      var cur = 1;
      if(finishedOrVoting){
        cur = 2;
      }
      var s = (uint)(stage);

      var real = 0;
      if(t > firstAuctionTime + ( s + 1) * (period+auctionVoteDuration) ){
        real = 5;
      }else if(t > firstAuctionTime + s * (period+auctionVoteDuration) + auctionVoteDuration ){
        real = 4;
      }else if(t > firstAuctionTime + s * (period+auctionVoteDuration) ){
        real = 3;
      }else if(t > firstAuctionTime + (s - 1)  * (period+auctionVoteDuration) + auctionVoteDuration ){
        real = 2;
      }else{
        real = 1;
      }

      if(real - cur >  1){
        wrong();
      }else if(real < cur){
        Debug("real < cur");
      }else if(real == cur + 1){
        if(cur == 1){
          finishedOrVoting = true;
          AuctionEnd(auctionStage);
          checkAuction();
        }else{
          if (checkFinished()){
            stage = (int64)(s) + 1;
            finishedOrVoting = false;
            newAuction();
            checked = 0;
          }
        }
      }else if(cur == 2){
        checkAuction();
      }
    }
  }

  function join(string _intro){
    trypush();
    if(stage != -1){
      throw;
    }

    if(isAMember(msg.sender) || pendingMember[msg.sender] ){
      throw;
    }

    pendingMember[msg.sender] = true;
    intro[msg.sender] = _intro;
    JoinApplied(msg.sender,_intro);
  }

  function accept(address _address) onlyManager{
    Debug("acce?");
    trypush();
    if(stage != -1){
      throw;
    }

    if(pendingMember[_address]){
      pendingMember[_address] = false;
      addMember(_address);
      membersArray.push(_address);
      NewMember(_address);
    }
  }



  function getState() constant returns (int64, bool, uint, uint, uint, uint , uint, uint, uint, uint, uint, uint, uint,uint){
    //Debug("suc");
    return (stage, finishedOrVoting,  auctionStage, startTime , recuritingEndTime, firstAuctionTime, thisVoteEndTime, period, base, period_s, base_s, auctionVoteDuration, checked, doubleChecked);
  }

  function setRecuritAndVoteAuctionTime(uint _recuritEndtime, uint _decisionDuration) onlyManager{
    if(stage != -2){
      throw;
    }

    if(_recuritEndtime <= startTime){
      throw;
    }

    recuritingEndTime = _recuritEndtime;
    auctionVoteDuration = _decisionDuration;
    RecruitAndDecisionTimeSet( _recuritEndtime, _decisionDuration);

    stage = -1;
  }

  function decideAttr(uint _period, uint _base) onlyManager{
    if(stage!=0){
      throw;
    }
    period = _period;
    stage = 1;
    firstAuctionTime = now;
    FormSet(_period,_base);
    BeginAuction(1);
  }

  function suggestAttr(uint _period, uint _base) onlyMember{
    if(stage!=0 || finishedOrVoting){
      throw;
    }else{
        trypush();
        if(stage==0 || !finishedOrVoting){
          currentVote = new SmallVote();
          period_s = _period;
          base_s = _base;
          finishedOrVoting = true;
          thisVoteEndTime = now + auctionVoteDuration;
          Suggested(_period, _base);
        }
    }
  }

  function vote(bool _ayeOrNay) onlyMember{//_aye true; _nay false
    if(stage!=0 || !finishedOrVoting){
      throw;
    }else{
      trypush();
      if(stage==0 || finishedOrVoting){
        if(currentVote.voteFor(msg.sender,_ayeOrNay)){
          Voted(msg.sender);
        }else{
          Debug("fail to vote");
        }
      }
    }
  }



  /**UTILS*/
    function uintToBytes(uint v) internal constant returns (bytes32 ret) {
      if (v == 0) {
        ret = '0';
      }
      else {
        while (v > 0) {
            ret = bytes32(uint(ret) / (2 ** 8));
            ret |= bytes32(((v % 10) + 48) * 2 ** (8 * 31));
            v /= 10;
        }
      }
      return ret;
    }

    function bytes32ToString(bytes32 data) internal constant returns (string) {
      bytes memory bytesString = new bytes(32);
      for (uint j=0; j<32; j++) {
        byte char = byte(bytes32(uint(data) * 2 ** (8 * j)));
        if (char != 0) {
            bytesString[j] = char;
        }
      }
      return string(bytesString);
    }

    function uintToString(uint _x) internal constant returns(string){
      bytes32 data = uintToBytes(_x);
      string memory x = bytes32ToString(data);
      return x;
    }
}
