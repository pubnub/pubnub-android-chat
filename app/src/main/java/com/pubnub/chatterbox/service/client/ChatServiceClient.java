package com.pubnub.chatterbox.service.client;

import android.os.Binder;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;

import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import com.pubnub.chatterbox.Constants;
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


    private final SubscribeCallback pnSubscribeCallback = new DefaultSubcribeCallback() {

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {

            try {
                log.debug("received message on channel: {0} of \n{1}", message.getSubscribedChannel(), message);

                ChatMessage msg = ChatMessage.create(message.toString(), message.getTimetoken());
                eventDispatcher.dispatchMessageReceived(message.getSubscribedChannel(), msg);
            } catch (Exception e) {
                log.error("exception" + e.toString());
            }
        }


        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            log.info("successCallback for presence");
            String messageStr = presence.getActualChannel();
            PresenceMessage presenceMessage = new PresenceMessage();
            presenceMessage.setUuid(presence.getUuid());
            presenceMessage.setOccupancyCount(presence.getOccupancy());
            presenceMessage.setActionType(presence.getEvent());
            presenceMessage.setTimeToken(presence.getTimetoken());
            eventDispatcher.dispatchPresenceEvent(presence.getActualChannel(), presenceMessage);
        }
    };


    private final DefaultSubcribeCallback publishCallback = new DefaultSubcribeCallback() {
        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if (status.isError()) {
                log.error("error in publish: " + status.getErrorData().toString());
            }
        }
    };







    public ChatServiceClient(ChatService service) {
        chatService = service;
    }


    public void publish(final String channel, ChatMessage message) {
        String messageString = ChatMessage.toJSON(message);
        log.debug("message: " + messageString);

        chatService.getPubNub().publish()
                .channel(channel)
                .shouldStore(true)
                .message(messageString)
                .usePOST(false)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        log.debug("onresponse from publish");
                        log.debug(result.toString());
                        if(status.isError() == false){
                            eventDispatcher.dispathMessagePublished(channel,result.getTimetoken().toString());
                        }else{
                            eventDispatcher.dispatchError(channel,status.toString());
                        }
                    }
                });


    }

    //Simply Enable Push Notifications for pubnub
    public void enablePushNotification(String token) {
        UserProfile profile = chatService.getUserProfile();
        if (profile != null) {
            List<String> s = new ArrayList<String>();
            s.add(profile.getUserName());
            try {
                PNPushAddChannelResult result = chatService.getPubNub()
                        .addPushNotificationsOnChannels()
                        .channels(s)
                        .pushType(PNPushType.GCM).deviceId(token)
                        .sync();
                //hmm...no result types.


            }catch(PubNubException e){
                log.debug("exception while attempting to register device", e);
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

            chatService.getPubNub().publish().channel(channel).async(new PNCallback<PNPublishResult>(){
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {
                        if(status.isError()){
                            log.error("error while publish push");
                            eventDispatcher.dispatchError(channel,status.getErrorData().toString());
                        }
                }
            });

        } catch (JSONException e) {
            log.error("could not create payload", e);
        }



    }


    public void history(String channel, long start, long end, int numberOfMessages) {
        try {
            chatService.getPubNub().history().channel(channel).start(start).end(end).count(numberOfMessages).sync();
        } catch (PubNubException on) {

        }

    }

    public void joinRoom(@NonNull final String roomName, final ChatRoomEventListener listener) {
        try {

            if (null != chatService.getPubNub()) {
                PubNub pubNub = chatService.getPubNub();
                UserProfile userProfile = chatService.getUserProfile();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", userProfile.getUserName());
                jsonObject.put("firstName", userProfile.getFirstName());
                jsonObject.put("lastName", userProfile.getLastName());
                jsonObject.put("email", userProfile.getEmail());
                jsonObject.put("status", "online");


                ArrayList<String> channels = new ArrayList<>();
                channels.add(userProfile.getUserName());
                channels.add(Constants.MAIN_CHAT_ROOM);

                eventDispatcher.addEventListener(roomName, listener);
                pubNub.addListener(pnSubscribeCallback);
                pubNub.setPresenceState()
                        .channels(channels)
                        .state(jsonObject)
                        .async(new PNCallback< PNSetStateResult>(){
                            @Override
                            public void onResponse(PNSetStateResult result, PNStatus status) {
                                log.debug(status.toString());
                            }
                        });



                pubNub.subscribe().channels(channels)
                                  .withTimetoken(0L)
                                  .withPresence()
                                  .execute();



            }


        } catch (Exception e) { //DEMO only...bad
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, ChatRoomEventListener listener) {
        eventDispatcher.removeEventListener(roomName, listener);
        if (!eventDispatcher.hasEventListener(roomName)) {
            ArrayList<String> channels = new ArrayList<>();
            channels.add(roomName);
            chatService.getPubNub().unsubscribe().channels(channels).execute();
        }
    }

    public void logout() {
        chatService.getPubNub().unsubscribe();
    }

    public boolean setUserProfile(UserProfile userProfile) {
        chatService.setUserProfile(userProfile);
        return (null != chatService.getPubNub());
    }


}