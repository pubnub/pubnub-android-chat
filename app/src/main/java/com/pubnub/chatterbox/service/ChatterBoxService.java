package com.pubnub.chatterbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatterBoxService extends Service {

    //put these in shared prefs
    private static final String subscribe_key = "sub-c-d6277024-f7fd-11e4-9214-0693d8625082";
    private static final String publish_key = "pub-c-fa2bc426-8f12-44c1-ad3d-ae4eb46da69d";

    /**
     * GCM Project ID Number, also refered to as "sender-ID"
     */
    private static final String PROJECT_ID = "347849282940";


    /**
     * Single instance of PubNub
     */
    private Pubnub pubnub;

    /**
     * some internal state to manage the service interaction with the UI,
     */
    private final Map<String, List<ChatterBoxCallback>> listeners = new HashMap<>();
    private final HashMap<String, UserProfile> globalPresenceCache = new HashMap<>();

    /**
     * Heartbeat monitor
     */
    private final Callback heartBeatCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.d(Constants.LOGT, "heartbeat callback" + message.toString());
        }


    };



    /**
     * A few state flags
     */
    private boolean initialized = false;
    private boolean connected = false;
    private boolean heartBeatStatus;


    public ChatterBoxService() {
    }

    @Override
    public void onCreate() {
        initialized = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        ChatterBoxClient chatterBoxClient = new ChatterBoxClient();

        return chatterBoxClient;
    }

    public class ChatterBoxClient extends Binder {

        ChatterBoxClient getService() {
            return ChatterBoxClient.this;
        }

        public void publish(String channel, ChatterBoxMessage message){
           try {
               JSONObject messageJSON = new JSONObject();
               messageJSON.put(ChatterBoxMessage.DEVICETAG, message.getDeviceTag());
               messageJSON.put(ChatterBoxMessage.SENDERUUID, pubnub.getUUID()); //Set the uuid
               messageJSON.put(ChatterBoxMessage.EMOTICON, "");
               messageJSON.put(ChatterBoxMessage.FROM, message.getFrom());
               messageJSON.put(ChatterBoxMessage.SENTON, new Date());
               messageJSON.put(ChatterBoxMessage.TYPE,message.getType());
               messageJSON.put(ChatterBoxMessage.MESSAGECONTENT,message.getMessageContent());


               pubnub.publish(channel, messageJSON, true, new Callback() {
                   @Override
                   public void successCallback(String channel, Object message) {
                      Log.d(Constants.LOGT, "successful publish");
                   }
               });
           }catch(Exception e){

           }
        }


        public void history(String channel){

            long startTime = new Date().getTime() * 1000;
            long endTime = new Date().getTime() * 1000;

            pubnub.history(channel, true, 50, new Callback(){

                @Override
                public void successCallback(String channel, Object message) {
                    Log.d(Constants.LOGT, "successful history call");

                }


            });
        }


        public void addRoom(final String roomName, final ChatterBoxCallback listener) {
            if (initialized) {
                boolean bfound = false;
                String[] currentChannels = pubnub.getSubscribedChannelsArray();
                for (String c : currentChannels) {
                    if (c.equals(roomName) == true) {
                        bfound = true;
                        break;
                    }
                }

                if (!bfound) { //no one subscription to this room, add one
                    try {
                        pubnub.subscribe(roomName, new Callback() {
                            @Override
                            public void successCallback(String channel, Object message, String timetoken) {
                                try {
                                    Log.d(Constants.LOGT + "-MSGCB", "received message on channel: " + channel);
                                    if (message instanceof JSONObject) {
                                        JSONObject jmessage = (JSONObject) message;
                                        String messageType = jmessage.getString(ChatterBoxMessage.TYPE);
                                        if (messageType.equals("chattmessage")) {

                                            ChatterBoxMessage msg = ChatterBoxMessage.create(jmessage, timetoken);

                                            List<ChatterBoxCallback> thisRoomListeners = listeners.get(roomName);
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
                        if (!listeners.containsKey(roomName)) {
                            l = new ArrayList<ChatterBoxCallback>();
                        } else {
                            l = (List<ChatterBoxCallback>) listeners.get(roomName);
                        }

                        l.add(listener); //add the listener for this room.
                        listeners.put(roomName, l);

                        //Set up the Presence on this Room
                        pubnub.presence(roomName, new PresenceCallback(l, pubnub, globalPresenceCache));


                    } catch (Exception e) {
                        //TODO: handle this exception so that the app continues to function
                        Log.e(Constants.LOGT, "exception while adding subscription", e);
                        //TODO:MAYBE CALL THE CLIENTS ERROR HANDLER
                    }

                }

                List<ChatterBoxCallback> l = null;
                if (!listeners.containsKey(roomName)) {
                    l = new ArrayList<>();
                } else {
                    l = listeners.get(roomName);
                }

                l.add(listener); //add the listener for this room.
                listeners.put(roomName, l);


            }
        }

        //Leave a room. Unsubscribe from a channel. This will trigger
        //a leave event on that channel and cause a presence event to be triggered
        public void leaveRoom(String roomName) {
            pubnub.unsubscribe(roomName);
        }


        public boolean isConnected() {
            return connected;
        }


        public boolean connect(UserProfile userProfile, final ChatterBoxCallback globalListener, final ChatterBoxCallback privateListener) {

          //Initialize the single instance of pubnub.
                pubnub = new Pubnub(publish_key,
                                subscribe_key,
                                true);


            pubnub.setHeartbeat(360, heartBeatCallback);

            pubnub.setHeartbeatInterval(30);
            pubnub.setResumeOnReconnect(true);
            pubnub.setSubscribeTimeout(20000);
            pubnub.setUUID(userProfile.getEmail()); //You can set a custom UUID or let the SDK generate one for you
            initialized = true;

            //disable push
            //get history

            //Subscribe to the global chat channel
            addRoom("AWG" + Constants.GLOBAL, globalListener); //subscribe to the global room
            //Subscribe to the personal channel
            addRoom(userProfile.getEmail().replace('@', '-').replace('.', '0') + "-pers", privateListener);
            connected = true;

            return connected;
        }


        public boolean disconnect(UserProfile userProfile, boolean enablePush) {
            String[] channels = pubnub.getSubscribedChannelsArray();
            //If I want to enable push this is where I will do it. For each channel I am
            //subscribed to I will enablePushNotifications.
            if (enablePush) {
               // pubnub.enablePushNotificationsOnChannels(channels);
            }

            return false;
        }


        public HashMap<String, UserProfile> getGlobalPresenceList() {
            return (globalPresenceCache);
        }

    }
}
