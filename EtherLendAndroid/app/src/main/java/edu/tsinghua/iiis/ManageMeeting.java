package edu.tsinghua.iiis;

import android.app.Fragment;
import android.content.Context;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.rey.material.widget.FloatingActionButton;


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

    private Button check;

    private Button submit;

    public void setted(){
        howLongRec.setVisibility(View.INVISIBLE);
        howLongAuc.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);
    }

    public void setServiceAccountMeetingStartTimeNextTimeStage(
            String service,String address,String _meeting,
            long _starttime, long _nextTime, int _stage){
        serviceAddr.setText("service: "+service);
        accountAddr.setText("account: "+address);
        meeting.setText("meeting: " +_meeting);
        startTime.setText("start: "+ _starttime);
        nextTime.setText("next :" + _nextTime);
        stage.setText("stage: "+ _stage);
    }

    private RecyclerView mRecyclerView;
    MainActivity callback;
    public void setCallback(MainActivity activity){
        callback = activity;
    }
    public void setAdapter(MyAdapter adpter,Context context){
        mRecyclerView.setAdapter(adpter);
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
                final long auctime = Long.parseLong(howLongAuc.getText().toString());
                final long recrutime = Long.parseLong(howLongRec.getText().toString());
                (new Runnable() {
                    @Override
                    public void run() {
                        callback.model.set(recrutime,auctime,callback);
                    }
                }).run();

            }
        });

        check = (Button)mRootView.findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Runnable() {
                    @Override
                    public void run() {
                        callback.model.getMembers(callback);
                        callback.check();
                    }
                }).run();
            }
        });

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.members);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton appBtn = (FloatingActionButton)mRootView.findViewById(R.id.accept);
        appBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.startScanner();
                    }
                }

        );

        (new Runnable() {
            @Override
            public void run() {
                callback.model.getMembers(callback);
                callback.model.checkMeeting(callback);
            }
        }).run();



        return mRootView;
    }
}



