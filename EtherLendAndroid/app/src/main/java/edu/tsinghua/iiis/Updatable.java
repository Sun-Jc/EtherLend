package edu.tsinghua.iiis;

import java.math.BigInteger;

/**
 * Created by SunJc on 24/12/16.
 */
public interface Updatable {
    void updateService(String service);

    void updateAccounts(
            String service,String[] accounts);

    void updateMeetings(
            String service,
            String address, BigInteger balance,
            String[] meetings, boolean[] isManager);

    void updateSingle(
            String service,
            String address, BigInteger balance,
            String meeting,  boolean isManager,
                    BigInteger startTimes,
                    BigInteger auctionVoteDur,
                    BigInteger numOfMembers,
                    BigInteger fristAuctionTime,
                    BigInteger base,
                    BigInteger period,
                    int whenBorrow, // default 0 for this not borrowed
                    BigInteger[] interests,
                    BigInteger toEarns,
                    BigInteger nextddl,
                    int whatTodo,boolean isMember,int stage);

    void message(String msg);

    void membersGot(String[] members);
}
