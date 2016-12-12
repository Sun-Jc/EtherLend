pragma solidity ^0.4.2;

import "Traits.sol";

// voting, must be called by membered. vote one by one, total is fixed
contract smallVote is Vote{

  uint count;

  address superVoter;

  bool svVote;

  uint endTime;

  bool ended = false;

  function smallVote(address _sv, bool _defaultSV, uint _endTime){
    count = 0;
    superVoter = _sv;
    svVote = _defaultSV;
    endTime = _endTime;
  }

  function voteFor(bool _vote){
    if ( !membered(owner).isAMember(msg.sender) || !ended ){ // only onwer's member can vote
      throw;
    }
    count = count + 1;
  }

  function countVote() onlyOwner returns (uint){
    return count;
  }

  function endVote(){
    if(now > endTime){
      ended = true;
    }
  }

}
