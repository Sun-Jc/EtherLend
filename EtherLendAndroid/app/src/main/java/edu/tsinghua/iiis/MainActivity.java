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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.rey.material.app.Dialog;


import java.io.FileOutputStream;
import java.io.IOException;

import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;

public class MainActivity extends Activity implements Updatable, SimpleScannerActivity.qrNeeded {

    private static final int WIDTH = 500;

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
        displayQR();
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

    public void accountChosen(final int which) {
        (new Runnable() {
            @Override
            public void run() {
                model.chooseAccount(which);
                getMeetings2();
            }
        }).run();
    }

    public void check(){
        model.checkMeeting(this);
    }

    public void meetingChosen(final int which) {
        (new Runnable() {
            @Override
            public void run() {
                model.chooseMeeting(which);
                check();
            }
        }).run();
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
            (new Runnable() {
                @Override
                public void run() {
                    accept(resultText);
                }
            }).run();
        }
    }
}