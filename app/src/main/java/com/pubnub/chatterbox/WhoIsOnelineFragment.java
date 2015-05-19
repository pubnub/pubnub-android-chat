package com.pubnub.chatterbox;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pubnub.chatterbox.domain.ChatterBoxPresenceMessage;
import com.pubnub.chatterbox.domain.ChatterBoxPrivateChatRequest;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.DefaultLChatterBoxCallback;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class WhoIsOnelineFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ArrayList<UserProfile> whosOnline = new ArrayList<>();
    private WhoIsOnlineArrayAdapter mWhosOnlineArrayAdapter;


    private ChatterBoxService.ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;


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


    //CALLBACK FOR PRESENCE
    //The personal channel is used to send commands to an individual, such as
    //group chat and personal chat.
    private DefaultLChatterBoxCallback personalListener = new DefaultLChatterBoxCallback() {

        @Override
        public void onPresence(final ChatterBoxPresenceMessage pmessage) {
            try{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.LOGT,"Presence event triggered");
                        mWhosOnlineArrayAdapter.add(pmessage.getTargetProfile());
                    }
                });


            }catch (Exception e){
                Log.e(Constants.LOGT,"Exception while executing presence callback");
            }

        }

    };

    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WhoIsOnelineFragment() {
    }

    // TODO: Rename and change types of parameters
    public static WhoIsOnelineFragment newInstance() {
        WhoIsOnelineFragment fragment = new WhoIsOnelineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWhosOnlineArrayAdapter = new WhoIsOnlineArrayAdapter(getActivity(),whosOnline);
       setListAdapter(mWhosOnlineArrayAdapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(whosOnline.get(position).getId());
        }
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
        public void onFragmentInteraction(String id);
    }

}
