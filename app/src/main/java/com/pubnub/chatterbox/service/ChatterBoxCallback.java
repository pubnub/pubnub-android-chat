package com.pubnub.chatterbox.service;


import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPrivateChatRequest;


/**
 * Provides a callback interface for the ChatterBoxService
 * ChatterBox service interacts with channels across PubNub to
 * send and receive realtime instant messaging. This interface
 * is invoked when significant events occur.
 *
 * @author Frederick R. Brock
 */
public interface ChatterBoxCallback {

    void onMessage(ChatterBoxMessage message);

    void onMessagePublished(String message);

    void onPresence(ChatterBoxPresenceMessage pmessage);

    void onHeartBeat(boolean error);

    void onError(String e);

    void onPrivateChatRequest(ChatterBoxPrivateChatRequest request);

    void onDisconnect();

    void onConnect();


}
