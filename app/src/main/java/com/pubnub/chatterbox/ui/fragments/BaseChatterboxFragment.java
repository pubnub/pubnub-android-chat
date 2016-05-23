package com.pubnub.chatterbox.ui.fragments;


import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.ChatService;
import com.pubnub.chatterbox.service.client.ChatServiceClient;
import com.pubnub.chatterbox.ui.SessionMediator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "baseFragment")
public abstract class BaseChatterboxFragment extends Fragment {

    @Getter
    @Setter
    private ChatServiceClient chatServiceClient;


    @Getter
    @Setter
    private Room room;

    @Setter
    private ChatRoomEventListener listener;


//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            chatServiceClient = (ChatServiceClient) service;
//            chatServiceClient.setUserProfile(SessionMediator.getInstance().getUserProfile());
//            listener = createListener();
//
//            if ((getRoom().getName() != null) && (listener != null)) {
//                chatServiceClient.joinRoom(getRoom().getName(), listener);
//            } else {
//                log.error("name is null");
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            log.info("service disconnected from fragment");
//        }
//    };

    public abstract ChatRoomEventListener createListener();

    public BaseChatterboxFragment(){
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        getChatServiceClient().joinRoom(getRoom().getName(),createListener());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        log.trace("entering onAttach(BaseFragment) for {0}", "test");
        getChatServiceClient().joinRoom(getRoom().getName(),createListener());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getChatServiceClient().leaveRoom(getRoom().getName(),createListener());
    }

}

