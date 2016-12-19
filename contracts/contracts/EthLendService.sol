pragma solidity ^0.4.2;

import "Traits.sol";

contract SmallEthLendService is EthLendService{
  function applyMeeting() returns (address newAddr){
    var r = new SmallMeeting(msg.sender);
    addMember(r);
    return r;
  }

  function destroyOneMeeting(address _meeting) onlyOwner{
    if(!isAMember(_meeting)){
      throw;
    }
    Meeting(_meeting).end();
  }
}

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

  uint Stage = 0; // -3: error, -2:waiting for setting recruit time, -1: recuriting, 0: setting times,  1,2,3...N: period

  bool finishedOrVoting = false; // 0 on auction, 1 finished or on voting

  SmallAuction cuurentAuction;

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
    Established(startTime, _manager)
  }

  function processVote() internal{
    var c = currentVote.countVote();
    if(c > membersArray.length / 2){
      stage = 1;
      period = period_s;
      base = base_s;
      FormSet(period,base);
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
      var agent = membersArray[checked];
      if(balance[agent] > 0){
        if(agent.send(balance[agent])){
          balance[agent] = 0;
        }
      }
      checked += 1;
    }else if(checked == N){
      var (suc,addr,amount) =  cuurentAuction.BidResult();
      if(!suc){
        wrong();
      }
      else{
        borrowed[addr] = true;
        interest[auctionStage] = amount - base;
        b = balance[addr] + base + (base - (amount - base)) * (N - auctionStage) ;
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
    }else if(doubleChecked < N){
      var agent = membersArray[checked];
      var (noblindbid, noreveal, lessGuarantee, amount, checkedbefore) = currentAuction.checkBidderAfterEnded(agent);
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

  function check(uint _i) internal returns(bool){
    var agent = membersArray[_i];
    var (noblindbid, noreveal, lessGuarantee, amount, checkedbefore) = currentAuction.checkBidderAfterEnded(agent);
    var (suc,addr,sucAmount) =  cuurentAuction.BidResult();
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

  function checkFinished() internal{
    if(stage>0 && finishedOrVoting && doubleChecked >= membersArray.length){
      return true;
    }else{
      return false;
    }
  }

  function newAuction() internal{
    currentAuction = new SmallAuction();
    auctionStage = stage;
    BeginAuction(auctionStage);
  }

  function bid(uint _stage, bytes32 _blindBid) payable onlyMember{
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
          if(currentAuction.bid(msg.sender,sha3(base,msg.sender),base)){
            Bidded(msg.sender,auctionStage);
          }else{
            Debug("fail to bid");
          }
        }else{
          if(msg.value == base){
            throw;
          }
          if(currentAuction.bid(msg.sender,blindBid,msg.value)){
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

  function showBid(uint _amount) onlyMember{
    if(stage>0 && !finishedOrVoting){
      trypush();
      if(stage>0 && !finishedOrVoting){
        if(auctionStage != _stage){
          throw;
        }
        if(borrowed[msg.sender]){
          if(currentAuction.ShowBid(msg.sender, base)){
            Revealed(msg.sender,auctionStage);
          }
        }else{
          if(currentAuction.ShowBid(msg.sender, _amount)){
            Revealed(msg.sender,auctionStage);
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
      var s = stage;

      var real = 0;
      if(t > firstAuctionTime + (s+1) * (period+auctionVoteDuration) ){
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
            stage = s + 1;
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
    if(stage != -1){
      throw;
    }

    if(isAMember[msg.sender]){
      throw;
    }

    pendingMember(msg.sender) = true;
    intro(msg.sender) = _intro;
    JoinApplied(msg.sender,_intro);
  }

  function accept(address _address) onlyManager{
    if(stage != -1){
      throw;
    }

    if(pendingMember[_address]){
      pendingMember[_address] = false;
      addMember(_address);
      membersArray.push(_address)
      NewMember(_address);
    }
  }

  function setRecuritAndVoteAuctionTime(uint _recuritDuration, uint _decisionDuration) onlyManager{
    if(stage != -2){
      throw;
    }

    recuritingDuration = _recuritDuration;
    auctionVoteDuration = _decisionDuration;
    RecruitTimeSet( startTime + _recuritDuration, _decisionDuration);

    stage = -1;
  }

  function decideAttr(uint _period, uint _base) onlyManager{
    if(stage!=0){
      throw;
    }
    period = _period;
    stage = 1;
    firstAuctionTime = now;
    FormSet(_period,_times,_base);
    BeginAuction(stage);
  }

  function suggestAttr(uint _period, uint _base) onlyMember{
    if(stage!=0 || finishedOrVoting){
      throw;
    }else{
        trypush();
        if(stage!=0 || finishedOrVoting){
          currentVote = new SmallVote();
          period_s = _period;
          base_s = _base;
          finishedOrVoting = true;
          Suggested(_period, _base);
        }
    }
  }

  function vote(bool _ayeOrNay) onlyMember{//_aye true; _nay false
    if(stage!=0 || !finishedOrVoting){
      throw;
    }else{
      trypush();
      if(stage!=0 || !finishedOrVoting){
        if(currentVote.voteFor(msg.sender,_ayeOrNay)){
          Voted(msg.sender);
        }else{
          Debug("fail to vote");
        }
      }
    }
  }

}
