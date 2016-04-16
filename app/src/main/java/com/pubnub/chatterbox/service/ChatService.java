package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pubnub.api.Pubnub;
import com.pubnub.chatterbox.BuildConfig;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic ="chatterboxService")
public class ChatService extends Service {
    /**
     * One and only  instance of PubNub
     */
    private Pubnub pubnub;
    private ChatServiceClient client;

    @Setter
    @Getter
    private String userProfileId;

    public ChatService() {
    }


    public Pubnub getPubNub() {
        if(log.isTraceEnabled()){
            log.trace("entering getPubNub()");
        }

        if ((null == pubnub) && (getUserProfileId() != null)) {

            pubnub = new Pubnub(BuildConfig.PUBLISH_KEY,
                                BuildConfig.SUBSCRIBE_KEY,
                                false);

            pubnub.setUUID(getUserProfileId());
            pubnub.setNonSubscribeTimeout(60);
            pubnub.setResumeOnReconnect(true);
            pubnub.setMaxRetries(5);
            pubnub.setRetryInterval(10);
            pubnub.setSubscribeTimeout(20000);

        }
        return pubnub;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null == client) {
            client = new ChatServiceClient(this);
        }
        return client;
    }

}
