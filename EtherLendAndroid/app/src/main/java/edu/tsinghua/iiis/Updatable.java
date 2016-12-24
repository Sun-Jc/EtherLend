package edu.tsinghua.iiis;

/**
 * Created by SunJc on 24/12/16.
 */
public interface Updatable {
    void updateService(String service);

    void updateAccounts(
            String service,String[] accounts);

    void updateMeetings(
            String service,
            String address, long balance,
            String[] meetings, boolean[] isManager);

    void updateSingle(
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
                    int whatTodo,boolean isMember,int stage);

    void message(String msg);

    void membersGot(String[] members);
}
