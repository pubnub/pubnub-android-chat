package com.pubnub.chatterbox.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPrivateChatRequest;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultChatterBoxCallback;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;


public class ChatterBoxRoomFragment extends Fragment {


    private RoomHost mListener;
    private UserProfile currentUserProfile;
    private ChatterBoxClient chatterBoxServiceClient;
    private ChatterBoxMessageSendFragment chatterBoxMessageSendFragment;
    private ChatterBoxMessageFragment chatterBoxMessageListFragment;
    private String roomTitle;
    private String roomChannel;


    private DefaultChatterBoxCallback roomListener = new DefaultChatterBoxCallback(){
        @Override
        public void onPresence(ChatterBoxPresenceMessage pmessage) {

        }

        @Override
        public void onHeartBeat(boolean error) {

        }

        @Override
        public void onError(String e) {

        }

        @Override
        public void onPrivateChatRequest(ChatterBoxPrivateChatRequest request) {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onConnect() {

        }

    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOGT, "connecting to service");
            chatterBoxServiceClient = (ChatterBoxClient) service;
            if(chatterBoxServiceClient.isConnected() == false){
                chatterBoxServiceClient.connect(currentUserProfile);
            }
            chatterBoxServiceClient.addRoom(roomChannel,roomListener);
            mListener.connectedToRoom(roomTitle,roomChannel);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };



    public void setCurrentUserProfile(UserProfile userProfile) {
        this.currentUserProfile = userProfile;
    }


    private void setChatterBoxMessageSendFragment(ChatterBoxMessageSendFragment sendFragment){
        this.chatterBoxMessageSendFragment = sendFragment;
    }

    private void setChatterBoxMessageFragment(ChatterBoxMessageFragment fragment){
        this.chatterBoxMessageListFragment = fragment;
    }

    private void setRoomTitle(String roomTitle){
        this.roomTitle = roomTitle;
    }

    private void setRoomChannel(String roomChannel){
       this.roomChannel = roomChannel;
    }


    private void configureRoom() {
        //Load up the Message View
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.message_display_fragment_container, chatterBoxMessageListFragment);
        fragmentTransaction.replace(R.id.message_input_fragment_container, chatterBoxMessageSendFragment);
        fragmentTransaction.commit();
    }


    private void configureWhoIsOnLine() {
       mListener.connectedToRoom(this.roomTitle,this.roomChannel);
    }


    public static ChatterBoxRoomFragment newInstance(UserProfile userProfile, String roomChannel, String roomTitle) {
        ChatterBoxRoomFragment fragment = new ChatterBoxRoomFragment();
        fragment.setCurrentUserProfile(userProfile);
        fragment.setRoomTitle(roomTitle);
        fragment.setRoomChannel(roomChannel);
        fragment.setChatterBoxMessageFragment(ChatterBoxMessageFragment.newInstance(userProfile, roomChannel));
        fragment.setChatterBoxMessageSendFragment(ChatterBoxMessageSendFragment.newInstance(userProfile,roomChannel));
        return fragment;
    }

    public ChatterBoxRoomFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatter_box_room, container, false);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RoomHost) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RoomHost");
        }

        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        configureRoom();
        configureWhoIsOnLine();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        chatterBoxServiceClient.removeRoomListener(roomChannel, roomListener);
        mListener.disconnectingFromRoom(roomChannel);
        mListener = null;

    }


}
