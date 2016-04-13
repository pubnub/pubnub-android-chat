package com.pubnub.chatterbox.service;


import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;


/**
 * Provides a callback interface for the ChatterBoxService
 * ChatterBox service interacts with channels across PubNub to
 * send and receive real-time instant messaging. This interface
 * is invoked when significant events occur.
 *
 * support@pubnub.com
 */
public interface ChatterBoxEventListener {

    void messageReceived(ChatterBoxMessage message);

    void messagePublished(String timeToken);

    void presenceEventReceived(ChatterBoxPresenceMessage pmessage);

    void heartbeat(boolean error);

    void errorReceived(String e);


}
