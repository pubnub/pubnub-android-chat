package com.pubnub.chatterbox.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.DefaultChatRoomEventListener;

import butterknife.ButterKnife;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ChatRoomFragment extends BaseChatterBoxFragment {

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
    }

    public static ChatRoomFragment newInstance(Room room) {
        ChatRoomFragment fragment = new ChatRoomFragment();

        fragment.setRoom(room);
        fragment.setChatterBoxMessageListFragment(MessageListFragment.newInstance(room));
        fragment.setChatterBoxMessageSendFragment(MessageSendFragment.newInstance(room));
        return fragment;
    }

    @Override
    public ChatRoomEventListener createListener() {
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
            return view;
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        configureRoom();
    }


}
