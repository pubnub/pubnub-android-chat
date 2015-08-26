package com.pubnub.chatterbox.service.binder;

import android.os.Binder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxCallback;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.PresenceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Frederick on 5/21/15.
 */
public class ChatterBoxClient extends Binder {

    /**
     * reference to our service.
     */
    private ChatterBoxService chatterBoxService;

    ChatterBoxClient getService() {
        return ChatterBoxClient.this;
    }

    public ChatterBoxClient(ChatterBoxService service) {
        chatterBoxService = service;
    }


    public void publish(final String channel, ChatterBoxMessage message) {
        try {
            JSONObject messageJSON = new JSONObject();
            messageJSON.put(ChatterBoxMessage.DEVICETAG, message.getDeviceTag());
            messageJSON.put(ChatterBoxMessage.SENDERUUID, chatterBoxService.getPubNub().getUUID()); //Set the uuid
            messageJSON.put(ChatterBoxMessage.EMOTICON, "");
            messageJSON.put(ChatterBoxMessage.FROM, message.getFrom());
            messageJSON.put(ChatterBoxMessage.SENTON, new Date());
            messageJSON.put(ChatterBoxMessage.TYPE, message.getType());
            messageJSON.put(ChatterBoxMessage.MESSAGECONTENT, message.getMessageContent());
            messageJSON.put(ChatterBoxMessage.SENDERUUID, chatterBoxService.getCurrentUserProfile().getEmail());


            chatterBoxService.getPubNub().publish(channel,messageJSON, true, new Callback(){
                @Override
                public void successCallback(String channel, Object message) {
                    List<ChatterBoxCallback> listeners = chatterBoxService.getListeners().get(channel);
                    String status = "";
                    String timeToken = "";
                    String resultCode = "";
                    try{
                        JSONArray results = (JSONArray)message;
                        resultCode = results.getString(0);
                        status = results.getString(1);
                        timeToken = results.getString(2);
                    }catch (JSONException e){
                        Log.d(Constants.LOGT, "Exception while attempting to process publish results.");
                    }

                    //Give the timeToken back to all listeners on that channel
                    //make sure the callback runs on the UI thread!!!
                    for(ChatterBoxCallback chatterBoxCallback: listeners){
                        chatterBoxCallback.onMessagePublished(timeToken);
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error){
                    List<ChatterBoxCallback> listeners = chatterBoxService.getListeners().get(channel);
                    for(ChatterBoxCallback chatterBoxCallback: listeners){
                        chatterBoxCallback.onError(error.getErrorString());
                    }
                }

            });

        } catch (Exception e) {
            Log.e(Constants.LOGT, "exception while publishing message", e);
        }
    }



    public void history(String channel) {


        long fiveMinAgo = (new Date().getTime() - (5 * 60 * 1000)) * 100000;

        //Starting Five minutes ago
        chatterBoxService.getPubNub().history("AWG" + Constants.GLOBAL, fiveMinAgo, -1, 50, true, true, new Callback() {

            @Override
            public void successCallback(String channel, Object message) {
                try {
                    Log.d(Constants.LOGT, "successful history call");
                    JSONArray jarr = (JSONArray) message;
                    JSONArray history = jarr.getJSONArray(1);
                } catch (Exception e) {
                    Log.e(Constants.LOGT, "Exception processing history", e);
                }
            }

            @Override
            public void errorCallback(String message, PubnubError error) {
                //Process error
            }


        });


    }


    public void addRoom(final String roomName, final ChatterBoxCallback listener) {

        if (chatterBoxService.isInitialized()) {
            boolean bfound = false;
            String[] currentChannels = chatterBoxService.getPubNub().getSubscribedChannelsArray();
            for (String c : currentChannels) {
                if (c.equals(roomName)) {
                    bfound = true;
                    break;
                }
            }

            if (!bfound) { //no one subscription to this room, add one AWG-Global
                try {
                    //PubNub Specific

                    chatterBoxService.getPubNub().subscribe(roomName, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message, String timetoken) {
                            try {
                                Log.d(Constants.LOGT + "-MSGCB", "received message on channel: " + channel);

                                if (message instanceof JSONObject) {
                                    JSONObject jmessage = (JSONObject) message;
                                    String messageType = jmessage.getString(ChatterBoxMessage.TYPE);
                                    if (messageType.equals("chattmessage")) {

                                        ChatterBoxMessage msg = ChatterBoxMessage.create(jmessage, timetoken);

                                        //Application specific
                                        List<ChatterBoxCallback> thisRoomListeners = chatterBoxService.getListeners().get(roomName);
                                        for (ChatterBoxCallback l : thisRoomListeners) {
                                            l.onMessage(msg);
                                        }

                                    }
                                } else {
                                    //TODO: Is this actually required...shouldn't I know
                                    //how messages are sent?
                                    Log.d(Constants.LOGT, "object received as " + message.getClass().getName());
                                }
                            } catch (Exception e) {
                                Log.e(Constants.LOGT, "Exception while processing message", e);
                            }
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError err) {
                            Log.e(Constants.LOGT, "error processing messages" + err.toString());
                            //TODO: Add appropriate error handler
                        }
                    });

                    List<ChatterBoxCallback> l = null;
                    if (!chatterBoxService.getListeners().containsKey(roomName)) {
                        l = new ArrayList<ChatterBoxCallback>();
                    } else {
                        l = chatterBoxService.getListeners().get(roomName);
                    }

                    l.add(listener); //add the listener for this room.
                    chatterBoxService.getListeners().put(roomName, l);

                    //Set up the Presence on this Room
                    chatterBoxService.getPubNub().presence(roomName, new PresenceCallback(l, chatterBoxService.getPubNub(), chatterBoxService.getGlobalPresenceCache()));


                } catch (Exception e) {
                    //TODO: handle this exception so that the app continues to function
                    Log.e(Constants.LOGT, "exception while adding subscription", e);
                    //TODO:MAYBE CALL THE CLIENTS ERROR HANDLER
                }

            }

            //TODO: Move this code
            List<ChatterBoxCallback> l = null;
            if (!chatterBoxService.getListeners().containsKey(roomName)) {
                l = new ArrayList<>();
            } else {
                l = chatterBoxService.getListeners().get(roomName);
            }

            l.add(listener); //add the listener for this room.
            chatterBoxService.getListeners().put(roomName, l);


        }
    }


    public void removeRoomListener(String roomName, ChatterBoxCallback listener){
        Map<String,List<ChatterBoxCallback>> rooms = chatterBoxService.getListeners();
        List<ChatterBoxCallback> roomListeners = rooms.get(roomName);
        roomListeners.remove(listener);
    }


    public void leaveRoom(String roomName) {
        Map<String,List<ChatterBoxCallback>> rooms = chatterBoxService.getListeners();
        List<ChatterBoxCallback> roomListeners = rooms.get(roomName);
        roomListeners.clear();
        chatterBoxService.getPubNub().unsubscribe(roomName);
    }


    public boolean isConnected() {
        return chatterBoxService.isConnected();
    }


    public boolean connect(UserProfile userProfile) {


        chatterBoxService.setConnected(true);
        chatterBoxService.setCurrentUserProfile(userProfile);

        return chatterBoxService.isConnected();
    }


    /**
     * Listen to presence events for a channel. Accepts a listener and channel and
     * registers for presence events. TODO://REFACTOR..
     *
     * @param roomName
     * @param presenceListener
     */

    public void presence(String roomName, final ChatterBoxCallback presenceListener) {
        List<ChatterBoxCallback> listeners = new ArrayList<ChatterBoxCallback>();
        listeners.add(presenceListener);

        HashMap<String, UserProfile> presenceCache =
                chatterBoxService.getGlobalPresenceCache();

        Pubnub pubnub = chatterBoxService.getPubNub();

        try {
            pubnub.presence(roomName, new PresenceCallback(listeners, pubnub, presenceCache));

        } catch (PubnubException e) {
            Log.e(Constants.LOGT, "exception while attempting to register for presence", e);
        }
    }


    public boolean disconnect(UserProfile userProfile, boolean enablePush) {
        String[] channels = chatterBoxService.getPubNub().getSubscribedChannelsArray();
        //If I want to enable push this is where I will do it. For each channel I am
        //subscribed to I will enablePushNotifications.
        if (enablePush) {
            try {
                GoogleCloudMessaging gcmCloudMessaging = GoogleCloudMessaging.getInstance(chatterBoxService.getBaseContext());
                String registrationID = gcmCloudMessaging.register(Constants.PROJECT_ID); //SENDER_ID
                Log.d(Constants.LOGT, "enabling push for: " + registrationID);
                chatterBoxService.getPubNub().enablePushNotificationsOnChannels(channels, registrationID);

                //PAYLOAD
                /**
                 *  { gcm: data : { message: "some  data" } }
                 *  { gcm: data : { message: "some  data" } }
                 *  { cm{ //content },  gcm: data : { message: "some  data" } }
                 */

            } catch (IOException e) {
                Log.e(Constants.LOGT, "exception while attempting to register for push notifications", e);
            }
        }

        return false;
    }


    public HashMap<String, UserProfile> getGlobalPresenceList() {
        return (chatterBoxService.getGlobalPresenceCache());
    }

}