pragma solidity ^0.4.2;

import "Traits.sol";
import "Auction.sol"
import "Vote.sol"

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

  event Suggested(uint period, uint times, uint endTime);

  event Voted(address agent);

  event FormSet(uint period, uint times, uint base);

  event FailSuggest();

//  event Msg(uint state, bool finished, uint arg1, uint arg2);

  event BeginAuction(uint round);

  event Bided(address agent);

  event Revealed(address agent);

  event AuctionSuccess(address winner, uint interest);

  event BadAgent(address agent, uint stage, bool noBlindBid, bool noRevealBid, bool lessDeposit, uint amount);

  event Wrong();

  uint Stage = 0; // -3: error, -2:waiting for setting recruit time, -1: recuriting, 0: setting times,  1,2,3...N: period

  bool finishedOrVoting = false; // 0 on auction, 1 finished or on voting

  SmallAuction cuurentAuction;

  address currentWinner;

  SmallVote currentVote;

  uint base = 10;

  mapping(address=> bool) pendingMember;

  mapping(address=>string) intro;

  address[] membersArray;

  mapping(address=>bool) borrowed;

  mapping(address=>uint) interest;

  mapping(address=> uint) balances;

  uint startTime = now;

  uint recuritingEndTime = now + 200;

  uint firstAuctionTime;

  uint thisVoteEndTime;

  uint period = 500;

  uint N = 3;

  uint auctionVoteDuration = 100;

  uint checked = 0;


  function SmallMeeting(address _manager){
    yingdian(_manager);
    Established(startTime, _manager)
  }

  function wrong() internal{
    stage = -3;
    check(0);
    checked = 1;
    Wrong();
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
      if(s > thisVoteEndTime){
        finishedOrVoting = false;
        FailSuggest();
      }
    }else if(stage > 0){
      var s = 1 + (t - firstAuctionTime ) / (period + auctionVoteDuration));
      if(s > stage + 1){
        wrong();
      }else{
        if(finishedOrVoting){
          if(  t - firstAuctionTime  > period + ( s - 1 )* (period + auctionVoteDuration) ){
            var (suc,addr,amount) = currentAuction.BidResult;
            if(!suc){
              wrong();
              return;
            }
            borrowed[addr] = true;
            interest[addr] = amount - base;
            currentWinner = addr;
            finishedOrVoting = true;
            AuctionSuccess(addr, amount - interest);
          }
        }
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

  function decideAttr(uint _period, uint _times, uint _base) onlyManager{
    if(stage!=0){
      throw;
    }
    period = _period;
    N = _times;
    stage = 1;
    firstAuctionTime = now;
    FormSet(_period,_times,_base);
    BeginAuction(stage);
  }

  /*function suggestAttr(uint _period, uint _times, uint _base) onlyManager{
    if(stage!=0 || finishedOrVoting){
      throw;
    }
  }*/

  function bid()
}
