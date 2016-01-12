package com.pubnub.chatterbox.service;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPrivateChatRequest;

/**
 * Created by Frederick on 5/14/15.
 */

public class DefaultChatterBoxCallback implements ChatterBoxCallback {

    @Override
    public void onMessage(ChatterBoxMessage message) {

    }

    @Override
    public void onMessagePublished(String timeToken) {

    }

    @Override
    public void onPresence(ChatterBoxPresenceMessage pmessage) {

    }

    @Override
    public void onHeartBeat(boolean error) {

    }

    @Override
    public void onError(String e) {

    }

    @Override
    public void onPrivateChatRequest(ChatterBoxPrivateChatRequest request) {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }
}
