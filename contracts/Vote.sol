import "traits"

// voting, must be called by membered. vote one by one, total is fixed
contract smallVote is Vote{

  uint count;

  address superVoter;

  bool svVote;

  Timer timer;

  function smallVote(address _sv, bool _defaultSV, Timer _timer){
    count = 0;
    superVoter = _sv;
    svVote = _defaultSV;
  }

  function voteFor(bool _vote){
    if ( !membered(owner).isMember(msg.sender) ){ // only onwer's member can vote
      throw;
    }
    count = count + 1;
  }

  function countVote() int onlyOwner{
    return count;
  }

  function endVote(){
    if(msg.sender != timer){
      throw;
    }
    okayToEnd = true;  
  }

}
