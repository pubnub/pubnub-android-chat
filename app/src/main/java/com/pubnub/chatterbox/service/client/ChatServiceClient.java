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
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.entity.StatusEvent;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.ChatService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Subscribers;
import rx.subjects.PublishSubject;

@Slf4j(topic = "chatServiceClient")
public class ChatServiceClient extends Binder {

    private ChatService chatService;

    //Differnt types of events we would like to Observe from our PubNub integration
    //this could also provide an integration point with other backends, custom Http based services
    //and additional API's

    @Getter
    private final PublishSubject<ChatMessage> observableMessageStream = PublishSubject.create();
    @Getter
    private final PublishSubject<PresenceMessage> presenceEventStream = PublishSubject.create();
    @Getter
    private final PublishSubject<StatusEvent> statusEventStream = PublishSubject.create();



    public void messageObserved(final Func1<ChatMessage,Void> chatMessageResponder){
        Subscriber<ChatMessage> s = Subscribers.create(new Action1<ChatMessage>() {
            @Override
            public void call(ChatMessage chatMessage) {
                chatMessageResponder.call(chatMessage);
            }
        });

        observableMessageStream.subscribe(s);
    }


    public void presenceObserved(final Func1<PresenceMessage, Void> presenceEventResponder){
        Subscriber<PresenceMessage> s = Subscribers.create(new Action1<PresenceMessage>() {
            @Override
            public void call(PresenceMessage presenceMessage) {
                presenceEventResponder.call(presenceMessage);
            }
        });

        presenceEventStream.subscribe(s);
    }

    public void statusObserved(final Func1<StatusEvent,Void> statusEventResponder){
        Subscriber<StatusEvent> s = Subscribers.create(new Action1<StatusEvent>() {
            @Override
            public void call(StatusEvent statusEvent) {
                statusEventResponder.call(statusEvent);
            }
        });

        statusEventStream.subscribe(s);
    }


    private final SubscribeCallback subscribeCallback = new SubscribeCallback() {

        @Override
        public void message(PubNub pubnub, PNMessageResult msg) {
            String msgText = msg.getMessage().asText();
            long timeToken = msg.getTimetoken();
            log.debug("received message on channel: {0} of \n{1}", msg.getActualChannel(), msg.getMessage().asText());

            try {
                ChatMessage chatMessage = ChatMessage.create(msgText, timeToken);
                observableMessageStream.onNext(chatMessage);

            }catch(Exception e){
                log.error("exception while attempting to dispatch received message",e);
            }

        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            StatusEvent event = new StatusEvent();
            event.setMessage(status.getCategory().name());
            event.setType(status.getCategory().name());
            statusEventStream.onNext(event);

        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult pevent) {
            log.info("presence event received: " + pevent.toString());
            String event = pevent.getEvent();

            if(pevent.getEvent().equals("join")){
                List<String> channels = Arrays.asList(pevent.getActualChannel());
                try {
                    PNGetStateResult result = pubnub.getPresenceState()
                                                    .uuid(pevent.getUuid())
                                                    .channels(channels)
                                                    .sync();

                    Map<String,Object> uuidState = result.getStateByUUID();
                    //process state attributes


                }catch(PubNubException e){
                    log.error("exception attempting to get state during join event",e);
                }
            }


            PresenceMessage presenceMessage = PresenceMessage.create(pevent.toString());
            presenceEventStream.onNext(presenceMessage);
        }
    };





    public ChatServiceClient(ChatService service) {
        chatService = service;
        service.getPubNub().addListener(subscribeCallback);
    }


    public void publish(final String channel, ChatMessage message) {
        final String messageString = ChatMessage.toJSON(message);
        log.debug("message: " + messageString);

        chatService.getPubNub().publish()
                               .message(messageString)
                               .channel(channel)
                               .async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {

                StatusEvent statusEvent = new StatusEvent();
                statusEvent.setType("message-published");
                statusEvent.setError(status.isError());
                statusEvent.setMessage(status.getErrorData().getInformation());
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

    public void joinRoom(@NonNull final String roomName, final Observer<ChatMessage> listener) {
        try {

            if (null != chatService.getPubNub()) {
                UserProfile userProfile = chatService.getUserProfile();

                List<String> channels = new ArrayList<String>();
                channels.add(roomName);

                chatService.getPubNub().setPresenceState()
                                       .channels(channels)
                                       .state(userProfile).sync();


                List<String>  channelList = Arrays.asList(roomName,userProfile.getUserName());

                chatService.getPubNub().subscribe()
                                       .withPresence()
                                       .channels(channelList).execute();

               observableMessageStream.subscribe(listener);
            }
        } catch (Exception e) { //DEMO only...bad
            log.error("Exception while attempting to register to listen to a room");
        }
    }

    public void leaveRoom(String roomName, Subscriber<ChatMessage> listener) {


        /*
        if (!eventDispatcher.hasEventListener(roomName)) {
            List<String> channelsList = new ArrayList<String>();
            channelsList.add(roomName);
            chatService.getPubNub().unsubscribe().channels(channelsList).execute();
        }*/
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