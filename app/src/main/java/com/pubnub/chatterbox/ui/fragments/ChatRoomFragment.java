package com.pubnub.chatterbox.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.DefaultChatRoomEventListener;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ChatRoomFragment extends BaseChatterboxFragment {

    @Setter
    MessageSendFragment chatterBoxMessageSendFragment;
    @Setter
    MessageListFragment chatterBoxMessageListFragment;



    private void configureRoom() {
        //Load up the Message View
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.message_display_fragment_container, chatterBoxMessageListFragment);
        fragmentTransaction.replace(R.id.message_input_fragment_container, chatterBoxMessageSendFragment);
        fragmentTransaction.commit();

        getChatServiceClient().joinRoom(Constants.MAIN_CHAT_ROOM, createListener());
    }

    public static ChatRoomFragment newInstance(Room room, ChatServiceClient client) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setChatServiceClient(client);
        fragment.setRoom(room);
        fragment.setChatterBoxMessageListFragment(MessageListFragment.newInstance(room, client));
        fragment.setChatterBoxMessageSendFragment(MessageSendFragment.newInstance(room, client));
        return fragment;
    }

    @Override
    public ChatRoomEventListener createListener() {
        log.debug("creating default listener");
        return new DefaultChatRoomEventListener();
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
