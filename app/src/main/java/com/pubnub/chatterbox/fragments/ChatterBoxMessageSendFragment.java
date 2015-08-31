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
import com.pubnub.chatterbox.service.DefaultChatterBoxCallback;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.Date;


public class ChatterBoxMessageSendFragment extends Fragment {


    private UserProfile currentUserProfile;
    private ChatterBoxClient chatterBoxServiceClient;
    private EditText mMessageEditText;
    private ImageButton mBtnSend;
    private String roomName;

    private DefaultChatterBoxCallback roomListener = new DefaultChatterBoxCallback() {
        @Override
        public void onMessagePublished(String timeToken) {
            Log.d(Constants.LOGT, "inside: onMessagePublished for Send fragment");
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
        mBtnSend = (ImageButton) messageControlsView.findViewById(R.id.send_a_message);
        mMessageEditText  = (EditText) messageControlsView.findViewById(R.id.message);


        final String roomNameF = this.roomName;
        final EditText txtMsg = mMessageEditText;
        final ImageButton btn = mBtnSend;

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence content = txtMsg.getText();
                if((content.length() == 0) || (content.equals(""))){
                    return;
                }

                ChatterBoxMessage message = ChatterBoxMessage.create();
                message.setDeviceTag("android");
                message.setSenderUUID(currentUserProfile.getId());
                message.setType(ChatterBoxMessage.CHATTMESSAGE);
                message.setMessageContent(txtMsg.getText().toString());
                message.setFrom(currentUserProfile.getEmail());
                message.setSentOn(new Date());

                txtMsg.setEnabled(false);
                btn.setEnabled(false);

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
