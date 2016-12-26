package edu.tsinghua.iiis;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Vector;


/**
 * Created by SunJc on 24/12/16.
 */
public class AccountModel{

    private static final String accountFile = "accounts";
    static final String serviceFile = "service";
    private static final String TAG = "SunjcDEbug";


    // what to do
    public static final int SET = 1;
    public static final int WAITtoACCEPTorJOIN = 2;
    public static final int WAITtoBeACCEPTED = 4;
    public static final int SUGGEST = 5;
    public static final int VOTE = 6;
    public static final int BID = 7;
    public static final int PUSH = 9;

    private String serviceAddr ="";

    private String[] accounts;

    private String[] meetings;
    private boolean[] isManager;

    private int whichAccount = -1;
    private int whichMeeting = -1;

    private BigInteger startTimes;
    private int stage;
    private BigInteger auctionVoteDur;
    private BigInteger numOfMembers;
    private BigInteger firstAuctionTime;
    private BigInteger base;
    private BigInteger period;
    private int whenBorrow; // default 0 for this not borrowed
    private BigInteger[] interests;
    private BigInteger toEarns;
    private BigInteger nextddl;
    private int whatTodo;

    private boolean isMember;

    private String msg = "";

    /*public static void writeFile(String str, String fileName, Context context){
        try{
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName, Context context){
        StringBuffer bu = new StringBuffer();
        try{
            // Open the file that is the first
            // command line parameter
            //if((context.fileList(),;
            FileInputStream fstream = context.openFileInput(fileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = br.readLine()) != null) {
                bu.append(line);
            }
            in.close();
        }catch (Exception e){//Catch exception if any
            e.printStackTrace();
            return "";
        }
        return bu.toString();
    }

    private static String encodeList(String[] list){
        String r = "";
        try {
            JSONArray jArray = new JSONArray();

            for(int i = 0; i < list.length; i++) {
                jArray.put(list[i]);
            }
            r = jArray.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    private static String[] parseList(String str){
        Vector<String> list = new Vector<>();
        try {
            JSONArray jArray = new JSONArray(str);
            for (int i=0; i < jArray.length(); i++) {
                try {
                    list.add( jArray.getString(i) ) ;
                } catch (JSONException e) {
                    // Oops
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list.toArray(new String[list.size()]);
    }

    private static boolean stored(String what, String where, Context c){
        String[] all = parseList(readFile(where,c));
        for(String s: all){
            if(s.equals(what)){
                return true;
            }
        }
        return false;
    }

    private static String[] append(String[] list, String app){
        Vector<String> v = new Vector<>();
        for (int i = 0; i < list.length; i++) {
            v.add(list[i]);
        }
        v.add(app);
        return v.toArray(new String[v.size()]);
    }*/
    NetComm netComm;

    public String BASEURL = "http://taoli.tsinghuax.org:8085/";

    String url(String route){
        return BASEURL+route;
    }

    AccountModel(MainActivity callback){
        netComm = new NetComm(callback);
    }

    private String parseString(JSONObject j,String key){
        String res ="";
        try {
            res =  j.getString(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private String[] parseArray(JSONObject j, String key){
        Vector<String> res = new Vector<>();
        try {
            JSONArray ja =  j.getJSONArray(key);
            for (int i = 0; i < ja.length(); i++) {
                res.add(ja.getString(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res.toArray(new String[res.size()]);
    }

    private void _getService(){
        JSONObject jsonObject = netComm.getJSON(url("getService"));
        this.serviceAddr = parseString(jsonObject,"deployedAddress");
    }

    private void _getAccounts(){
        JSONObject jsonObject = netComm.getJSON(url("getAccounts"));
        this.accounts = parseArray(jsonObject,"accounts");

        /*this.accounts =  new String[]{
                "0x1db0a93276a3819cec2b37a0e010e517465d7d68",
                "0x54399b39acf443788351abb3a98606219e697bf6",
                "0xe4a5ab5a8e89bc9f34de5bd21bc51a11d5071dd2",
                "0xcb6d552cb12b67d77037b8ae5c5a967f7d967713"};*/
    }

    private BigInteger _getBalance(){
        JSONObject jsonObject = netComm.getJSON(url("getBalance/"+whichAccount));
        return new BigInteger(parseString(jsonObject,"balance"));
    }

    private void _join(String intro){
        netComm.getJSON(url("join/"+meetings[whichMeeting]+"/"+whichAccount));
    }

    private void _getMeetings(){
        //TODO
        /*if(this.meetings!=null && this.meetings.length>0)
            return;*/
        JSONObject jsonObject = netComm.getJSON(url("getMeetings"));
        Vector<String> ms = new Vector<>();
        Iterator<?> it = jsonObject.keys();

        while(it.hasNext())
            ms.add((String) it.next());

        this.meetings = ms.toArray(new String[ms.size()]);
        this.isManager = new boolean[meetings.length];
    }

    private void _applyMeeting(){

        netComm.getJSON(url("applyMeeting/"+whichAccount));


        /*this.meetings = new String[]{
                "0x883eb0952dc97dd1b62c98fa77363b8af597556a",
                "0x18a7e513a0d114062489dcaa260c3256e8d424ed",
                "0x7b39df9b93f3fad5dcea72bbde34f99c5445c807",
                "0x7b39df9b93f3fad5dcea72bbde34f99c5445cNEW"
        };*/
    }

    private void _updateMeeting() {

        //isManager = new boolean[meetings.length];
        isManager[whichMeeting] = false;
        startTimes = new BigInteger("-1");
        stage = -3;
        auctionVoteDur = new BigInteger("-1");
        numOfMembers = new BigInteger("0");
        firstAuctionTime = new BigInteger("-1");
        isMember = false;
        base = new BigInteger("-1");
        period = new BigInteger("-1");
        whenBorrow = 0; // default
        interests = new BigInteger[0];
        toEarns = new BigInteger("0");
        nextddl = new BigInteger("0");
        whatTodo = -1;

        JSONObject s = netComm.getJSON(url("check/" + meetings[whichMeeting] + "/" + whichAccount));


        JSONObject result = netComm.getJSON(url("getEvents/" + meetings[whichMeeting]));
        try {

            firstAuctionTime = new BigInteger(s.getJSONObject("state").getString("firstAuctionTime"));

            JSONArray events = result.getJSONArray("events");

            for (int i = 0; i < result.length(); i++) {
                JSONObject e = events.getJSONObject(i);
                String type = e.getString("event");
                JSONObject args = e.getJSONObject("args");

                if (type.equals("Established")) {
                    String manager = args.getString("manager");
                    this.isManager[whichMeeting] = manager.equals(accounts[whichAccount]);
                    this.startTimes = new BigInteger(args.getString("startTime"));
                    stage = -2;
                    whatTodo = SET;
                    msg = "Manager needs to decide before when should new members join in and how long will an auction or voting last.";
                } else if (type.equals("RecruitAndDecisionTimeSet")) {
                    this.auctionVoteDur = new BigInteger(args.getString("decisonTime"));
                    this.nextddl = new BigInteger(args.getString("endTimeRecruit"));
                    stage = -1;
                    whatTodo = WAITtoACCEPTorJOIN;
                    msg = "Members should join in before " + nextddl.toString() + " s";
                } else if (type.equals("JoinApplied")) {
                    if (args.getString("agent").equals(accounts[whichAccount])) {
                        whatTodo = WAITtoBeACCEPTED;
                        msg = "Your application has been submitted.";
                    }
                } else if (type.equals("NewMember")) {
                    numOfMembers.add(new BigInteger("1"));
                    stage = 0;
                    if (args.getString("who").equals(accounts[whichAccount])) {
                        isMember = true;
                        whatTodo = SUGGEST;
                        msg = "You are already a member";
                    }
                } else if (type.equals("Suggested")) {
                    stage = 0;
                    whatTodo = VOTE;
                    msg = "Please Vote for this suggestion: Our meeting will held every " +
                            args.getString("period") + " s and basic amount is " + args.getString("base") + " wei";
                } else if (type.equals("Voted")) {
                    stage = 0;
                    whatTodo = PUSH;
                    msg = "You have voted successfully";
                } else if (type.equals("FormSet")) {
                    base = new BigInteger(args.getString("base"));
                    period = new BigInteger(args.getString("period"));
                    stage = 1;
                    whatTodo = BID;
                } else if (type.equals("BeginAuction")) {
                    stage = Integer.parseInt(args.getString("round"));
                    whatTodo = BID;

                    nextddl = firstAuctionTime.add(
                            (period.add(auctionVoteDur)).multiply(new BigInteger(Integer.toString(stage - 1))).add(auctionVoteDur));
                    msg = "Please bid for round " + stage + " before " + nextddl + " s";

                } else if (type.equals("Bidded")) {
                    stage = Integer.parseInt(args.getString("stage"));
                    if (args.getString("agent").equals(accounts[whichAccount])) {
                        whatTodo = BID;
                        msg = "Please reveal for round " + stage + " before " + nextddl + " s";
                    }
                } else if (type.equals("Revealed")) {
                    stage = Integer.parseInt(args.getString("stage"));
                    if (args.getString("agent").equals(accounts[whichAccount])) {
                        whatTodo = PUSH;
                        msg = "Please push for result";
                    }
                } else if (type.equals("AuctionSuccess")) {
                    whatTodo = PUSH;
                    stage = Integer.parseInt(args.getString("stage"));
                    msg = args.getString("winner") + " won this auction, who will pay " + args.getString("interest") + " wei for others as interest";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void _set(BigInteger whenEndR, BigInteger howLongAuc){
        netComm.getJSON(url("set/"+meetings[whichMeeting]+"/"+whichAccount+"/"+whenEndR+"/"+howLongAuc));
    }

    private void _suggest(BigInteger period, BigInteger base){
        netComm.getJSON(url("suggest/"+meetings[whichMeeting]+"/"+whichAccount+"/"+period+"/"+base));
    }

    private void _vote(){
        netComm.getJSON(url("vote/"+meetings[whichMeeting]+"/"+whichAccount+"+/aye"));
    }

    private void _accept(String who){
        netComm.getJSON(url("accept/"+meetings[whichMeeting]+"/"+who+"/"+accounts[whichAccount]));
    }

    private void _bid(BigInteger howMuch){
        netComm.getJSON(url("bid/"+meetings[whichMeeting]+"/"+whichAccount+"/"+stage+"/"+howMuch+"/"+howMuch));
    }

    private void _reveal(BigInteger howMuch){
        netComm.getJSON(url("reaveal/"+meetings[whichMeeting]+"/"+whichAccount+"/"+stage+"/"+howMuch));
    }

    private String[] _getUsers(){
        return  new String[]{
                "0x1db0a93276a3819cec2b37a0e010e517465d7d68",
                "0x54399b39acf443788351abb3a98606219e697bf6",
                "0xe4a5ab5a8e89bc9f34de5bd21bc51a11d5071dd2",
                "0xcb6d552cb12b67d77037b8ae5c5a967f7d967713"};
    }


    public void loadService(Updatable obj){
        _getService();
        obj.updateService(this.serviceAddr);
    }

    public void loadAccounts(Updatable obj){
        if(serviceAddr.equals("") ){
            obj.message("no service, get a service first");
        }else{
            _getAccounts();
            obj.updateAccounts(serviceAddr,accounts);
        }
    }

    public void chooseAccount(int i){
        this.whichAccount = i;
        Log.d(TAG,"choose "+i);
    }


    public void loadMeetings(Updatable obj){
        if(whichAccount==-1 ){
            obj.message("no account, get an account first");
        }else{
            _getMeetings();
            obj.updateMeetings(
                    this.serviceAddr,this.accounts[whichAccount],
                    _getBalance(),this.meetings,isManager);
        }
    }

    public void applyMeeting(Updatable obj){
        _applyMeeting();
        obj.message("applied");
        loadMeetings(obj);
    }

    public void joinMeeting(String intro , Updatable obj){
        _join(intro);
        obj.message("joined");
    }

    public void accept(String agent, Updatable obj){
        _accept(agent);
        obj.message("accepted:"+agent);
    }

    public void chooseMeeting(int which){
        this.whichMeeting = which;
    }

    public void checkMeeting(Updatable obj){
        String addr = this.accounts[whichAccount];
        String maddr = this.meetings[whichMeeting];

        _updateMeeting();

        obj.updateSingle(this.serviceAddr,addr,_getBalance(),maddr,
            isManager[whichMeeting],
            startTimes,
            auctionVoteDur,
            numOfMembers,
                firstAuctionTime,
            base,
            period,
            whenBorrow, // default 0 for this not borrowed
            interests,
            toEarns,
            nextddl,
            whatTodo,isMember,stage,msg);
    }

    public void set(BigInteger whenEndR, BigInteger howLongAuc, Updatable obj){
        _set(whenEndR,howLongAuc);
        obj.message("set");
        checkMeeting(obj);
    }

    public void suggest(BigInteger period, BigInteger base, Updatable obj){
        _suggest(period,base);
        obj.message("suggest");
        checkMeeting(obj);
    }

    public void bid(BigInteger howMuch, Updatable obj){
        _bid(howMuch);
        obj.message("bid");
        checkMeeting(obj);
    }

    public void reveal(BigInteger howMuch, Updatable obj){
        _reveal(howMuch);
        obj.message("reveal");
        checkMeeting(obj);
    }

    public void getMembers(Updatable obj){
        obj.membersGot(_getUsers());
    }

    public String who(){
        return accounts[whichAccount];
    }

    public void vote(Updatable obj){
        _vote();
        obj.message("voted");
    }

    public String whoIsService(){
        return serviceAddr;
    }
}
