package edu.tsinghua.iiis;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.rey.material.app.Dialog;
import org.json.JSONObject;


import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static edu.tsinghua.iiis.AccountModel.BID;
import static edu.tsinghua.iiis.AccountModel.SUGGEST;
import static edu.tsinghua.iiis.AccountModel.VOTE;
import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;

public class MainActivity extends Activity implements Updatable, SimpleScannerActivity.qrNeeded {

    private static final int WIDTH = 500;

    private ChooseAccount accountsChoosing;
    ChoosingMeeting meetingChoosing;
    ManageMeeting meetingManage;
    PlayMeeting meetingPlay;

    public AccountModel model;

    private Fragment currentPage = accountsChoosing;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        model = new AccountModel(this);

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

    void playMeetings3(){
        meetingPlay.setCallback(this);
        showPage(meetingPlay);
    }

    private void showPage(Fragment i){
        currentPage = i;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainContainer, i);
        ft.commit();
    }

    private void registerPages(){
        accountsChoosing = new ChooseAccount();
        meetingChoosing = new ChoosingMeeting();
        meetingManage = new ManageMeeting();
        meetingPlay = new PlayMeeting();
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
    public void updateMeetings(String service, String address, BigInteger balance, String[] meetings, boolean[] isManager) {
        meetingChoosing.setServiceAccountBalance(service,address,balance.toString());
        MyAdapter adapter = new MyAdapter(meetings);
        meetingChoosing.setAdapter(adapter,getBaseContext());
    }

    @Override
    public void updateSingle(String service, String address, BigInteger balance, String meeting, boolean isManager, BigInteger startTimes, BigInteger auctionVoteDur, BigInteger numOfMembers, BigInteger fristAuctionTime, BigInteger base, BigInteger period, int whenBorrow, BigInteger[] interests, BigInteger toEarns, BigInteger nextddl, int whatTodo, boolean isMember, int stage) {
        if(currentPage == meetingChoosing){
            if(isManager){
                manageMeeting3();
            }else{
                playMeetings3();
            }
        }else if(currentPage == meetingManage){
            meetingManage.setServiceAccountMeetingStartTimeNextTimeStage(service,address,meeting,startTimes,nextddl.toString(),stage);
            if(stage>0){
                meetingManage.setted();
            }
        }else if(currentPage == meetingPlay){
            meetingPlay.updateSerAccBalMeetNtMsg(service,address,balance,meeting,nextddl,"");
            if(stage==-1){
                meetingPlay.toJoin();
            }else if(stage==0 && whatTodo == VOTE){
                meetingPlay.toVote();
            }else if(stage == 0 && whatTodo == SUGGEST){
              meetingPlay.toSuggest();
            }else if(stage>1 && whatTodo == BID){
                meetingPlay.toBid();
            }else if(stage > -1 &&!isMember){
                meetingPlay.NotJoinable();
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

    public void accountChosen(final int which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                model.chooseAccount(which);
                getMeetings2();
            }
        }).start();
    }

    public void check(){
        model.checkMeeting(this);
    }

    public void meetingChosen(final int which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                model.chooseMeeting(which);
                check();
            }
        }).start();
    }

    public void displayQR(){
        Dialog settingsDialog = new Dialog(this);
        //settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = getLayoutInflater().inflate(R.layout.image_layout, null);
        final ImageView img = (ImageView) v.findViewById(R.id.img);
        settingsDialog.setContentView(v);
        settingsDialog.show();
        try {
            Bitmap b = encodeAsBitmap(model.who());
            img.setImageBitmap(b);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.cardview_light_background);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    } /// end of this method



    public void startScanner() {
        SimpleScannerActivity.setCallback(this);
        Intent startScanner = new Intent(this, SimpleScannerActivity.class);
        startActivity(startScanner);
    }

    public void accept(String resultText){
        model.accept(resultText,this);
    }

    @Override
    public void qrGot(final String resultText) {
        if(currentPage == meetingManage){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    accept(resultText);
                }
            }).start();
        }
    }
}