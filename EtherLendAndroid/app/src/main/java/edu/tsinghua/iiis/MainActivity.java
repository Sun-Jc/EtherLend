package edu.tsinghua.iiis;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;

public class MainActivity extends Activity implements MyAdapter.Clickable, Updatable {

    private final int numOfPages = 9;

    private ChooseAccount accountsChoosing;
    ChoosingMeeting meetingChoosing;

    AccountModel model = new AccountModel();

    private Fragment currentPage = accountsChoosing;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        registerPages();

       // Log.d(TAG,AccountModel.readFile("x",getBaseContext()));
        getAccounts1();
    }

    void getAccounts1(){
        accountsChoosing.setCallback(this);
        showPage(accountsChoosing);
    }

    void getMeetings2(){
        showPage(meetingChoosing);

    }

    void getaccounts(){
        model.loadService(this);
        //model.loadAccounts(this);
    }

    void getmeetings(){
        model.loadMeetings(this);
    }

    private void showPage(Fragment i){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainContainer, i);
        ft.commit();
        getFragmentManager().executePendingTransactions();
    }

    private void registerPages(){
        accountsChoosing = new ChooseAccount();
        meetingChoosing = new ChoosingMeeting();
    }


    @Override
    public void updateService(String service) {
        accountsChoosing.changeService("service: "+service);
    }

    @Override
    public void updateAccounts(String service, String[] accounts) {
        MyAdapter adpter = new MyAdapter(accounts,1,this);
        accountsChoosing.changeService("service: "+service);
        accountsChoosing.mRecyclerView.setAdapter(adpter);
    }

    @Override
    public void updateMeetings(String service, String address, long balance, String[] meetings, boolean[] isManager) {
        meetingChoosing.serviceAddr.setText("service: "+service);
        meetingChoosing.accountAddr.setText("account: "+address);
        meetingChoosing.balance.setText("balance: "+balance);
        MyAdapter adpter = new MyAdapter(meetings,2,this);
        meetingChoosing.mRecyclerView.setAdapter(adpter);
    }

    @Override
    public void updateSingle(String service, String address, long balance, String meeting, boolean isManager, long startTimes, long auctionVoteDur, long numOfMembers, long fristAuctionTime, long base, long period, int whenBorrow, long[] interests, long toEarns, long nextddl, int whatTodo, boolean changed) {

    }

    @Override
    public void message(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void accountChosen(int which) {
        model.chooseAccount(which);
        getMeetings2();
    }

    @Override
    public void meetingChosen(int which) {

    }
}