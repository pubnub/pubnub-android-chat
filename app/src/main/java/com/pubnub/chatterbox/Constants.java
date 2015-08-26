package com.pubnub.chatterbox;


public class Constants {

    public static final String LOGT = "CB_LOG";

    //Content Schema
    public static final String MSG_FROM = "from";
    public static final String MSG_TEXT = "text";
    public static final String MSG_DTSENT = "sent";


    public static final String GLOBAL = "-global";
    public static final String PRIVATE = "-private";
    public static final String MAIN_CHAT_ROOM = "AWG" + GLOBAL;


    //OTHER APP KEYS
    public static final String CURRENT_USER_PROFILE = "_data_user_profile";

    /**
     * GCM Project ID Number, also refered to as "sender-ID"
     */
    public static final String PROJECT_ID = "347849282940";


    //Activity Request Codes
    public static final int SIGN_IN_REQUEST = 4000; //4k seems like a good number
}
