package com.pubnub.chatterbox.service;


import com.pubnub.chatterbox.domain.ChatMessage;
import com.pubnub.chatterbox.domain.PresenceMessage;


/**
 * Provides a callback interface for the ChatService
 * ChatterBox service interacts with channels across PubNub to
 * send and receive real-time instant messaging. This interface
 * is invoked when significant events occur.
 * <p/>
 * support@pubnub.com
 */
public interface ChatRoomEventListener {

    void messageReceived(ChatMessage message);

    void messagePublished(String timeToken);

    void presenceEventReceived(PresenceMessage pmessage);

    void heartbeat(boolean error);

    void errorReceived(String e);


}
