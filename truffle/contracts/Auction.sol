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

  function Bid(bytes32 _blindBid) payable{
    if ( !membered(owner).isAMember(msg.sender) || !ended ){ // only onwer's member can vote
      throw;
    }

    if (revealed[msg.sender] == true){
      throw;
    }

    if(!bidded[msg.sender]){
      bidded[msg.sender] = true;
      balance[msg.sender] = 0;
    }

    balance[msg.sender] += msg.value;
    bidBlind[msg.sender] = _blindBid;

  }

  function ShowBid(uint _bid){
    if ( !membered(owner).isAMember(msg.sender) || !ended ){ // only onwer's member can vote
      throw;
    }

    if(!bidded[msg.sender] || revealed[msg.sender]){
      throw;
    }

    if(sha3(_bid,msg.sender) == bidBlind[msg.sender]){
      revealed[msg.sender] = true;
      bid[msg.sender] = _bid;
      if(_bid > firstBid){ // only first people get the bid
        secondBid = firstBid;
        firstBid = _bid;
        secondBidder = firstBidder;
        firstBidder = msg.sender;
      }else if(_bid > secondBid){
        secondBid = _bid;
        secondBidder = msg.sender;
      }
    }else{
      throw;
    }
  }

  function Refund() returns (bool){
    if(!ended){
      throw;
    }

    uint b = balance[msg.sender];
    if(b < bid[msg.sender]){
      Meeting(owner).failAuction(msg.sender,secondBid);
    }else{
      if(secondBidder == msg.sender){
        if(msg.sender.send(b - secondBid)){
          return true;
        }
      }else{
        if(msg.sender.send(b)){
          return true;
        }
      }
    }
  }

  function BidResult() returns (bool suc, address addr,uint ammount){
    if ( !ended ){
      throw;
    }
    if(secondBid == 0){
      return (false,msg.sender,0);
    }else{
      return (true,secondBidder,secondBid);
    }
  }

  function retriveBid() onlyOwner returns (bool){
    if(owner.send(secondBid)){
      return true;
    }
    return false;
  }
}
