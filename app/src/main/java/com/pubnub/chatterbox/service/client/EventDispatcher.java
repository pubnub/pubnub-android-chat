package com.pubnub.chatterbox.service.client;

import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.service.ChatRoomEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic="eventDispatcher")
public class EventDispatcher {

    private Map<String, List<ChatRoomEventListener>> eventListeners = new HashMap<>();

    private List<ChatRoomEventListener> getListeners(String roomName) {
        if (!eventListeners.containsKey(roomName)) {
            eventListeners.put(roomName, new ArrayList<ChatRoomEventListener>());
        }
        return eventListeners.get(roomName);
    }

    public boolean hasEventListener(String room) {
        return getListeners(room).size() >= 1;
    }

    public void removeEventListener(String room, ChatRoomEventListener evListener) {
        getListeners(room).remove(evListener);
    }

    public void addEventListener(String room, ChatRoomEventListener evlistener) {
        //log.info("adding event listener {0}", evlistener);
        List<ChatRoomEventListener> listeners = getListeners(room);
        getListeners(room).add(evlistener);
    }

    public void dispatchMessageReceived(String room, ChatMessage m) {
        List<ChatRoomEventListener> listeners = getListeners(room);
        for (ChatRoomEventListener ls : listeners) {
            if (ls == null) {
                //log.debug("listener was null on foreach loop");
            } else {
                ls.messageReceived(m);
            }
        }
    }

    public void dispathMessagePublished(String room, String m) {
        List<ChatRoomEventListener> listeners = getListeners(room);
        for (ChatRoomEventListener ls : listeners) {
            ls.messagePublished(m);
        }
    }


    public void dispatchPresenceEvent(String room, PresenceMessage m) {
        List<ChatRoomEventListener> listeners = getListeners(room);
        for (ChatRoomEventListener ls : listeners) {
            ls.presenceEventReceived(m);
        }
    }

    public void dispatchError(String room, String errorMessage) {
        List<ChatRoomEventListener> listeners = getListeners(room);
        for (ChatRoomEventListener ls : listeners) {
            ls.errorReceived(room);
        }
    }

    public void dispatchError(List<String> rooms, String errorMessage) {
        for(String room: rooms) {
            List<ChatRoomEventListener> listeners = getListeners(room);
            for (ChatRoomEventListener ls : listeners) {
                ls.errorReceived(room);
            }
        }
    }


}