pragma solidity ^0.4.2;

import "Traits.sol";

// voting, must be called by membered. vote one by one, total is fixed
contract SmallVote is Vote{

  uint AYEcount = 0;

  uint NAYcount = 0;

  mapping(address => bool) voted;

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

  function voteFor(address _agent, bool _vote) onlyOwner{
    if(now > endTime){
      ended = true;
    }

    if(ended){
      throw;
    }
    if(voted[_agent]){
      throw;
    }
    voted[_agent] = true;
    if (_vote){
      AYEcount += 1;
    }else{
      NAYcount += 1;
    }
    return true;
  }

  function countVote() onlyOwner returns (bool ended, uint aye,uint nay, bool sv){
    return (ended, AYEcount, NAYcount, svVote);
  }

}
