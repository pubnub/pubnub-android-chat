package com.pubnub.chatterbox.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import butterknife.ButterKnife;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rx.Observer;


@Slf4j
public class ChatRoomFragment extends BaseChatterboxFragment implements Observer<ChatMessage> {

    @Setter
    MessageSendFragment messageSendFragment;
    @Setter
    MessageListFragment messageListFragment;

    @Override
    public void onNext(ChatMessage chatMessage) {
        log.debug("received a chat message");
    }

    @Override
    public void onError(Throwable e) {
        log.error("throwable caught");
    }

    @Override
    public void onCompleted() {
        log.debug("on completed");

    }

    private void configureRoom() {
        //Load up the Message View
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.message_display_fragment_container, messageListFragment);
        fragmentTransaction.replace(R.id.message_input_fragment_container, messageSendFragment);
        fragmentTransaction.commit();

        getChatServiceClient().joinRoom(Constants.MAIN_CHAT_ROOM, createListener());
    }

    public static ChatRoomFragment newInstance(Room room, ChatServiceClient client) {

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setChatServiceClient(client);
        fragment.setRoom(room);
        fragment.setMessageListFragment(MessageListFragment.newInstance(room, client));
        fragment.setMessageSendFragment(MessageSendFragment.newInstance(room, client));
        return fragment;
    }

    @Override
    public Observer<ChatMessage> createListener() {
        log.debug("creating default listener");
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_chatter_box_room, container, false);
            ButterKnife.bind(this,view);
            configureRoom();
            return view;
    }




}
