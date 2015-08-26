package com.pubnub.chatterbox.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultLChatterBoxCallback;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.Date;


public class ChatterBoxMessageSendFragment extends Fragment {


    private UserProfile currentUserProfile;
    private ChatterBoxClient chatterBoxServiceClient;
    private EditText mMessageEditText;
    private String roomName;


    private DefaultLChatterBoxCallback roomListener = new DefaultLChatterBoxCallback() {



        @Override
        public void onMessagePublished(String timeToken) {
            mMessageEditText.setEnabled(true);
            mMessageEditText.setText("");
        }

        @Override
        public void onError(String message) {
            Log.d(Constants.LOGT, "error while listening for message");
        }
    };


    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOGT, "connecting to service");
            chatterBoxServiceClient = (ChatterBoxClient) service;
            if(chatterBoxServiceClient.isConnected() == false){
                chatterBoxServiceClient.connect(currentUserProfile);
            }

            chatterBoxServiceClient.addRoom(roomName,roomListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };

    public ChatterBoxMessageSendFragment() {

    }

    public static ChatterBoxMessageSendFragment newInstance(UserProfile userProfile, String roomName) {
        ChatterBoxMessageSendFragment fragment = new ChatterBoxMessageSendFragment();
        fragment.setRoomName(roomName);
        fragment.setCurrentUserProfile(userProfile);
        return fragment;
    }

    public void setCurrentUserProfile(UserProfile userProfile) {
        this.currentUserProfile = userProfile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        View messageControlsView = inflater.inflate(R.layout.fragment_chatter_box_message_send, container, false);
        ImageButton btn = (ImageButton) messageControlsView.findViewById(R.id.send_a_message);
        mMessageEditText  = (EditText) messageControlsView.findViewById(R.id.message);
        mMessageEditText.setMaxLines(4);
        mMessageEditText.setMinLines(1);
        mMessageEditText.setPadding(1, 1, 1, 1);


        final String roomNameF = this.roomName;
        final EditText txtMsg = mMessageEditText;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatterBoxMessage message = ChatterBoxMessage.create();
                message.setFrom(currentUserProfile.getUserName());
                message.setDeviceTag("android");
                message.setSenderUUID(currentUserProfile.getId());
                message.setType("chattmessage");
                message.setMessageContent(txtMsg.getText().toString());
                message.setFrom(currentUserProfile.getEmail());
                message.setSentOn(new Date());

                if (chatterBoxServiceClient.isConnected()) {
                    chatterBoxServiceClient.publish(roomNameF, message);
                    txtMsg.setText("");
                }

            }
        });



        return messageControlsView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unbindService(serviceConnection);
    }



}
