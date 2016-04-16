package com.pubnub.chatterbox.service.client;

import android.os.Binder;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatMessage;
import com.pubnub.chatterbox.domain.PresenceMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.ChatService;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServiceClient extends Binder {

    private ChatService chatService;
    private final EventManager eventManager = new EventManager();

    private final Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timeToken) {
            try {
                log.debug("received message on channel: {0} of \n{1}", channel, message);
                ChatMessage msg = ChatMessage.create(message.toString(), timeToken);
                eventManager.dispatchMessageReceived(channel, msg);
            } catch (Exception e) {
                log.error("Exception while processing message", e);
            }
        }
    };

    private final Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timetoken) {
            eventManager.dispathMessagePublished(channel, timetoken);
        }
    };


    private final Callback presenceCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            log.info("successCallback for presence");
            String messageStr = message.toString();
            PresenceMessage presenceMessage = PresenceMessage.create(messageStr);
            eventManager.dispatchPresenceEvent(channel, presenceMessage);
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
                    ChatMessage chatterBoxMessage = ChatMessage.create(m.toString(), m.getString("timeToken"));
                    eventManager.dispatchMessageReceived(channel, chatterBoxMessage);
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
            //eventManager.dispatchLeaveRoomEvent();
        }
    };


    public ChatServiceClient(ChatService service) {
        chatService = service;
    }


    public void publish(final String channel, ChatMessage message) {
        String messageString = ChatMessage.toJSON(message);
        chatService.getPubNub().publish(channel, messageString, true, publishCallback);
    }

    public void history(String channel, long start, long end, int numberOfMessages) {
        chatService.getPubNub().history(channel, start, end, numberOfMessages, true, false, historyCallback);
    }

    public void joinRoom(final String roomName, final ChatRoomEventListener listener) {
        try {
            if (null != chatService.getPubNub()) {
                Pubnub pubNub = chatService.getPubNub();
                pubNub.subscribe(new String[]{roomName}, subscribeCallback);
                pubNub.presence(roomName, presenceCallback);
                eventManager.addEventListener(roomName, listener);
            }
        } catch (PubnubException e) {
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, ChatRoomEventListener listener) {
        eventManager.removeEventListener(roomName, listener);
        if (!eventManager.hasEventListener(roomName)) {
            chatService.getPubNub().unsubscribe(roomName, unsubscribeCallback);
        }
    }

    public boolean setUserProfile(UserProfile userProfile) {
        chatService.setUserProfileId(userProfile.getEmail());
        return (null != chatService.getPubNub());
    }


}