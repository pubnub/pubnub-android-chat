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



public class ChatterBoxService extends Service {

    private final Map<String, List<ChatterBoxEventListener>> listeners = new HashMap<>();

    /**
     * One and only  instance of PubNub
     */
    private Pubnub pubnub;
    private ChatterBoxServiceClient client;


    /**
     * GCM Registration id
     */
    private String gcmregistrationID;

    private ChatterBoxUserProfile currentUserProfile;

    public ChatterBoxService() {
    }

    public Map<String, List<ChatterBoxEventListener>> getListeners() {
        return listeners;
    }


    public ChatterBoxUserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(ChatterBoxUserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }


    public String getGcmregistrationID() {
        return gcmregistrationID;
    }

    public void setGcmregistrationID(String gcmregistrationID) {
        this.gcmregistrationID = gcmregistrationID;
    }

    public Pubnub getPubNub() {

        if ((null == pubnub) && (currentUserProfile != null)) {

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

    }

    @Override
    public IBinder onBind(Intent intent) {
        if(null == client){
            client = new ChatterBoxServiceClient(this);
        }
        return client;
    }

}
