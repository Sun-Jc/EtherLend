pragma solidity ^0.4.2;

contract Traits{

}

// owned
contract owned {

    address public owner;

    bool okayToEnd;

    address public manager;

    function owned() {
        owner = msg.sender;
        okayToEnd = true; // should be false defaultly
    }

    function yingdian(address _manager){
      manager = _manager;
    }

    modifier onlyOwner {
        if (msg.sender != owner) throw;
        _;
    }

    modifier onlyManager{
      if (msg.sender != manager) throw;
      _;
    }

    function transferOwnership(address newOwner) onlyOwner {
        owner = newOwner;
    }

    function end(address _toWho) onlyOwner{
        if( !okayToEnd){
          throw;
        }
        selfdestruct(_toWho);
    }
}

// membered
contract membered is owned {

  mapping(address => bool) isMember;

  modifier onlyMember {
      if ( !isMember[msg.sender] ) throw;
      _;
  }

  function addMember(address _agent) onlyOwner{
    isMember[_agent] = true;
  }

  function isAMember(address _agent) returns(bool ism){
    return isMember[_agent];
  }

}

// vote, must be called by membered. vote one by one, total is fixed
contract Vote is owned{

  function voteFor(address _agent, bool _vote) onlyOwner returns(bool);

  function countVote() onlyOwner returns (uint aye,uint nay);
}

// auction
contract Auction is owned{

  function Bid(address _address, bytes32 _blindBid, uint _desposit) onlyOwner returns(bool);

  function ShowBid(address _address, uint _bid) onlyOwner returns (bool);

  function BidResult() returns (bool suc, address addr,uint ammount);

  function checkBidderAfterEnded(address _agent) returns(bool noblindbid, bool noreveal, bool lessGuarantee,uint amount, bool checkedbefore);

}

// credit recorder
contract CreditRecorder is membered{

  function addBadRecord(address _agent,uint _amount, uint _insuranceId) onlyMember returns(bytes32 id);

  function retriveRecord(bytes32 _recordId) returns (address _agent,uint _amount, address reporter, uint _insuranceId);
}

// insurance company
contract Insurance is membered{

  function askPrice() onlyMember returns(uint charge);

  function buy(uint _guarantee) payable onlyMember returns (uint id);

  function pay(bytes32 _recordId, address _agent, uint _amount, uint _insuranceId) onlyMember returns (bool ok);
}

// timer
contract Timer is owned{

  function trigger();

  function register(uint _after, uint _before, string _method, address _address, uint _gasLimit) payable returns(bool ok);
}

// master
contract EthLendService is membered{
  //function applyMeeting();
}

// meeting
contract Meeting is membered{

/*  function join();

  function setDecisionTime(uint _duration) onlyManager;

  function setAuctionTime(uint _duration) onlyManager;

  function suggestAttr(uint _period, uint _times) onlyMember;

  function getMsg() onlyMember; // -x: x's auction; +x: x's offer; 0: voting for string

  function withdraw() onlyMember; //must pulled by user, cannot push

  function push();

  function check(address _who) returns(bool);*/

}
