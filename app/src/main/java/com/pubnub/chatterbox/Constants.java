package com.pubnub.chatterbox;

/**
 * Created by Frederick on 4/6/15.
 */
public class Constants {

    public static final String LOGT = "CBLOG";

    //Content Schema
    public static final String MSG_FROM = "from";
    public static final String MSG_TEXT = "text";
    public static final String MSG_DTSENT = "sent";


    //PubNub Constants
    public static final String SUBKEY = "demo-36";
    public static final String PUBKEY = "demo-36";
    public static final String CHANNEL = "android-webinar-chat";
    public static final String GLOBAL = "-global";


    //OTHER APP KEYS
    public static final String CURRENT_USER_PROFILE = "_data_user_profile";


    //Activity Request Codes
    public static final int SIGN_IN_REQUEST = 4000; //4k seems like a good number
}
