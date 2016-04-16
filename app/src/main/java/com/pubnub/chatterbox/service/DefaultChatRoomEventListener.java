package com.pubnub.chatterbox.service;

import com.pubnub.chatterbox.domain.ChatMessage;
import com.pubnub.chatterbox.domain.PresenceMessage;

public class DefaultChatRoomEventListener implements ChatRoomEventListener {

    @Override
    public void messageReceived(ChatMessage message) {

    }

    @Override
    public void messagePublished(String timeToken) {
    }

    @Override
    public void presenceEventReceived(PresenceMessage pmessage) {

    }

    @Override
    public void heartbeat(boolean error) {

    }

    @Override
    public void errorReceived(String e) {

    }


}
