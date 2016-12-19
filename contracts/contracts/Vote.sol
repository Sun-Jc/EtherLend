pragma solidity ^0.4.2;

import "Traits.sol";

// voting, must be called by membered. vote one by one, total is fixed
contract SmallVote is Vote{

  uint AYEcount = 0;

  uint NAYcount = 0;

  mapping(address => bool) voted;

  function voteFor(address _agent, bool _vote) onlyOwner returns(bool){
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

  function countVote() onlyOwner returns (uint aye,uint nay){
    return (AYEcount, NAYcount);
  }

}
