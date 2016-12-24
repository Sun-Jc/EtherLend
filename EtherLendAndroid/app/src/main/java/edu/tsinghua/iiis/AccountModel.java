package edu.tsinghua.iiis;

import android.content.Context;
import android.location.Address;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import com.google.android.gms.nearby.messages.internal.Update;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


/**
 * Created by SunJc on 24/12/16.
 */
public class AccountModel{

    private static final String accountFile = "accounts";
    static final String serviceFile = "service";
    private static final String TAG = "SunjcDEbug";

    private String serviceAddr ="";

    private String[] accounts;

    private String[] meetings;
    private boolean[] isManager;

    private int whichAccount = -1;
    private int whichMeeting = -1;

    private long startTimes;
    private int stage;
    private long auctionVoteDur;
    private long numOfMembers;
    private long fristAuctionTime;
    private long base;
    private long period;
    private int whenBorrow; // default 0 for this not borrowed
    private long[] interests;
    private long toEarns;
    private long nextddl;
    private int whatTodo;

    private boolean changed;

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


    private void _getService(){
        //TODO
        this.serviceAddr = "0xbebc628b9a0cc26a98b7fd8dc9a4f554c7eef02f";
    }

    private void _getAccounts(){
        // TODO
        this.accounts =  new String[]{
                "0x1db0a93276a3819cec2b37a0e010e517465d7d68",
                "0x54399b39acf443788351abb3a98606219e697bf6",
                "0xe4a5ab5a8e89bc9f34de5bd21bc51a11d5071dd2",
                "0xcb6d552cb12b67d77037b8ae5c5a967f7d967713"};
    }

    private long _getBalance(){
        // TODO
        return 0;
    }

    private void _join(String intro){
        //TODO
    }

    private void _getMeetings(){
        //TODO
        if(this.meetings!=null && this.meetings.length>0)
            return;
        this.meetings= new String[]{
                "0x883eb0952dc97dd1b62c98fa77363b8af597556a",
                "0x18a7e513a0d114062489dcaa260c3256e8d424ed",
                "0x7b39df9b93f3fad5dcea72bbde34f99c5445c807"
        };
        this.isManager = new boolean[meetings.length];
    }

    private void _applyMeeting(){
        this.meetings = new String[]{
                "0x883eb0952dc97dd1b62c98fa77363b8af597556a",
                "0x18a7e513a0d114062489dcaa260c3256e8d424ed",
                "0x7b39df9b93f3fad5dcea72bbde34f99c5445c807",
                "0x7b39df9b93f3fad5dcea72bbde34f99c5445cNEW"
        };
        //TODO
    }

    private void _updateMeeting(){
        //TODO
        if(stage != -1) {
            isManager[whichMeeting] = true;
            startTimes = 2016;


            auctionVoteDur = -1;
            numOfMembers = 0;
            fristAuctionTime = -1;
            base = -1;
            period = -1;
            whenBorrow = -1;
            interests = new long[0];
            toEarns = 0;
            nextddl = 10;
            whatTodo = 0;
            changed = false;
            stage = -2;
        }
    }

    private void _set(long whenEndR, long howLongAuc){
        //TODO
        nextddl = whenEndR;
        auctionVoteDur = howLongAuc;
        stage = -1;
    }

    private void _suggest(long period, long base){
        //TODO
    }

    private void _vote(){
        //TODO
    }

    private void _accept(String who){
        //TODO

    }

    private void _bid(long howMuch){
        //TODO
    }

    private void _reveal(long howMuch){
        //TODO
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
            fristAuctionTime,
            base,
            period,
            whenBorrow, // default 0 for this not borrowed
            interests,
            toEarns,
            nextddl,
            whatTodo,changed,stage);
    }

    public void set(long whenEndR, long howLongAuc, Updatable obj){
        _set(whenEndR,howLongAuc);
        obj.message("set");
        checkMeeting(obj);
    }

    public void suggest(long period, long base, Updatable obj){
        _suggest(period,base);
        obj.message("suggest");
        checkMeeting(obj);
    }

    public void bid(long howMuch, Updatable obj){
        _bid(howMuch);
        obj.message("bid");
        checkMeeting(obj);
    }

    public void reveal(long howMuch, Updatable obj){
        _reveal(howMuch);
        obj.message("reveal");
        checkMeeting(obj);
    }

    public void getMembers(Updatable obj){
        obj.membersGot(_getUsers());
    }

    public String who(){
        //return accounts[whichAccount];
        return "0xe4a5ab5a8e89bc9f34de5bd21bc51a11d5071dd2";
    }

    public void vote(Updatable obj){
        _vote();
        obj.message("voted");
    }
}
