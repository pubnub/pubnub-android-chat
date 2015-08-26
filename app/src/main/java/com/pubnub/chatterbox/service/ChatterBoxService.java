package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatterBoxService extends Service {

    //put these in shared prefs
    private static final String subscribe_key = "sub-c-8bd55596-1f48-11e5-9205-0619f8945a4f";
    private static final String publish_key = "pub-c-27c05fcb-d215-4433-9b95-a6e3fd9f49d7";

    /**
     * some internal state to manage the service interaction with the UI,
     */
    private final Map<String, List<ChatterBoxCallback>> listeners = new HashMap<>();
    private final HashMap<String, UserProfile> globalPresenceCache = new HashMap<>();
    /**
     * Heartbeat monitor
     */
    private final Callback heartBeatCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.d(Constants.LOGT, "heartbeat callback" + message.toString());
        }


    };
    /**
     * Single instance of PubNub
     */
    private Pubnub pubnub;
    /**
     * GCM Registration id
     */
    private String gcmregistrationID;

    private boolean initialized = false;

    private boolean connected = false;

    private boolean heartBeatStatus;
    private UserProfile currentUserProfile;

    public ChatterBoxService() {
    }

    public Map<String, List<ChatterBoxCallback>> getListeners() {
        return listeners;
    }

    public HashMap<String, UserProfile> getGlobalPresenceCache() {
        return globalPresenceCache;
    }

    public UserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(UserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    public boolean isHeartBeatStatus() {
        return heartBeatStatus;
    }

    public void setHeartBeatStatus(boolean heartBeatStatus) {
        this.heartBeatStatus = heartBeatStatus;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getGcmregistrationID() {
        return gcmregistrationID;
    }

    public void setGcmregistrationID(String gcmregistrationID) {
        this.gcmregistrationID = gcmregistrationID;
    }

    public Pubnub getPubNub() {
        if ((null == pubnub) && (currentUserProfile != null)) {
            //Initialize the single instance of pubnub.
            pubnub = new Pubnub(publish_key,
                                subscribe_key,
                                false);


            pubnub.setHeartbeat(120, heartBeatCallback);
            pubnub.setHeartbeatInterval(60);

            pubnub.setNonSubscribeTimeout(60);
            pubnub.setResumeOnReconnect(true);
            pubnub.setSubscribeTimeout(20000);
            pubnub.setUUID(currentUserProfile.getEmail()); //You can set a custom UUID or let the SDK generate one for you
            initialized = true;


        }

        return pubnub;
    }

    @Override
    public void onCreate() {
        initialized = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        ChatterBoxClient chatterBoxClient = new ChatterBoxClient(this);
        return chatterBoxClient;
    }

}
