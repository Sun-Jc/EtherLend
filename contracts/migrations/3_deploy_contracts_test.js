module.exports = function(deployer) {
  deployer.deploy(SmallVote);
  deployer.deploy(SmallAuction);
  deployer.deploy(SmallMeeting);
  deployer.deploy(SmallEthLendService);
}
