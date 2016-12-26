package edu.tsinghua.iiis;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ChoosingMeeting extends Fragment {

    private View mRootView;

    private TextView accountAddr;
    private TextView balanceT;
    public void setServiceAccountBalance(final String service,final String address,final String balance){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                accountAddr.setText(ChooseAccount.format(address));
                balanceT.setText("$ "+balance +" wei");
            }
        });

    }
    private RecyclerView mRecyclerView;
    MainActivity callback;
    public void setCallback(MainActivity activity){
        callback = activity;
    }
    public void setAdapter(final MyAdapter adpter, final Context context){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutManager = new LinearLayoutManager(callback);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(adpter);
                mRecyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                callback.meetingChosen(position);
                            }
                        })
                );
            }
        });

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
        accountAddr = (TextView)mRootView.findViewById(R.id.ac);
        balanceT = (TextView)mRootView.findViewById(R.id.bal);

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.meetings);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton appBtn = (FloatingActionButton)mRootView.findViewById(R.id.apply);
        appBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                callback.model.applyMeeting(callback);
                                Looper.loop();
                            }
                        }).start();
                    }
                }

        );

        FloatingActionButton renewBtn = (FloatingActionButton)mRootView.findViewById(R.id.refresh);
        renewBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                callback.model.loadMeetings(callback);
                                Looper.loop();
                            }
                        }).start();
                    }
                }

        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                callback.model.loadMeetings(callback);
                Looper.loop();
            }
        }).start();

        return mRootView;
    }
}



