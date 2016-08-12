package com.pubnub.chatterbox.ui.fragments;


import android.app.Fragment;
import android.content.Context;

import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rx.Observer;


@Slf4j(topic = "baseFragment")
public abstract class BaseChatterboxFragment extends Fragment {

    @Getter
    @Setter
    private ChatServiceClient chatServiceClient;


    @Getter
    @Setter
    private Room room;



    public abstract Observer<ChatMessage> createListener();

    public BaseChatterboxFragment(){
        super();
    }

    @Override
    public void onStart() {
        super.onStart();

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
        //getChatServiceClient().leaveRoom(getRoom().getName(),createListener());
    }

}

