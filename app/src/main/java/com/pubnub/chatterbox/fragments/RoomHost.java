package com.pubnub.chatterbox.fragments;

import com.pubnub.chatterbox.domain.Room;

import java.util.Map;

/**
 * Created by Frederick on 5/21/15.
 */
public interface RoomHost {
    void connectedToRoom(String roomTitle, String roomChannelForHereNow);
    void disconnectingFromRoom(String roomTitle);
    Map<String, Room> getCurrentRooms();

}
