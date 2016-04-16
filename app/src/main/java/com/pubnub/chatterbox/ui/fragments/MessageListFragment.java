package com.pubnub.chatterbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.pubnub.chatterbox.ChatMessageListArrayAdapter;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.ChatMessage;
import com.pubnub.chatterbox.domain.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.DefaultChatRoomEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageListFragment extends BaseChatterBoxFragment {

    private ArrayList<ChatMessage> chatterMessageArray = new ArrayList<>();


    @Bind(android.R.id.list)
    AbsListView mListView;

    private ChatMessageListArrayAdapter mAdapter;

    @Override
    public ChatRoomEventListener createListener() {
        return  new DefaultChatRoomEventListener() {
            @Override
            public void messageReceived(ChatMessage message) {
                log.info("received a message");
                final ChatMessage fmsg = message;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add(fmsg);
                    }
                });
            }

            @Override
            public void errorReceived(String message) {
                log.info("error while listening for message");
            }
        };
    }

    public MessageListFragment() {
        super();
    }

    public static MessageListFragment newInstance(Room room) {
        MessageListFragment fragment = new MessageListFragment();
        fragment.setRoom(room);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ChatMessageListArrayAdapter(getActivity()
                                                   ,R.layout.chat_message_item
                                                   ,chatterMessageArray);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chattmessage_list, container, false);
        ButterKnife.bind(this, view);
        mListView.setAdapter(mAdapter);
        return view;
    }


}
