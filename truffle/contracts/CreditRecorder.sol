pragma solidity ^0.4.2;

import "Traits.sol";


contract SmallCreditRecorder is CreditRecorder{

  struct record{
    address agent;
    uint amount;
    address reporter;
    uint insuranceId;
  }

  mapping(bytes32 => record) records;

  uint recordNum = 0;

  function register(address _reporter) onlyOwner{
    addMember(_reporter);
  }

  function addBadRecord(address _agent,uint _amount, uint _insuranceId) onlyMember returns(bytes32 id){
    bytes32 hs = sha3(recordNum,msg.sender);
    recordNum ++;
    record memory r = record({agent:_agent,amount:_amount,reporter:msg.sender,insuranceId:_insuranceId});
    records[hs] = r;
    return hs;
  }

  function retriveRecord(bytes32 _recordId) returns (address _agent,uint _amount, address reporter, uint _insuranceId){
    record r = records[_recordId];
    address ag = r.agent;
    uint am = r.amount;
    address re = r.reporter;
    uint ins = r.insuranceId;
    return (ag,am,re,ins);
  }
}
