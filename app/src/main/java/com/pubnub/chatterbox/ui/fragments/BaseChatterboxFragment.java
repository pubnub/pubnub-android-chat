package com.pubnub.chatterbox.ui.fragments;


import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pubnub.chatterbox.domain.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.ChatService;
import com.pubnub.chatterbox.service.client.ChatServiceClient;
import com.pubnub.chatterbox.ui.SessionMediator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "baseFragment")
public abstract class BaseChatterBoxFragment extends Fragment {

    @Getter
    private ChatServiceClient chatterBoxServiceClient;


    @Getter
    @Setter
    private Room room;

    @Setter
    private ChatRoomEventListener listener;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatServiceClient) service;
            chatterBoxServiceClient.setUserProfile(SessionMediator.getInstance().getUserProfile());

            if ((getRoom().getRoomID() != null) && (listener != null)) {
                chatterBoxServiceClient.joinRoom(getRoom().getRoomID(), listener);
            } else {
                log.error("name is null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.info("service disconnected from fragment");
        }
    };

    public abstract ChatRoomEventListener createListener();

    public BaseChatterBoxFragment(){
        super();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        log.trace("entering onAttach(BaseFragment) for {0}", "test");
        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        chatterBoxServiceClient.leaveRoom(room.getRoomID(), listener);
        getActivity().unbindService(serviceConnection);
    }

}

