package edu.tsinghua.iiis;

import android.app.Fragment;
import android.content.Context;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;

import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;


public class ManageMeeting extends Fragment {

    private View mRootView;

    private TextView serviceAddr;
    private TextView accountAddr;
    private TextView meeting;
    private TextView startTime;
    private TextView nextTime;
    private TextView stage;
    private EditText howLongRec;
    private EditText howLongAuc;
    private FloatingActionButton check;
    FloatingActionButton appBtn;
    private Button    submit;

    public void notsetted(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceAddr.setVisibility(View.INVISIBLE);;
                accountAddr.setVisibility(View.VISIBLE);;
                meeting.setVisibility(View.VISIBLE);;
                startTime.setVisibility(View.VISIBLE);
                nextTime.setVisibility(View.INVISIBLE);;
                stage.setVisibility(View.INVISIBLE);;
                howLongRec.setVisibility(View.VISIBLE);;
                howLongAuc.setVisibility(View.VISIBLE);;
                check.setVisibility(View.VISIBLE);;
                appBtn.setVisibility(View.INVISIBLE);;
                submit.setVisibility(View.VISIBLE);;
                mRecyclerView.setVisibility(View.INVISIBLE);
                Log.d(TAG,"notsetted");
            }
        });
    }

    public void setted(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.VISIBLE);
                serviceAddr.setVisibility(View.INVISIBLE);;
                accountAddr.setVisibility(View.VISIBLE);;
                meeting.setVisibility(View.VISIBLE);;
                startTime.setVisibility(View.VISIBLE);
                nextTime.setVisibility(View.VISIBLE);;
                stage.setVisibility(View.VISIBLE);;
                howLongRec.setVisibility(View.INVISIBLE);
                howLongAuc.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.VISIBLE);;
                appBtn.setVisibility(View.VISIBLE);;
                submit.setVisibility(View.INVISIBLE);;
                Log.d(TAG,"setted");
            }
        });

    }

    public void beginauction(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceAddr.setVisibility(View.INVISIBLE);;
                accountAddr.setVisibility(View.VISIBLE);;
                meeting.setVisibility(View.VISIBLE);;
                startTime.setVisibility(View.VISIBLE);
                nextTime.setVisibility(View.VISIBLE);;
                stage.setVisibility(View.VISIBLE);;
                howLongRec.setVisibility(View.INVISIBLE);;
                howLongAuc.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.VISIBLE);;
                appBtn.setVisibility(View.INVISIBLE);;
                submit.setVisibility(View.INVISIBLE);;
                mRecyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG,"beigin");
            }
        });

    }

    public void setServiceAccountMeetingStartTimeNextTimeStage(
            final String service, final String address,final String _meeting,
            final BigInteger _starttime, final String _nextTime, final int _stage){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceAddr.setText("service: "+service);
                accountAddr.setText(address);
                meeting.setText(_meeting);
                startTime.setText("This meeting starts at "+ _starttime + " s");
                nextTime.setText("The next time is " + _nextTime + "s");
                stage.setText("stage: "+ _stage);
            }
        });

    }

    private RecyclerView mRecyclerView;
    MainActivity callback;
    public void setCallback(MainActivity activity){
        callback = activity;
    }

    public void setAdapter(final MyAdapter adpter,Context context){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(adpter);
            }
        });

    }
    private RecyclerView.LayoutManager mLayoutManager;


    public ManageMeeting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_manage_meeting, container, false);

        serviceAddr = (TextView)mRootView.findViewById(R.id.service2);
        accountAddr = (TextView)mRootView.findViewById(R.id.ac2);
        meeting = (TextView)mRootView.findViewById(R.id.meeting);
        startTime = (TextView)mRootView.findViewById(R.id.startTime);
        nextTime = (TextView)mRootView.findViewById(R.id.nextTime);
        stage = (TextView)mRootView.findViewById(R.id.stage);


        howLongRec = (EditText)mRootView.findViewById(R.id.recend);
        howLongAuc = (EditText)mRootView.findViewById(R.id.auctime);


        submit = (Button)mRootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BigInteger auctime = new BigInteger(howLongAuc.getText().toString());
                final BigInteger recrutime = new BigInteger(howLongRec.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        callback.model.set(recrutime,auctime,callback);
                        Looper.loop();
                    }
                }).start();

            }
        });

        check = (FloatingActionButton) mRootView.findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        callback.model.getMembers(callback);
                        callback.check();
                        Looper.loop();
                    }
                }).start();
            }
        });

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.members);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        appBtn = (FloatingActionButton)mRootView.findViewById(R.id.accept);
        appBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.startScanner();
                    }
                }

        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                callback.model.getMembers(callback);
                callback.model.checkMeeting(callback);
                Looper.loop();
            }
        }).start();


        nextTime.setVisibility(View.INVISIBLE);
        stage.setVisibility(View.INVISIBLE);
        howLongRec.setVisibility(View.INVISIBLE);;
        howLongAuc.setVisibility(View.INVISIBLE);;
        appBtn.setVisibility(View.INVISIBLE);;
        submit.setVisibility(View.INVISIBLE);;


        return mRootView;
    }
}



