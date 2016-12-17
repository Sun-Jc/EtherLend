pragma solidity ^0.4.2;

import "Traits.sol";

// insurance company
contract SmallInsurance is Insurance{

  uint feeRate = 1000; // feeRate * fee = insurance amount

  struct Receipt{
    address holder;
    uint gurantee;
    bool payed;
  }

  Receipt[] receipts;

  CreditRecorder cr;

  function SmallInsurance(address _creditRecorder){
    cr = CreditRecorder(_creditRecorder);
  }


  function askPrice() onlyMember returns(uint charge){
    return feeRate;
  }

  function buy(uint _guarantee) payable onlyMember returns (uint id){
    if(msg.value * feeRate < _guarantee){
      // not enough fee
      throw;
    }
    Receipt memory r = Receipt({holder:msg.sender, gurantee: _guarantee,payed: false});
    receipts.push(r);
    return receipts.length;

  }

  function pay(bytes32 _recordId, address _agent, uint _amount, uint _insuranceId) onlyMember returns (bool ok){
    /* TODO:CHECK
    var () = cr.retriveRecord(_recordId)){
      throw;
    }*/

    Receipt r = receipts[_insuranceId];
    if(!r.payed){
      if(r.holder.send(r.gurantee)){
        return true;
      }
    }
    return false;
  }
}
