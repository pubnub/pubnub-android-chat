package com.pubnub.chatterbox.fragments;

/**
 * Created by Frederick on 5/21/15.
 */
public interface RoomHost {
    void connectedToRoom(String roomTitle, String roomChannelForHereNow);
    void disconnectingFromRoom(String roomTitle);

}
