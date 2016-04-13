package com.pubnub.chatterbox.service;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;

public class DefaultChatterBoxCallback implements ChatterBoxEventListener {

    @Override
    public void messageReceived(ChatterBoxMessage message) {

    }

    @Override
    public void messagePublished(String timeToken) {
    }

    @Override
    public void presenceEventReceived(ChatterBoxPresenceMessage pmessage) {

    }

    @Override
    public void heartbeat(boolean error) {

    }

    @Override
    public void errorReceived(String e) {

    }




}
