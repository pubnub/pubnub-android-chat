package com.pubnub.chatterbox.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pubnub.chatterbox.BuildConfig;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "registrationIntentService")
public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("registrationintent");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(BuildConfig.GCM_PROJECT_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Intent tokenReceived = new Intent("tokenReceived");
            tokenReceived.putExtra("GCM_TOKEN", token);

            //notify interested intents
            sendBroadcast(tokenReceived);

        } catch (IOException i) {
            log.error("exception registering user");
        }

    }


}

