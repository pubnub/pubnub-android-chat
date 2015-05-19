package com.pubnub.chatterbox;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;


public class ChatterBoxMessageSendFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private UserProfile currentUserProfile;
    private ChatterBoxService.ChatterBoxClient chatterBoxServiceClient;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatterBoxServiceClient = (ChatterBoxService.ChatterBoxClient) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };

    public static ChatterBoxMessageSendFragment newInstance(UserProfile userProfile) {
        ChatterBoxMessageSendFragment fragment = new ChatterBoxMessageSendFragment();
        fragment.setCurrentUserProfile(userProfile);
        return fragment;
    }


    public void setCurrentUserProfile(UserProfile userProfile){
        this.currentUserProfile = userProfile;
    }


    public ChatterBoxMessageSendFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bind to the service
        Intent chatterBoxServiceIntent = new Intent(getActivity(), ChatterBoxService.class);
        getActivity().bindService(chatterBoxServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View messageControlsView = inflater.inflate(R.layout.fragment_chatter_box_message_send, container, false);
        Button btn = (Button)messageControlsView.findViewById(R.id.sendMessageButton);
        final TextView txtMsg = (TextView)messageControlsView.findViewById(R.id.message);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatterBoxMessage message = ChatterBoxMessage.create();
                message.setFrom(currentUserProfile.getUserName());
                message.setDeviceTag("android");
                message.setSenderUUID(currentUserProfile.getId());
                message.setType("chattmessage");
                message.setMessageContent(txtMsg.getText().toString());

                chatterBoxServiceClient.publish("AWG-global", message);

            }
        });


        return messageControlsView;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
