pragma solidity ^0.4.2;

import "Traits.sol";
/**
Timer can call another contract's specific method later
Time unit: second

Usage: user schedule a calling by register();
      server trigger the calling by polling on trigger();
      the method to be called should not contain args?
*/



// concrete timer
contract SmallTimer is Timer{

  uint createdTime;

  struct Request {
    uint After;
    uint Before;
    address Receiver;
    uint GasLimit;
    string Method;
  }

  mapping(int64 => Request) pendingQueue;
  int64 head;
  int64 next;

  // this service will at least charge charge*gasLimit of ether of the user
  uint charge;

  function SmallTimer(address _owner, uint _charge) payable{
    owner = _owner;
    charge = _charge;
    createdTime = now;
  }

  // owner withdraws money, unit is wei, if _amount is 0, withdraw all - 1 ether
  function withdraw(uint _amount) onlyOwner{
    var available = this.balance - 1 ether;
    if (_amount > available){
      throw;
    }
    if (_amount == 0){
      if (msg.sender.send(available)){
        throw;
      }
    }else{
      if (msg.sender.send(_amount)){
        throw;
      }
    }
  }

  // suicide
  function finish() onlyOwner{
    selfdestruct(msg.sender);
  }

  function askPrice() returns (uint,uint){
    return (charge,createdTime);
  }

  function register(uint _after, uint _before, string _method, address _address, uint _gasLimit) payable returns(bool ok){
      if( tx.gasprice * _gasLimit * charge < msg.value){
        // not enough fee
        throw;
      }

      mapping(int64=>uint) startTime;
      mapping(int64=>Request) pendings;
      int64 hd = head;
      int64 nt = next;
      Request memory req = Request({After:_after,Before:_before,Receiver:_address,GasLimit:_gasLimit,Method:_method});

      for(int64 i = hd; i < nt ; i++){
        pendings[i] = pendingQueue[i];
        startTime[i] = pendings[i].After;
      }

      //int64 obj = binarySearch(startTime, hd, nt, _after);

      uint time = _after;
      int64 a = hd;
      int64 b = nt;
      while(a < b){
        int64 mi = (a + b)/2;
        uint vm = startTime[mi];
        if( vm >= time ){
          b = mi;
        }else{
          a = mi;
        }
      }

      int64 obj = mi;

      pendingQueue[obj] = req;
      if(obj - hd + 1 >= nt - obj){
        next ++;
        for(int64 ii = obj; ii < nt; ii++){
          pendingQueue[ii+1] = pendings[ii];
        }
      }else{
        head --;
        for(int64 iii = obj; ii >= hd; iii--){
          pendingQueue[iii-1] = pendings[iii];
        }
      }
      return true;
  }

  /*function binarySearch( mapping(int64=>uint) startTime, int64 begin, int64 next, uint time) private returns(int64){
    int64 a = begin;
    int64 b = next;
    while(a < b){
      int64 mi = (a + b)/2;
      uint vm = startTime[mi];
      if( vm >= time ){
        b = mi;
      }else{
        a = mi;
      }
    }
    return mi;
  }*/

  function trigger(){
    if(head>=next){
      throw;
    }else{
      uint nowTime = now;
      Request req = pendingQueue[head];
      while(req.After <= nowTime){
        if(req.Before >= nowTime){
          if (! req.Receiver.call.value(0).gas(req.GasLimit)( bytes4(sha3(req.Method)))){
            throw;
          }
          head++;
        }
      }
    }
  }
}
