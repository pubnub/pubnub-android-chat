package com.pubnub.chatterbox.ui;

import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.entity.UserProfile;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SessionMediator {

    private static SessionMediator instance;
    @Getter
    private Map<String, Room> rooms = new HashMap<>();

    @Setter
    @Getter
    private UserProfile userProfile;

    private SessionMediator(){
    }

    public synchronized void addRoom(Room room){
        rooms.put(room.getRoomID(),room);
    }

    public synchronized void removeRoom(Room room){
        rooms.remove(room.getRoomID());
    }

    public boolean isRoomExists(Room room){
        return rooms.containsKey(room.getRoomID());
    }

    public static SessionMediator getInstance(){
        if(instance == null){
            instance = new SessionMediator();
        }

        return(instance);
    }
}
