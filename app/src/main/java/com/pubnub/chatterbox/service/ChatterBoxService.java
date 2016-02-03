package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import com.pubnub.chatterbox.BuildConfig;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.binder.ChatterBoxServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatterBoxService extends Service {

    private final Map<String, List<ChatterBoxCallback>> listeners = new HashMap<>();
    private final HashMap<String, UserProfile> presenceCache = new HashMap<>();


    /**
     * Single instance of PubNub
     */
    private Pubnub pubnub;
    /**
     * GCM Registration id
     */
    private String gcmregistrationID;

    private boolean connected = false;

    private UserProfile currentUserProfile;

    public ChatterBoxService() {
    }

    public Map<String, List<ChatterBoxCallback>> getListeners() {
        return listeners;
    }

    public HashMap<String, UserProfile> getPresenceCache() {
        return presenceCache;
    }

    public UserProfile getCurrentUserProfile() {
        return currentUserProfile;
    }

    public void setCurrentUserProfile(UserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
        this.connected = true;
    }

    public boolean isConnected() {
        return connected;
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

            //1. registration id can change
            //2. cache your registration ID in preferences
            //3. remove stale key
            //4. add new key
            pubnub.setHeartbeat(140, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d(Constants.LOGT, "heartbeat received");

                }

                @Override
                public void errorCallback(String channel, Object message) {
                    Log.e(Constants.LOGT, "error receiving heartbeat");
                    pubnub.disconnectAndResubscribe();
                }
            });
            pubnub.setHeartbeatInterval(120);
            pubnub.setNonSubscribeTimeout(60);
            pubnub.setResumeOnReconnect(true);
            pubnub.setMaxRetries(500);
            pubnub.setSubscribeTimeout(20000);

            connected = true;
        }

        return pubnub;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        ChatterBoxServiceClient chatterBoxClient = new ChatterBoxServiceClient(this);
        return chatterBoxClient;
    }

}
