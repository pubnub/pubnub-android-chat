package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pubnub.api.Pubnub;
import com.pubnub.chatterbox.BuildConfig;
import com.pubnub.chatterbox.domain.ChatterBoxUserProfile;
import com.pubnub.chatterbox.service.client.ChatterBoxServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic ="chatterboxService")
@Log
public class ChatterBoxService extends Service {
    /**
     * One and only  instance of PubNub
     */
    private Pubnub pubnub;
    private ChatterBoxServiceClient client;

    @Setter
    @Getter
    private ChatterBoxUserProfile currentUserProfile;

    public ChatterBoxService() {
    }


    public Pubnub getPubNub() {
        if(log.isTraceEnabled()){
            log.trace("entering getPubNub()");
        }

        if ((null == pubnub) && (getCurrentUserProfile() != null)) {

            pubnub = new Pubnub(BuildConfig.PUBLISH_KEY,
                    BuildConfig.SUBSCRIBE_KEY,
                    false);
            pubnub.setUUID(currentUserProfile.getEmail()); //You can set a custom UUID or let the SDK generate one for you
            pubnub.setNonSubscribeTimeout(60);
            pubnub.setResumeOnReconnect(true);
            pubnub.setMaxRetries(5);
            pubnub.setRetryInterval(10);
            pubnub.setSubscribeTimeout(20000);

        }
        return pubnub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log.info("service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null == client) {
            client = new ChatterBoxServiceClient(this);
        }
        return client;
    }

}
