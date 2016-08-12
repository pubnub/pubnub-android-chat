package com.pubnub.chatterbox.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.client.ChatServiceClient;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rx.functions.Func1;


@Slf4j(topic = "PresenceListFragment")
public class PresenceListFragment extends Fragment {

    @Getter
    private ArrayList<UserProfile> presentUserProfiles = new ArrayList<>();

    private PresenceListArrayAdapter presenceListArrayAdapter;

    @Setter
    private ChatServiceClient chatServiceClient;

    @Setter
    private Room room;


    @Bind(android.R.id.list)
    AbsListView presenceListView;


    private Func1<PresenceMessage, Void> presenceHandler = new Func1<PresenceMessage, Void>() {
        @Override
        public Void call(PresenceMessage presenceMessage) {
            try {
                log.trace("Presence event triggered for:\n {0}", presenceMessage);
                UserProfile targetProfile = null;
                for (UserProfile userProfile : presentUserProfiles) {
                    if (userProfile.getId().equals(presenceMessage.getUuid())) {
                        targetProfile = userProfile;
                        break;
                    }
                }

                if ((!presenceMessage.getActionType().equals("leave")) ||
                        (presenceMessage.getActionType().equals("timeout"))) {
                    presenceListArrayAdapter.remove(targetProfile);
                } else if (!presenceMessage.getActionType().equals("join")) {

                }


                presenceListArrayAdapter.add(targetProfile);


            } catch (Exception e) {
                log.error("Exception while executing presence callback", e);
            }

            return null;
        }
    };



    public static PresenceListFragment newInstance(Room room, ChatServiceClient client) {
        PresenceListFragment fragment = new PresenceListFragment();
        fragment.setChatServiceClient(client);
        fragment.setRoom(room);
        client.presenceObserved(fragment.presenceHandler);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presencelist, container, false);
        ButterKnife.bind(this, view);
        presenceListArrayAdapter = new PresenceListArrayAdapter(getActivity(), presentUserProfiles);
        presenceListView.setAdapter(presenceListArrayAdapter);
        return view;
    }


}