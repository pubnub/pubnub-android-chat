package com.pubnub.chatterbox.domain;

import org.json.JSONObject;

/**
 * Created by Frederick on 5/14/15.
 */
public class ChatterBoxPrivateChatRequest {

    public static final String FROM_USER = "fromUser";
    public static final String ON_CHANNEL = "onchannel";
    private String fromUser;
    private String onChannel;

    //TODO: I could have just used GSON...I hate catching Exception on a factory
    //method shoved in a domain object, but no time.
    //TODO: refactor to make better...use GSON or fastJSON
    public ChatterBoxPrivateChatRequest create(JSONObject object) throws Exception {
        String fromUser = object.getString(FROM_USER);
        String onChannel = object.getString(ON_CHANNEL);

        ChatterBoxPrivateChatRequest pcr = new ChatterBoxPrivateChatRequest();
        pcr.setFromUser(fromUser);
        pcr.setOnChannel(onChannel);

        return (pcr);
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getOnChannel() {
        return onChannel;
    }

    public void setOnChannel(String onChannel) {
        this.onChannel = onChannel;
    }
    //add PAM auth key stuff


}
