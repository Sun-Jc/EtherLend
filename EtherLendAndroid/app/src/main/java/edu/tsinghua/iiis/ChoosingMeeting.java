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


public class ChoosingMeeting extends Fragment {

    private View mRootView;

    public TextView serviceAddr;
    public TextView accountAddr;
    public TextView balance;
    public RecyclerView mRecyclerView;
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
        return mRootView;
    }
}



