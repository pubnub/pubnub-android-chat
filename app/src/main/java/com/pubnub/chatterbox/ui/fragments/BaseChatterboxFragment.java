package com.pubnub.chatterbox.ui.fragments;


import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pubnub.chatterbox.domain.ChatterBoxUserProfile;
import com.pubnub.chatterbox.service.ChatterBoxEventListener;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.client.ChatterBoxServiceClient;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "baseFragment")
public class BaseChatterBoxFragment extends Fragment {

    @Getter
    private ChatterBoxServiceClient chatterBoxServiceClient;

    @Getter
    @Setter
    private ChatterBoxUserProfile userProfile;

    @Getter
    @Setter
    private String roomName;

    @Setter
    private ChatterBoxEventListener listener;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxServiceClient) service;

            chatterBoxServiceClient.connect(userProfile);

            if ((roomName != null) && (listener != null)) {
                chatterBoxServiceClient.joinRoom(roomName, listener);
            } else {
                log.error("roomName is null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.info("service disconnected from fragment");
        }
    };

    public BaseChatterBoxFragment(){
        super();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chatterBoxServiceClient.leaveRoom(this.roomName, listener);
        getActivity().unbindService(serviceConnection);
    }

}

