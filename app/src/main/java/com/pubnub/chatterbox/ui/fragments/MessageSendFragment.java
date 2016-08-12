package com.pubnub.chatterbox.ui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.ChatMessage;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.entity.StatusEvent;
import com.pubnub.chatterbox.service.client.ChatServiceClient;
import com.pubnub.chatterbox.ui.SessionMediator;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rx.functions.Func1;


@Slf4j
public class MessageSendFragment extends Fragment {

    @Bind(R.id.message)
    EditText mMessageEditText;
    @Bind(R.id.send_a_message)
    ImageButton mBtnSend;

    @Setter
    @Getter
    private ChatServiceClient chatServiceClient;

    @Getter
    @Setter
    private Room room;

    private Func1<StatusEvent,Void> statusEventCallable = new Func1<StatusEvent, Void>() {
        @Override
        public Void call(StatusEvent event)  {
            mMessageEditText.setEnabled(true);
            mBtnSend.setEnabled(true);
            mMessageEditText.setText("");

            return null;

        }
    };


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
        message.setContent(mMessageEditText.getText().toString().trim());
        message.setFrom(sessionMediator.getUserProfile().getUserName());
        message.setSentOn(new Date());
        message.setConversation(getRoom().getName());

        //mMessageEditText.setEnabled(false);
        //mBtnSend.setEnabled(false);

        getChatServiceClient().publish(getRoom().getName(), message);
        mMessageEditText.setText("");
    }



    public static MessageSendFragment newInstance(Room room, ChatServiceClient client) {
        MessageSendFragment fragment = new MessageSendFragment();
        fragment.setChatServiceClient(client);
        fragment.setRoom(room);
        client.status(fragment.statusEventCallable);
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
