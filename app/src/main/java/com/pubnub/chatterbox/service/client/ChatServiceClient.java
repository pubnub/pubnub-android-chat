package com.pubnub.chatterbox.service.client;

import android.os.Binder;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.endpoints.History;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.ChatService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "chatServiceClient")
public class ChatServiceClient extends Binder {

    private ChatService chatService;
    private final EventDispatcher eventDispatcher = new EventDispatcher();



    private final SubscribeCallback subscribeCallback = new SubscribeCallback() {


        @Override
        public void message(PubNub pubnub, PNMessageResult msg) {
            String msgText = msg.getMessage().asText();
            String actualChannel = msg.getMessage().asText();
            long timeToken = msg.getTimetoken();

            log.debug("received message on channel: {0} of \n{1}", msg.getActualChannel(), msg.getMessage().asText());
            try {
                ChatMessage chatMessage = ChatMessage.create(msgText, timeToken);
                eventDispatcher.dispatchMessageReceived(actualChannel, chatMessage);
            }catch(Exception e){
                log.error("exception while attempting to dispatch received message",e);
            }

        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if(status.isError()){
                String[] channels = (String[])status.getAffectedChannels().toArray();
                eventDispatcher.dispatchError(status.getAffectedChannels(),status.getErrorData().getInformation());
            }

        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            log.info("successCallback for presence");
            String messageStr = presence.toString();
            PresenceMessage presenceMessage = PresenceMessage.create(messageStr);
            eventDispatcher.dispatchPresenceEvent(presence.getActualChannel(), presenceMessage);


        }


    };





    public ChatServiceClient(ChatService service) {
        chatService = service;
    }


    public void publish(final String channel, ChatMessage message) {
        final String messageString = ChatMessage.toJSON(message);
        log.debug("message: " + messageString);

        chatService.getPubNub().publish().message(messageString).channel(channel).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if(status.isError()){
                    eventDispatcher.dispatchError(status.getAffectedChannels(),status.getErrorData().getInformation());
                }else{

                    eventDispatcher.dispathMessagePublished(channel,messageString);
                }
            }
        });
    }

    //Simply Enable Push Notifications for pubnub
    public void enablePushNotification(String token){
        UserProfile profile = chatService.getUserProfile();
        if(profile != null) {
            List<String> channelsList = new ArrayList<String>();

            channelsList.add(profile.getUserName());
            try {
                PNPushAddChannelResult result = chatService.getPubNub().addPushNotificationsOnChannels()
                        .channels(channelsList)
                        .deviceId(token)
                        .pushType(PNPushType.GCM)
                        .sync();

            }catch(Exception e){
                log.error("");
            }
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


        chatService.getPubNub().publish().channel(channel).message(messageString).shouldStore(false);
    }


    public void history(String channel, long start, long end, int numberOfMessages) {
        History history = chatService.getPubNub().history()
                                                 .channel(channel)
                                                 .start(start)
                                                 .end(end)
                                                 .count( numberOfMessages)
                                                 .includeTimetoken(true)
                                                 .reverse(false);
        try {
            PNHistoryResult result = history.sync();

        } catch (PubNubException e) {
            e.printStackTrace();
        }
    }

    public void joinRoom(@NonNull final String roomName, final ChatRoomEventListener listener) {
        try {

            if (null != chatService.getPubNub()) {
                UserProfile userProfile = chatService.getUserProfile();

                List<String> channels = new ArrayList<String>();
                channels.add(roomName);

                chatService.getPubNub().setPresenceState()
                                       .channels(channels)
                                       .state(userProfile).sync();

                List<String> channelsList = new ArrayList<String>();
                channelsList.add(roomName);
                channelsList.add(userProfile.getUserName());

                chatService.getPubNub().subscribe()
                                      .withPresence()
                                      .channels(channelsList).execute();

                eventDispatcher.addEventListener(roomName, listener);
            }
        } catch (Exception e) { //DEMO only...bad
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, ChatRoomEventListener listener) {
        eventDispatcher.removeEventListener(roomName, listener);
        if (!eventDispatcher.hasEventListener(roomName)) {
            List<String> channelsList = new ArrayList<String>();
            channelsList.add(roomName);
            chatService.getPubNub().unsubscribe().channels(channelsList).execute();
        }
    }

    public void logout() {
        List<String> channelsList = new ArrayList<String>();
        channelsList.add(chatService.getUserProfile().getUserName());
        chatService.getPubNub().unsubscribe().channels(channelsList).execute();

    }

    public boolean setUserProfile(UserProfile userProfile) {
        chatService.setUserProfile(userProfile);
        return (null != chatService.getPubNub());
    }





}