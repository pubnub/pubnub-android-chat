package com.pubnub.chatterbox.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.pubnub.chatterbox.ChatMessageListArrayAdapter;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;
import rx.Observer;

@Slf4j
public class MessageListFragment extends BaseChatterboxFragment {

    private ArrayList<ChatMessage> chatterMessageArray = new ArrayList<>();


    @Bind(android.R.id.list)
    AbsListView mListView;

    private ChatMessageListArrayAdapter mAdapter;

    @Override
    public Observer<ChatMessage> createListener() {

        return new Observer<ChatMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                log.debug("error received from Observable");
            }

            @Override
            public void onNext(ChatMessage chatMessage) {
                final ChatMessage fmsg = chatMessage;
                getActivity().runOnUiThread(new Runnable() {
                    public void run(){
                       mAdapter.add(fmsg);;
                    };
                });
            }
        };
    };


    public MessageListFragment() {
        super();
    }

    public static MessageListFragment newInstance(Room room, ChatServiceClient client) {
        MessageListFragment fragment = new MessageListFragment();
        fragment.setChatServiceClient(client);
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
