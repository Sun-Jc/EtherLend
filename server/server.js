//Lets require/import the HTTP module
var http = require('http');
var HttpDispatcher = require('httpdispatcher');
var dispatcher = new HttpDispatcher();

var Web3 = require('web3');
var web3 = new Web3();

var provider = 'http://localhost:8545'
web3.setProvider(new web3.providers.HttpProvider(provider));

//Lets define a port we want to listen to
const PORT=8085; 

//We need a function which handles requests and send response
function handleRequest(request, response){
    //response.end('It Works!! Path Hit: ' + request.url);
    try {
        //log the request on console
        console.log(request.url);
        //Disptach
        dispatcher.dispatch(request, response);
    } catch(err) {
        console.log(err);
    }
}

//For all your static (js/css/images/etc.) set the directory name (relative path).
dispatcher.setStatic('resources');

//A sample GET request    
dispatcher.onGet("/page1", function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('Page One');
});    

dispatcher.onGet("/balance", function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    var account = web3.eth.accounts[0];
    var balance = web3.eth.getBalance(account);
    res.end(JSON.stringify({'balance': balance.toString()}));
});    

//A sample POST request
dispatcher.onPost("/post1", function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('Got Post Data');
});

//Create a server
var server = http.createServer(handleRequest);

//Lets start our server
server.listen(PORT, function(){
    //Callback triggered when server is successfully listening. Hurray!
    console.log("Server listening on: http://localhost:%s", PORT);
});
