package com.pubnub.chatterbox.service.client;

import android.os.Binder;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.ChatService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "chatServiceClient")
public class ChatServiceClient extends Binder {

    private ChatService chatService;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    private final Callback subscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timeToken) {
            try {
                log.debug("received message on channel: {0} of \n{1}", channel, message);
                ChatMessage msg = ChatMessage.create(message.toString(), timeToken);
                eventDispatcher.dispatchMessageReceived(channel, msg);
            } catch (Exception e) {
                log.error("Exception while processing message", e);
            }
        }
    };

    private final Callback publishCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message, String timetoken) {
            eventDispatcher.dispathMessagePublished(channel, timetoken);
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            eventDispatcher.dispatchError(channel, error.getErrorString());
        }
    };


    private final Callback presenceCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            log.info("successCallback for presence");
            String messageStr = message.toString();
            PresenceMessage presenceMessage = PresenceMessage.create(messageStr);
            eventDispatcher.dispatchPresenceEvent(channel, presenceMessage);
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
                    eventDispatcher.dispatchMessageReceived(channel, chatterBoxMessage);
                }
            } catch (Exception e) {
                log.error("Exception processing history", e);
            }
        }
    };


    private final Callback heartBeatCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            log.debug("heartbeat sent successfully");
        }
    };


    private final Callback unsubscribeCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            log.info(Constants.LOGT, "unsubscribe to room: " + channel + " successful");
            //eventDispatcher.dispatchLeaveRoomEvent();
        }
    };

    private final Callback loggingCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            log.info(Constants.LOGT, "call completed");
        }


    };


    public ChatServiceClient(ChatService service) {
        chatService = service;
    }


    public void publish(final String channel, ChatMessage message) {
        String messageString = ChatMessage.toJSON(message);
        log.debug("message: " + messageString);

        chatService.getPubNub().publish(channel, messageString, true, publishCallback);
    }

    //Simply Enable Push Notifications for pubnub
    public void enablePushNotification(String token){
        UserProfile profile = chatService.getUserProfile();
        if(profile != null) {
            chatService.getPubNub().enablePushNotificationsOnChannel(profile.getUserName(), token);
        }
    }


    public void publishPush(final String channel, ChatMessage message) {
        String messageString = ChatMessage.toJSON(message);
        log.debug("message: " + messageString);
        try {
            JSONObject pushPayload = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject notification = new JSONObject();

            notification.put("body", message.getContent());
            notification.put("title", "You got push notifications?");

            data.put("conversation", message.getConversation());
            data.put("content", message.getContent());
            //other interesting attributes here

            pushPayload.put("data", data);
            pushPayload.put("notification", notification);


            JSONObject pngcm = new JSONObject();
            pngcm.put("pn_gcm", pushPayload);
        } catch (JSONException e) {
            log.error("could not create payload", e);
        }


        chatService.getPubNub().publish(channel, messageString, true, publishCallback);
    }


    public void history(String channel, long start, long end, int numberOfMessages) {
        chatService.getPubNub().history(channel, start, end, numberOfMessages, true, false, historyCallback);
    }

    public void joinRoom(@NonNull final String roomName, final ChatRoomEventListener listener) {
        try {

            if (null != chatService.getPubNub()) {
                Pubnub pubNub = chatService.getPubNub();
                UserProfile userProfile = chatService.getUserProfile();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", userProfile.getUserName());
                jsonObject.put("firstName", userProfile.getFirstName());
                jsonObject.put("lastName", userProfile.getLastName());
                jsonObject.put("email", userProfile.getEmail());
                jsonObject.put("status", "online");


                pubNub.setState(userProfile.getUserName(), userProfile.getUserName(), jsonObject, loggingCallback);
                pubNub.subscribe(new String[]{roomName, userProfile.getUserName()}, subscribeCallback);
                pubNub.presence(roomName, presenceCallback);
                eventDispatcher.addEventListener(roomName, listener);
            }
        } catch (Exception e) { //DEMO only...bad
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, ChatRoomEventListener listener) {
        eventDispatcher.removeEventListener(roomName, listener);
        if (!eventDispatcher.hasEventListener(roomName)) {
            chatService.getPubNub().unsubscribe(roomName, unsubscribeCallback);
        }
    }

    public void logout() {
        chatService.getPubNub().unsubscribe(chatService.getUserProfile().getUserName(), unsubscribeCallback);
        chatService.getPubNub().disconnectAndResubscribe();
    }

    public boolean setUserProfile(UserProfile userProfile) {
        chatService.setUserProfile(userProfile);
        return (null != chatService.getPubNub());
    }





}