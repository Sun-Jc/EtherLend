pragma solidity ^0.4.2;

import "Traits.sol";
import "SmallMeeting.sol";

contract SmallEthLendService is EthLendService{
  //event NewMeeting(address newContract, address manager);
  event Debug();

  function applyMeeting(){
    //var r = new SmallMeeting(msg.sender);
    //NewMeeting(0x00000000000, msg.sender);
    Debug();
  }

  function destroyOneMeeting(address _meeting, address _receiver) onlyOwner{
    if(!isAMember(_meeting)){
      throw;
    }
    Meeting(_meeting).end(_receiver);
  }
}
