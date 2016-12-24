package edu.tsinghua.iiis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SunJc on 24/12/16.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private String mItem;
        private TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTextView = (TextView) view;
        }

        public void setItem(String item) {
            mItem = item;
            mTextView.setText(item);
        }

        public void setCallback(int ac,Clickable c){
            accountOrMeeting = ac;
            this.c =c;
        }

        @Override
        public void onClick(View view) {
            if(accountOrMeeting==1){
                c.accountChosen(getPosition());
            }else{
                c.meetingChosen(getPosition());
            }
        }

        private int accountOrMeeting;
        private Clickable c;
    }

    private int accountOrMeeting;
    private Clickable c;

    private String[] mDataset;

    public MyAdapter(String[] dataset, int accountOrMeeing, Clickable c) {
        mDataset = dataset;
        this.accountOrMeeting = accountOrMeeing;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.setCallback(accountOrMeeting,c);
        return vh;
    }

    interface Clickable{
        public void accountChosen(int which);
        public void meetingChosen(int which);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItem(mDataset[position]);
    }
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
