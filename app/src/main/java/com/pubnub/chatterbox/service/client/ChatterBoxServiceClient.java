package com.pubnub.chatterbox.service.client;

import android.os.Binder;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.ChatterBoxUserProfile;
import com.pubnub.chatterbox.service.ChatterBoxEventListener;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.ChatterboxClientManager;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatterBoxServiceClient extends Binder {

    private ChatterBoxService chatterBoxService;
    private final ChatterboxClientManager clientManager = new ChatterboxClientManager();

    private final Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timeToken) {
            try {
                log.debug("received message on channel: {0} of \n{1}", channel, message);
                ChatterBoxMessage msg = ChatterBoxMessage.create(message.toString(), timeToken);
                clientManager.dispatchMessageReceived(channel, msg);
            } catch (Exception e) {
                log.error("Exception while processing message", e);
            }
        }
    };

    private final Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timetoken) {
            clientManager.dispathMessagePublished(channel, timetoken);
        }
    };


    private final Callback presenceCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            log.info("successCallback for presence");
            String messageStr = ((JSONObject) message).toString();
            ChatterBoxPresenceMessage presenceMessage = ChatterBoxPresenceMessage.create(messageStr);
            clientManager.dispatchPresenceEvent(channel, presenceMessage);
        }
    };

    private final Callback historyCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            try {
                JSONArray jArray = (JSONArray) message;
                JSONArray messages = (JSONArray) jArray.get(0);

                for (int idx = 0; idx < messages.length(); ++idx) {
                    JSONObject m = (JSONObject) messages.get(idx);
                    ChatterBoxMessage chatterBoxMessage = ChatterBoxMessage.create(m.toString(), m.getString("timeToken"));
                    clientManager.dispatchMessageReceived(channel, chatterBoxMessage);
                }
            } catch (Exception e) {
                log.error("Exception processing history", e);
            }
        }
    };


    private final Callback unsubscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            log.info(Constants.LOGT, "unsubscribe to room: " + channel + " successful");
            //clientManager.dispatchLeaveRoomEvent();
        }
    };


    public ChatterBoxServiceClient(ChatterBoxService service) {
        chatterBoxService = service;
    }


    public void publish(final String channel, ChatterBoxMessage message) {
        String messageString = ChatterBoxMessage.toJSON(message);
        chatterBoxService.getPubNub().publish(channel, messageString, true, publishCallback);
    }

    public void history(String channel, long start, long end, int numberOfMessages) {
        chatterBoxService.getPubNub().history(channel, start, end, numberOfMessages, true, false, historyCallback);
    }

    public void joinRoom(final String roomName, final ChatterBoxEventListener listener) {
        try {
            if (null != chatterBoxService.getPubNub()) {
                Pubnub pubNub = chatterBoxService.getPubNub();
                pubNub.subscribe(new String[]{roomName}, subscribeCallback);
                pubNub.presence(roomName, presenceCallback);
                clientManager.addEventListener(roomName, listener);
            }
        } catch (PubnubException e) {
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, ChatterBoxEventListener listener) {
        clientManager.removeEventListener(roomName, listener);
        if (!clientManager.hasEventListener(roomName)) {
            chatterBoxService.getPubNub().unsubscribe(roomName, unsubscribeCallback);
        }
    }

    public boolean connect(ChatterBoxUserProfile userProfile) {
        chatterBoxService.setCurrentUserProfile(userProfile);
        return (null != chatterBoxService.getPubNub());
    }

}