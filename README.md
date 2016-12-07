# EtherLend

#Smart Contracts

There are 7 kind of contracts (,some of which is abstract). The APIs between contract and users are roughly as follows:

1. EthLendService{
  applyAMeeting
}

2. Meeting{
  joinMeeting(name,id)
  voteJoin(event,n/a)
  offerAdvice(period, times)
  sendBid(interst)
  showBid(intest)
  offer(money)
  getMsg
}

3. SealedAuction

4. contract Vote

5. CreditRecorder

6. Timer{
  trigger
}

7. Insurance


#UI side: TBA
