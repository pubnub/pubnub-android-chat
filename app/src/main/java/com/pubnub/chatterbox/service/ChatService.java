package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

import com.pubnub.api.Pubnub;
import com.pubnub.chatterbox.BuildConfig;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.client.ChatServiceClient;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;



@Slf4j(topic ="chatService")
public class ChatService extends Service {
    /**
     * One and only  instance of PubNub
     */
    private Pubnub pubnub;
    private ChatServiceClient client;
    @Getter
    @Setter
    private String deviceToken;


    private final BroadcastReceiver tokenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = (String)intent.getExtras().getCharSequence("GCM_TOKEN");
            setDeviceToken(token);
        }
    };


    @Getter
    @Setter
    private UserProfile userProfile;


    public ChatService() {
    }


    public Pubnub getPubNub() {
        
        if(log.isTraceEnabled()){
            log.trace("entering getPubNub()");
        }

        if ((null == pubnub) && (getUserProfile() != null)) {

            pubnub = new Pubnub(BuildConfig.PUBLISH_KEY,
                                BuildConfig.SUBSCRIBE_KEY,
                                false);

            pubnub.setUUID(getUserProfile().getUserName());
            pubnub.setNonSubscribeTimeout(60);
            pubnub.setResumeOnReconnect(true);
            pubnub.setMaxRetries(5);
            pubnub.setRetryInterval(10);
            pubnub.setSubscribeTimeout(20000);

        }
        return pubnub;
    }



    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;

    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null == client) {
            client = new ChatServiceClient(this);
        }
        return client;
    }

}
