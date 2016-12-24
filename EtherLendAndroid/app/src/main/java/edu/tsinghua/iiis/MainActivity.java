package edu.tsinghua.iiis;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;

public class MainActivity extends Activity implements Updatable, SimpleScannerActivity.qrNeeded {

    private ChooseAccount accountsChoosing;
    ChoosingMeeting meetingChoosing;
    ManageMeeting meetingManage;

    public AccountModel model = new AccountModel();

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
        meetingChoosing.setCallback(this);
        showPage(meetingChoosing);
    }

    void manageMeeting3(){
        meetingManage.setCallback(this);
        showPage(meetingManage);
    }

    private void showPage(Fragment i){
        currentPage = i;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainContainer, i);
        ft.commit();
        getFragmentManager().executePendingTransactions();
    }

    private void registerPages(){
        accountsChoosing = new ChooseAccount();
        meetingChoosing = new ChoosingMeeting();
        meetingManage = new ManageMeeting();
    }


    @Override
    public void updateService(String service) {
        accountsChoosing.changeService("service: "+service);
    }

    @Override
    public void updateAccounts(String service, String[] accounts) {
        MyAdapter adpter = new MyAdapter(accounts);
        accountsChoosing.changeService("service: "+service);
        accountsChoosing.setAdapter(adpter,getBaseContext());
    }

    @Override
    public void updateMeetings(String service, String address, long balance, String[] meetings, boolean[] isManager) {
        meetingChoosing.setServiceAccountBalance(service,address,balance);
        MyAdapter adpter = new MyAdapter(meetings);
        meetingChoosing.setAdapter(adpter,getBaseContext());
    }

    @Override
    public void updateSingle(String service, String address, long balance, String meeting, boolean isManager, long startTimes, long auctionVoteDur, long numOfMembers, long fristAuctionTime, long base, long period, int whenBorrow, long[] interests, long toEarns, long nextddl, int whatTodo, boolean changed, int stage) {
        if(currentPage == meetingChoosing){
            if(isManager){
                manageMeeting3();
            }
        }else if(currentPage == meetingManage){
            meetingManage.setServiceAccountMeetingStartTimeNextTimeStage(service,address,meeting,startTimes,nextddl,stage);
            if(stage>0){
                meetingManage.setted();
            }
        }
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
    public void membersGot(String[] members) {
        MyAdapter adpter = new MyAdapter(members);
        meetingManage.setAdapter(adpter,getBaseContext());
    }

    public void accountChosen(int which) {
        model.chooseAccount(which);
        getMeetings2();
    }

    public void meetingChosen(int which) {
        model.chooseMeeting(which);
        model.checkMeeting(this);
    }


    public void startScanner() {
        Intent startScanner = new Intent(this, SimpleScannerActivity.class);
        startActivity(startScanner);
    }

    @Override
    public void qrGot(String resultText) {
        if(currentPage == meetingManage){
            model.accept(resultText,this);
        }
    }
}