package com.pubnub.chatterbox.domain;

/**
 * Created by Frederick on 5/11/15.
 */
public class ChatterBoxPresenceMessage {


    private String actionType;
    private String timeToken;
    private int occupancyCount;
    private UserProfile userProfile;
    private String uuid;

    public int getOccupancyCount() {
        return occupancyCount;
    }

    public void setOccupancyCount(int occupancyCount) {
        this.occupancyCount = occupancyCount;
    }

    public UserProfile getTargetProfile() {
        return userProfile;
    }

    public void setTargetProfile(UserProfile state) {
        this.userProfile = state;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTimeToken() {
        return timeToken;
    }

    public void setTimeToken(String timeToken) {
        this.timeToken = timeToken;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuids) {
        this.uuid = uuids;
    }

}
