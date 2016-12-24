package edu.tsinghua.iiis;

/**
 * Created by SunJc on 24/12/16.
 */
public interface Updatable {
    public void updateService(String service);

    public void updateAccounts(
            String service,String[] accounts);

    public void updateMeetings(
            String service,
            String address, long balance,
            String[] meetings, boolean[] isManager);

    public void updateSingle(
            String service,
            String address, long balance,
            String meeting,  boolean isManager,
                    long startTimes,
                    long auctionVoteDur,
                    long numOfMembers,
                    long fristAuctionTime,
                    long base,
                    long period,
                    int whenBorrow, // default 0 for this not borrowed
                    long[] interests,
                    long toEarns,
                    long nextddl,
                    int whatTodo,boolean changed);

    public void message(String msg);
}
