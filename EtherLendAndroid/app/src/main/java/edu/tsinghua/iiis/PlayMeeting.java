package edu.tsinghua.iiis;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;


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

    Button   suggest;
    Button   join;
    Button   bid;
    Button   reveal;
    Button   check;
    Button   vote;

    public void updateSerAccBalMeetNtMsg(
            final String serviceAddr,
            final String accountAddr,
            final BigInteger balance,
            final String meeting,
            final BigInteger nextTime,
            final String msg
    ){
        final PlayMeeting th = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                th.serviceAddr.setText("service: "+serviceAddr);
                th.accountAddr.setText("account: "+accountAddr);
                th.balance.setText("balance: "+balance);
                th.meeting.setText("meeting: "+meeting);
                th.nextTime.setText("Bid before "+nextTime);
                if(!msg.equals(""))
                    th.msg.setText(msg);
            }
        });


    }


    MainActivity callback;
    public void setCallback(MainActivity c){
        callback = c;
    }


    public PlayMeeting() {
        // Required empty public constructor
    }

    public void NotJoinable(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nextTime.setVisibility(View.INVISIBLE);
                msg.setText("cannot join this meeting");
                selfIntro.setVisibility(View.INVISIBLE);;
                period.setVisibility(View.INVISIBLE);;
                base.setVisibility(View.INVISIBLE);;
                suggest.setVisibility(View.INVISIBLE);;
                join.setVisibility(View.INVISIBLE);;
                bid.setVisibility(View.INVISIBLE);;
                reveal.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.INVISIBLE);;
                vote.setVisibility(View.INVISIBLE);;
            }
        });

    }

    public void toJoin(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.setText("input self intro and join");

                join.setVisibility(View.VISIBLE);
                selfIntro.setVisibility(View.VISIBLE);

                period.setVisibility(View.INVISIBLE);
                base.setVisibility(View.INVISIBLE);;
                suggest.setVisibility(View.INVISIBLE);;
                bid.setVisibility(View.INVISIBLE);;
                reveal.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.INVISIBLE);;
                vote.setVisibility(View.INVISIBLE);;
            }
        });

    }

    public void toSuggest(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.setText("Suggest");
                period.setVisibility(View.VISIBLE);
                base.setVisibility(View.VISIBLE);

                selfIntro.setVisibility(View.INVISIBLE);
                join.setVisibility(View.INVISIBLE);;
                bid.setVisibility(View.INVISIBLE);;
                reveal.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.INVISIBLE);;
                vote.setVisibility(View.INVISIBLE);;
            }
        });

    }

    public void toVote(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selfIntro.setVisibility(View.INVISIBLE);
                period.setVisibility(View.INVISIBLE);;
                base.setVisibility(View.INVISIBLE);;
                suggest.setVisibility(View.INVISIBLE);;
                join.setVisibility(View.INVISIBLE);;
                bid.setVisibility(View.INVISIBLE);;
                reveal.setVisibility(View.INVISIBLE);;
                check.setVisibility(View.INVISIBLE);;
                vote.setVisibility(View.VISIBLE);;
            }
        });

    }

    public void toBid(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selfIntro.setVisibility(View.INVISIBLE);
                period.setVisibility(View.INVISIBLE);;
                base.setVisibility(View.VISIBLE);;
                suggest.setVisibility(View.INVISIBLE);;
                join.setVisibility(View.INVISIBLE);;
                bid.setVisibility(View.VISIBLE);;
                reveal.setVisibility(View.VISIBLE);;
                check.setVisibility(View.INVISIBLE);;
                vote.setVisibility(View.VISIBLE);;
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_play_meeting, container, false);

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
                final BigInteger p = new BigInteger(period.getText().toString());
                final BigInteger b = new BigInteger(base.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.model.suggest(p,b,callback);
                        callback.model.checkMeeting(callback);
                    }
                }).start();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String selfintro = selfIntro.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.model.joinMeeting(selfintro,callback);
                        callback.check();
                        callback.message("Please show this code to your manager");
                        callback.displayQR();
                    }
                }).start();
            }
        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.model.vote(callback);
                    }
                }).start();

            }
        });

        bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BigInteger amount = new BigInteger(base.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.model.bid(amount,callback);
                    }
                }).start();
            }
        });

        reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BigInteger amount = new BigInteger(base.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.model.reveal(amount,callback);
                    }
                }).start();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.check();
                    }
                }).start();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.check();
            }
        }).start();

        return mRootView;
    }

}
