package com.pubnub.chatterbox.service.client;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.service.ChatterBoxEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventManager {

    private Map<String, List<ChatterBoxEventListener>> eventListeners = new HashMap<>();

    private List<ChatterBoxEventListener> getListeners(String roomName) {
        if (!eventListeners.containsKey(roomName)) {
            eventListeners.put(roomName, new ArrayList<ChatterBoxEventListener>());
        }
        return eventListeners.get(roomName);
    }

    public boolean hasEventListener(String room) {
        return getListeners(room).size() >= 1;
    }

    public void removeEventListener(String room, ChatterBoxEventListener evListener) {
        getListeners(room).remove(evListener);
    }

    public void addEventListener(String room, ChatterBoxEventListener evlistener) {
        log.info("adding event listener {0}", evlistener);
        getListeners(room).add(evlistener);
    }

    public void dispatchMessageReceived(String room, ChatterBoxMessage m) {
        List<ChatterBoxEventListener> listeners = getListeners(room);
        for (ChatterBoxEventListener ls : listeners) {
            if (ls == null) {
                log.debug("listener was null on foreach loop");
                continue;
            } else {
                ls.messageReceived(m);
            }
        }
    }

    public void dispathMessagePublished(String room, String m) {
        List<ChatterBoxEventListener> listeners = getListeners(room);
        for (ChatterBoxEventListener ls : listeners) {
            ls.messagePublished(m);
        }
    }


    public void dispatchPresenceEvent(String room, ChatterBoxPresenceMessage m) {
        List<ChatterBoxEventListener> listeners = getListeners(room);
        for (ChatterBoxEventListener ls : listeners) {
            ls.presenceEventReceived(m);
        }
    }


}