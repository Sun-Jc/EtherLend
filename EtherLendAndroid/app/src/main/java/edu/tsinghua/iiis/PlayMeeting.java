package edu.tsinghua.iiis;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayMeeting extends Fragment {

    private View mRootView;

    TextView serviceAddr;
    TextView accountAddr;
    TextView balance;
    TextView meeting;
    TextView nextTime;
    TextView msg;

    EditText selfIntro;
    EditText period;
    EditText base;

    Button suggest;
    Button join;
    Button bid;
    Button reveal;
    Button check;
    Button vote;

    MainActivity callback;
    public void setCallback(MainActivity c){
        callback = c;
    }


    public PlayMeeting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_manage_meeting, container, false);

        serviceAddr = (TextView) mRootView.findViewById(R.id.ser);
        accountAddr= (TextView) mRootView.findViewById(R.id.acc);
        balance= (TextView) mRootView.findViewById(R.id.balance);
        meeting= (TextView) mRootView.findViewById(R.id.m);
        nextTime= (TextView) mRootView.findViewById(R.id.nexttime);
        msg= (TextView) mRootView.findViewById(R.id.msg);

        selfIntro = (EditText)mRootView.findViewById(R.id.intro);
        period= (EditText)mRootView.findViewById(R.id.period);
        base= (EditText)mRootView.findViewById(R.id.base);

        suggest = (Button) mRootView.findViewById(R.id.suggest);
        join=(Button) mRootView.findViewById(R.id.join);
        bid=(Button) mRootView.findViewById(R.id.bid);
        reveal=(Button) mRootView.findViewById(R.id.reveal);
        check=(Button) mRootView.findViewById(R.id.chk);
        vote=(Button) mRootView.findViewById(R.id.vote);

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long p = Long.parseLong(period.getText().toString());
                final long b = Long.parseLong(base.getText().toString());
                (new Runnable() {
                    @Override
                    public void run() {
                        callback.model.suggest(p,b,callback);
                        callback.model.checkMeeting(callback);
                    }
                }).run();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String selfintro = selfIntro.getText().toString();
                (new Runnable() {
                    @Override
                    public void run() {
                        callback.model.joinMeeting(selfintro,callback);
                        callback.check();
                        callback.message("Please show this code to your manager");
                        callback.displayQR();
                    }
                }).run();
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Runnable() {
                    @Override
                    public void run() {
                        callback.model.vote(callback);
                    }
                }.run();
            }
        });

        bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long amount = Long.parseLong(base.getText().toString());
                new Runnable() {
                    @Override
                    public void run() {
                        callback.model.bid(amount,callback);
                    }
                }.run();
            }
        });

        reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long amount = Long.parseLong(base.getText().toString());
                new Runnable() {
                    @Override
                    public void run() {
                        callback.model.reveal(amount,callback);
                    }
                }.run();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Runnable() {
                    @Override
                    public void run() {
                        callback.check();
                    }
                }.run();
            }
        });




        return mRootView;
    }

}
