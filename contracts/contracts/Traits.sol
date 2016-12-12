pragma solidity ^0.4.2;

contract Traits{

}

// owned
contract owned {

    address public owner;

    bool okayToEnd;

    address public manager;

    function owned(address _manager) {
        owner = msg.sender;
        okayToEnd = false;
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

    function end() onlyOwner{
        if( !okayToEnd){
          throw;
        }
        selfdestruct(msg.sender);
    }
}

// membered
contract membered is owned {

  mapping(address => bool) isMember;

  modifier onlyMember {
      if ( !isMember[msg.sender] ) throw;
      _;
  }

  function addMember(address _agent) internal{
    isMember[_agent] = true;
  }

  function isAMember(address _agent) onlyMember returns(bool ism){
    return isMember[_agent];
  }

}

// vote, must be called by membered. vote one by one, total is fixed
contract Vote is owned{

  function voteFor(bool _vote);

  function countVote() onlyOwner returns (int count);
}

// auction
contract Auciton is owned{

  function Bid(bytes32 _blindedBid) payable;

  function ShowBid(uint _bid);

  function Refund();

  function BidResult() returns (address addr,uint ammount);
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

  function pay(uint insuranceId, bytes32 _recordId, address _agent, uint _amount, uint _insuranceId) onlyMember returns (bool ok);
}

// timer
contract Timer is owned{

  function trigger();

  function register(uint _after, uint _before, string _method, address _address, uint _gasLimit) payable returns(bool ok);
}

// master
contract EthLendService is membered{
  function applyMeeting() returns (address newAddr);
}

// meeting
contract Meeting is membered{

  function join();

  function setDecisionTime(uint _duration) onlyManager;

  function suggestAttr(uint _period, uint _times) onlyMember;

  function getMsg() onlyMember returns (uint, string, address) ; // -x: x's auction; +x: x's offer; 0: voting for string

  function offer(uint _money) onlyMember;

  function withdraw() onlyMember; //must pulled by user, cannot push

  function push();
}
