
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

  mapping[address => bool] isMember;

  modifier onlyMember {
      if ( !isMember[msg.sender] ) throw;
      _;
  }

  function addMember(address _agent) internal{
    isMember[_agent] = true;
  }

  function isMember(address _agent) bool onlyMember{
    return isMember[_agent];
  }

}

// vote, must be called by membered. vote one by one, total is fixed
contract Vote is owned{

  function voteFor(bool _vote);

  function countVote() int onlyOwner;
}

// auction
contract Auciton is owned{

  function Bid(bytes32 _blindedBid) payable;

  function ShowBid(uint _bid);

  function Refund();

  function BidResult() (address,uint);
}

// credit recorder
contract CreditRecorder is membered{

  function addBadRecord(address _agent,uint _amount, uint _insuranceId) bytes32 onlyMember;

  function retriveRecord(bytes32 _recordId) (address _agent,uint _amount, uint _insuranceId);
}

// insurance company
contract Insurance is membered{

  function askPrice() uint onlyMember;

  function buy(uint _guarantee) payable uint onlyMember;

  function pay(uint insuranceId, bytes32 _recordId, address _agent, uint _amount, uint _insuranceId) bool onlyMember;
}

// timer
contract Timer is owned{

  function trigger();

  function register payable(uint _after, uint _before, string _method, address _address, uint _gasLimit);
}

// master
contract EthLendService is membered{
  function applyMeeting() address;
}

// meeting
contract Meeting is membered{

  function join();

  function setDecisionTime(uint _duration) onlyManager;

  function suggestAttr(uint _period, uint _times) onlyMember;

  function getMsg() (uint, string, address) onlyMember; // -x: x's auction; +x: x's offer; 0: voting for string

  function offer(uint _money) onlyMember;

  function withdraw() onlyMember; //must pulled by user, cannot push
}
