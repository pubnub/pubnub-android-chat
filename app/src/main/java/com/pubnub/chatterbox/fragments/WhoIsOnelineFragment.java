package com.pubnub.chatterbox.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultChatterBoxCallback;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.ArrayList;


public class WhoIsOnelineFragment extends ListFragment {

    private ArrayList<UserProfile> whosOnline = new ArrayList<>();
    private WhoIsOnlineArrayAdapter mWhosOnlineArrayAdapter;
    private ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;
    private String roomChannel;
    private String roomTitle;


    public void setRoomChannel(String roomChannel) {
        this.roomChannel = roomChannel;
    }

    public void setCurrentUserProfile(UserProfile currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }




    //CALLBACK FOR PRESENCE
    private DefaultChatterBoxCallback presenceListener = new DefaultChatterBoxCallback() {

        @Override
        public void onPresence(final ChatterBoxPresenceMessage pmessage) {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.LOGT, "Presence event triggered");
                        mWhosOnlineArrayAdapter.add(pmessage.getTargetProfile());
                    }
                });


            } catch (Exception e) {
                Log.e(Constants.LOGT, "Exception while executing presence callback");
            }

        }

    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxClient) service;
            if (chatterBoxServiceClient.isConnected()) {
                chatterBoxServiceClient.presence(roomChannel, presenceListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WhoIsOnelineFragment() {
    }


    public static WhoIsOnelineFragment newInstance(UserProfile userProfile, String roomChannel, String roomTitle) {
        WhoIsOnelineFragment fragment = new WhoIsOnelineFragment();
        fragment.setCurrentUserProfile(userProfile);
        fragment.setRoomChannel(roomChannel);
        fragment.setRoomTitle(roomTitle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWhosOnlineArrayAdapter = new WhoIsOnlineArrayAdapter(getActivity(), whosOnline);
        setListAdapter(mWhosOnlineArrayAdapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (serviceConnection != null) {
            getActivity().unbindService(serviceConnection);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


    }


}
