package edu.tsinghua.iiis;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.rey.material.widget.FloatingActionButton;



/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAccount extends Fragment{

    private View mRootView;

    private TextView serviceAddr;

    public void changeService(final String str){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceAddr.setText(str);
            }
        });

    }
    private RecyclerView mRecyclerView;
    public void setAdapter(final MyAdapter adpter,final Context context){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayoutManager = new LinearLayoutManager(callback);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(adpter);
                mRecyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                callback.accountChosen(position);
                            }
                        })
                );
            }
        });
    }
    private RecyclerView.LayoutManager mLayoutManager;

    public ChooseAccount() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_get_account_page1, container, false);
        serviceAddr = (TextView)mRootView.findViewById(R.id.service1);

        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.accounts);
        // use a linear layout manager

        Log.d("sunjc",serviceAddr.getText().toString());


        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.model.loadService(callback);
                callback.model.loadAccounts(callback);
            }
        }).start();

        return mRootView;
    }

    MainActivity callback;

    public void setCallback(MainActivity activity){
        callback = activity;
    }
}




