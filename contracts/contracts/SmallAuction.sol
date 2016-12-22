pragma solidity ^0.4.2;

import "Traits.sol";


contract SmallAuction is Auction{

  mapping(address => bytes32) bidBlind;
  mapping(address => uint) bid;
  mapping(address => bool) bidded;
  mapping(address => bool) revealed;
  mapping(address => uint) balance;
  mapping(address => bool) checked;

  address firstBidder;
  uint firstBid = 0;

  address secondBidder;
  uint secondBid = 0;


  function Bid(address _address, bytes32 _blindBid, uint _desposit) onlyOwner returns(bool){
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
    if(!bidded[_address] || revealed[_address]){
      throw;
    }

    if(sha3(_bid) == bidBlind[_address]){
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
    if(secondBid == 0){
      return (false,msg.sender,0);
    }else{
      return (true,secondBidder,secondBid);
    }
  }

  function checkBidderAfterEnded(address _agent) returns(bool noblindbid, bool noreveal, bool lessGuarantee,uint amount, bool checkedbefore){
    if(!bidded[_agent]){
      return (true,false,false,0,false);
    }
    if(!revealed[_agent]){
      return (false,true,false,0,false);
    }
    if(bid[_agent] >= balance[_agent]){
      return (false,false,true,bid[_agent],false);
    }
    if(!checked[_agent]){
      checked[_agent] = true;
      return (false,false,false,balance[_agent],false);
    }else{
      return (false,false,false,balance[_agent],true);
    }
  }
}
