package com.pubnub.chatterbox;

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
import android.widget.TextView;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPrivateChatRequest;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultLChatterBoxCallback;

import java.util.ArrayList;

public class ChatterBoxMessageFragment extends Fragment implements AbsListView.OnItemClickListener {


    private ArrayList<ChatterBoxMessage> chatterMessageArray = new ArrayList<>();
    private ChatterBoxService.ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    //The personal channel is used to send commands to an individual, such as
    //group chat and personal chat.
    private DefaultLChatterBoxCallback personalListener = new DefaultLChatterBoxCallback() {

        @Override
        public void onPrivateChatRequest(ChatterBoxPrivateChatRequest message) {
            Log.d(Constants.LOGT, "private chat request received");
        }

    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxService.ChatterBoxClient) service;
            if (chatterBoxServiceClient.isConnected() == false) {
                chatterBoxServiceClient.connect(currentUserProfile, globalListener, personalListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChatMessageListArrayAdapter mAdapter;

    //This is the global channel listener for the app. Mostly the global channel
    //is a channel everyone subscribes to. This allows you to track presence and state
    //of users across the spectrum
    private DefaultLChatterBoxCallback globalListener = new DefaultLChatterBoxCallback() {

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


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatterBoxMessageFragment() {
    }


    public static ChatterBoxMessageFragment newInstance(UserProfile userProfile) {
        ChatterBoxMessageFragment fragment = new ChatterBoxMessageFragment();
        fragment.setCurrentUserProfile(userProfile);
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
                chatterMessageArray);


        //Bind to the ChatterBox service.

        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
