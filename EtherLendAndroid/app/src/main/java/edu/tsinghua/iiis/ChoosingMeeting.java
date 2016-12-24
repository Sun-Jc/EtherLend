package edu.tsinghua.iiis;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.rey.material.widget.FloatingActionButton;


public class ChoosingMeeting extends Fragment {

    private View mRootView;

    private TextView serviceAddr;
    private TextView accountAddr;
    private TextView balance;
    public void setServiceAccountBalance(String service,String address,long balance){
        serviceAddr.setText("service: "+service);
        accountAddr.setText("account: "+address);
        this.balance.setText("balance: "+balance);
    }
    private RecyclerView mRecyclerView;
    MainActivity callback;
    public void setCallback(MainActivity activity){
        callback = activity;
    }
    public void setAdapter(MyAdapter adpter,Context context){
        mRecyclerView.setAdapter(adpter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        callback.meetingChosen(position);
                    }
                })
        );
    }
    private RecyclerView.LayoutManager mLayoutManager;


    public ChoosingMeeting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.meeting_choose, container, false);
        serviceAddr = (TextView)mRootView.findViewById(R.id.service);
        accountAddr = (TextView)mRootView.findViewById(R.id.ac);
        balance = (TextView)mRootView.findViewById(R.id.bal);

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.meetings);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton appBtn = (FloatingActionButton)mRootView.findViewById(R.id.apply);
        appBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.model.applyMeeting(callback);
                    }
                }

        );

        (new Runnable() {

            @Override
            public void run() {
                callback.model.loadMeetings(callback);
            }
        }).run();


        return mRootView;
    }
}


