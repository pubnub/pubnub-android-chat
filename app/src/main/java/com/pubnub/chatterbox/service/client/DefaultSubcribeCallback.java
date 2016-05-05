package com.pubnub.chatterbox.service.client;


import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public class DefaultSubcribeCallback extends SubscribeCallback {

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {

    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {

    }
}