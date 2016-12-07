/**
Timer can call another contract's specific method later
Time unit: second

Usage: user schedule a calling by register();
      server trigger the calling by polling on trigger();
      the method to be called should not contain args?
*/



// concrete timer
contract SmallTimer is owned, Timer{

  struct Request {
    uint after;
    uint before;
    address receiver;
    uint gasLimit;
    string method;
  }

  Request[] pendingRequest;
  uint finished;

  // this service will at least charge charge*gasLimit of ether of the user
  uint charge;

  function SmallTimer(address _owner, uint _charge) payable{
    owner = _owner;
    charge = _charge;
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

  function register payable(uint _after, uint _before, string _method, address _address, uint _gasLimit){
      if( tx.gasprice * gasLimit * charge < msg.value){
        // not enough fee
        throw;
      }

      pendingRequest.push(Request{
        after: _after,
        before: _before,
        receiver: _address,
        gasLimit: _gasLimit,
        method: _method
        });
  }

  function trigger(){
    if (pendingRequest.length-1 > finished)
  }



}
