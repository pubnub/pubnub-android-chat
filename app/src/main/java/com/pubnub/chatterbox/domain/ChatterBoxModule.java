package com.pubnub.chatterbox.domain;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pubnub.api.PubNub;

import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.ui.SessionMediator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatterBoxModule {

    private UserProfile userProfile;
    private String platformString;


    public ChatterBoxModule(UserProfile userProfile){
        this.userProfile = userProfile;
    }

    @Provides
    @Singleton
    public Gson gson(){
        Gson gson = new GsonBuilder().create();
        return gson;
    }

    @Provides
    @Singleton
    public SessionMediator sessionMediator(){
        return SessionMediator.getInstance();
    }

    @Provides
    @Singleton
    public PubNub providePubNub(UserProfile profile, String deviceTag){

        /*PubNub pubnub = new PubNub(BuildConfig.PUBLISH_KEY,
                                  BuildConfig.SUBSCRIBE_KEY, true);

        pubnub(userProfile.getEmail() + "~" + deviceTag); */

        return(null);
    }

    @Provides
    @Singleton
    public UserProfile provideUserProfile(){
        return this.userProfile;
    }


}

