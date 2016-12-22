var service = SmallEthLendService.deployed();

var lastEvent;

var events = service.allEvents({fromBlock: 0, toBlock: 'latest'});
events.watch(function(error, result){
  console.log('event')
  if (!error){
    console.log(result);
    lastEvent = result;
  }else{
    console.log(error);
  }
});

service.applyMeeting({from:account, gas:3000000}).then(function(){console.log('finished');}).catch(function(e){console.log(e)});




var events1 = meeting.allEvents({fromBlock: 0, toBlock: 'latest'});
events1.watch(function(error, result){
  console.log('event')
  if (!error){
    console.log(result);
    lastEvent = result;
  }else{
    console.log(error);
  }
});




meeting.getsState({from:account, gas:3000000}).then(function(){console.log('finished');}).catch(function(e){console.log(e)});


meeting.setRecuritAndVoteAuctionTime(lastEvent.args.startTime.c[0] + 2000 , 100, {from:account}).then(function(){console.log('finished');}).catch(function(e){console.log(e)});

meeting.getState({from:account}).then( function(value) { console.log(value)} )
