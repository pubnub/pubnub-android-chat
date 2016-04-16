package com.pubnub.chatterbox.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.ChatMessage;
import com.pubnub.chatterbox.domain.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.DefaultChatRoomEventListener;
import com.pubnub.chatterbox.ui.SessionMediator;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MessageSendFragment extends BaseChatterBoxFragment {

    @Bind(R.id.message)
    EditText mMessageEditText;
    @Bind(R.id.send_a_message)
    ImageButton mBtnSend;


    @OnClick(R.id.send_a_message)
    void publishMessage() {
        SessionMediator sessionMediator = SessionMediator.getInstance();
        CharSequence content = mMessageEditText.getText();
        if ((content.length() == 0) || (content.equals(""))) {
            return;
        }

        ChatMessage message = ChatMessage.create();
        message.setDeviceTag("android");
        message.setSenderUUID(sessionMediator.getUserProfile().getId());
        message.setMessageContent(mMessageEditText.getText().toString());
        message.setFrom(sessionMediator.getUserProfile().getEmail());
        message.setSentOn(new Date());

        mMessageEditText.setEnabled(false);
        mBtnSend.setEnabled(false);

        getChatterBoxServiceClient().publish(getRoom().getRoomID(), message);
        mMessageEditText.setText("");
    }



    @Override
    public ChatRoomEventListener createListener() {
        return new DefaultChatRoomEventListener() {
            @Override
            public void messagePublished(String timeToken) {
                log.trace("inside: messagePublishedListener for Send fragment");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMessageEditText.setEnabled(true);
                        mBtnSend.setEnabled(true);
                        mMessageEditText.setText("");
                    }
                });
            }

        };
    }

    public static MessageSendFragment newInstance(Room room) {
        MessageSendFragment fragment = new MessageSendFragment();
        fragment.setRoom(room);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View messageControlsView = inflater.inflate(R.layout.fragment_chatter_box_message_send, container, false);
        ButterKnife.bind(this, messageControlsView);
        return messageControlsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}