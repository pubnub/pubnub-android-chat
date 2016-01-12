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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pubnub.chatterbox.ChatMessageListArrayAdapter;
import com.pubnub.chatterbox.Constants;
import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultChatterBoxCallback;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.ArrayList;

public class ChatterBoxMessageFragment extends Fragment implements AbsListView.OnItemClickListener {


    private ArrayList<ChatterBoxMessage> chatterMessageArray = new ArrayList<>();
    private ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;

    private String roomName;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private ScrollView mMessageScrollView;


    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatMessageListArrayAdapter mAdapter;

    private DefaultChatterBoxCallback roomListener = new DefaultChatterBoxCallback() {

        @Override
        public void onMessage(ChatterBoxMessage message) {
            Log.d(Constants.LOGT, "received a message");
            final ChatterBoxMessage fmsg = message;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Just add another message to the adapter.
                    mAdapter.add(fmsg);
                }
            });

        }

        @Override
        public void onError(String message) {
            Log.d(Constants.LOGT, "error while listening for message");
        }
    };



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxClient) service;
            if (chatterBoxServiceClient.isConnected() == false) {
                chatterBoxServiceClient.connect(currentUserProfile);
            }

            chatterBoxServiceClient.addRoom(roomName, roomListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatterBoxMessageFragment() {
    }


    public static ChatterBoxMessageFragment newInstance(UserProfile userProfile, String roomName) {
        ChatterBoxMessageFragment fragment = new ChatterBoxMessageFragment();
        fragment.setCurrentUserProfile(userProfile);
        fragment.setRoomName(roomName);
        return fragment;
    }

    //TODO: Build a profile Manager or AppDataUtil to store
    //instance data for the app. This is going to get messy fast
    public void setCurrentUserProfile(UserProfile profile) {
        this.currentUserProfile = profile;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        mAdapter = new ChatMessageListArrayAdapter(getActivity()
                , R.layout.chat_message_item,
                chatterMessageArray,
                currentUserProfile);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chattmessage_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
        chatterBoxServiceClient.removeRoomListener(this.roomName,roomListener);
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }




    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


}
