package com.pubnub.chatterbox.service;

import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.UserProfile;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Frederick on 5/14/15.
 */
public class PresenceCallback extends Callback {

    private List<ChatterBoxCallback> cblist;
    //You would want to order these MRU/LRU massive rooms would die
    private HashMap<String, UserProfile> globalPresenceList;
    private Pubnub pubnub;

    //TODO: clean up the PubNub reference passing...don't like it.
    public PresenceCallback(List<ChatterBoxCallback> listeners, Pubnub pubnub, HashMap<String, UserProfile> globalPresenceList) {
        this.cblist = listeners;
        this.globalPresenceList = globalPresenceList;
        this.pubnub = pubnub;
    }




    @Override
    public void successCallback(String channel, Object message) {
        Log.d(Constants.LOGT, "successCallback for presence");
        try {
            ChatterBoxPresenceMessage presenceMessage = new ChatterBoxPresenceMessage();
            if (message instanceof JSONObject) {
                final JSONObject messageJSON = (JSONObject) message;
                if(!messageJSON.has("action")){
                    Log.d(Constants.LOGT, "The presence payload has no value because its a status message");
                    return;
                }
                Log.d(Constants.LOGT, messageJSON.toString());

                String action = messageJSON.getString("action");
                String uuid = messageJSON.getString("uuid");
                String timeStamp = messageJSON.getString("timestamp");
                Integer occupancyCount = messageJSON.getInt("occupancy");

                UserProfile targetProfile = null;
                presenceMessage.setActionType(action);
                presenceMessage.setTimeToken(timeStamp);
                presenceMessage.setOccupancyCount(occupancyCount);
                presenceMessage.setUuid(uuid);

                if (action.equals("join")) {

                    targetProfile = new UserProfile();
                    targetProfile.setId(messageJSON.getString("uuid"));

                    globalPresenceList.put(uuid, targetProfile);
                    //Get the state of the user...don't rely on state
                    //being a part of the join event.

                    if (!messageJSON.has("data")) {
                        pubnub.getState(channel, uuid, this);
                    }

                } else if ((action.equals("state-change") == true) || (messageJSON.has("data"))) {
                    //assuming you already have a profile for this user
                    targetProfile = globalPresenceList.get(uuid);
                    JSONObject stateData = messageJSON.getJSONObject("data");
                    String userName = stateData.getString("userName");
                    String firstName = stateData.getString("firstName");
                    String lastName = stateData.getString("lastName");
                    String email = stateData.getString("email");
                    String imageURL = stateData.getString("imageURL");
                    String location = stateData.getString("location");

                    targetProfile.setUserName(userName);
                    targetProfile.setImageURL(imageURL);
                    targetProfile.setLastName(lastName);
                    targetProfile.setFirstName(firstName);
                    targetProfile.setEmail(email);
                    targetProfile.setLocation(location);
                    globalPresenceList.put(uuid, targetProfile);

                } else if (action.equals("leave") || action.equals("timeout")) {
                    targetProfile = globalPresenceList.remove(uuid);
                }

                presenceMessage.setTargetProfile(targetProfile);
                for (ChatterBoxCallback cb : cblist) {
                    cb.onPresence(presenceMessage);
                }

            }

        } catch (Exception e) {
            Log.e(Constants.LOGT, "exception while processing presence event", e);
        }
    }

    @Override
    public void errorCallback(String channel, PubnubError error) {
        super.errorCallback(channel, error);
    }
}
