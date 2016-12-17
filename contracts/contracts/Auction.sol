pragma solidity ^0.4.2;

import "Traits.sol";


contract SmallAuciton is Auction{

  mapping(address => bytes32) bidBlind;
  mapping(address => uint) bid;
  mapping(address => bool) bidded;
  mapping(address => bool) revealed;
  mapping(address => uint) balance;

  bool ended = false;

  uint endTime;

  address firstBidder;
  uint firstBid = 0;

  address secondBidder;
  uint secondBid = 0;

  function SmallAuction(uint _endTime){
    endTime = _endTime;
  }

  function tryEnd(){
    if(now >= endTime){
      ended = true;
    }
  }

  function Bid(address _address, bytes32 _blindBid, uint _desposit) onlyOwner returns(bool){
    tryEnd();

    if (ended){
      throw;
    }

    if (revealed[_address] == true){
      throw;
    }

    if(!bidded[_address]){
      bidded[_address] = true;
      balance[_address] = 0;
    }

    balance[_address] += _desposit;
    bidBlind[_address] = _blindBid;

    return true;
  }

  function ShowBid(address _address, uint _bid) onlyOwner returns (bool){
    tryEnd();

    if (ended){
      throw;
    }

    if(!bidded[] || revealed[_address]){
      throw;
    }

    if(sha3(_bid,_address) == bidBlind[_address]){
      revealed[_address] = true;
      bid[_address] = _bid;
      if(_bid > firstBid){ // only first people get the bid
        secondBid = firstBid;
        firstBid = _bid;
        secondBidder = firstBidder;
        firstBidder = _address;
      }else if(_bid > secondBid){
        secondBid = _bid;
        secondBidder = _address;
      }
      return true;

    }else{
      throw;
    }
  }

  function BidResult() returns (bool suc, address addr,uint ammount){
    tryEnd();
    if ( !ended ){
      throw;
    }
    if(secondBid == 0){
      return (false,msg.sender,0);
    }else{
      return (true,secondBidder,secondBid);
    }
  }

  function checkBidderAfterEnded(address _agent) returns(bool noblindbid, bool noreveal, bool lessGuarantee,uint amount){
    tryEnd();
    if(!ended){
      throw;
    }
    if(!bidded[_agent]){
      return (true,false,false,0);
    }
    if(!revealed[_agent]){
      return (false,true,false,0);
    }
    if(bid[_agent] >= balance[_agent]){
      return (false,false,true,bid[_agent]);
    }
    return true;
  }
}
