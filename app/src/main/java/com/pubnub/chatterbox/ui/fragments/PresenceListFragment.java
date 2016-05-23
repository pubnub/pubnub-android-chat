package com.pubnub.chatterbox.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.entity.PresenceMessage;
import com.pubnub.chatterbox.entity.UserProfile;

import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.service.ChatRoomEventListener;
import com.pubnub.chatterbox.service.DefaultChatRoomEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j(topic = "PresenceListFragment")
public class PresenceListFragment extends BaseChatterboxFragment {

    @Getter
    private ArrayList<UserProfile> presentUserProfiles = new ArrayList<>();
    private PresenceListArrayAdapter presenceListArrayAdapter;


    @Bind(android.R.id.list)
    AbsListView presenceListView;

    @Override
    public ChatRoomEventListener createListener() {
        return new DefaultChatRoomEventListener() {

            @Override
            public void presenceEventReceived(final PresenceMessage pmessage) {
                try {
                    log.trace("Presence event triggered for:\n {0}", pmessage);
                    UserProfile targetProfile = null;
                    for (UserProfile userProfile : presentUserProfiles) {
                        if (userProfile.getId().equals(pmessage.getUuid())) {
                            targetProfile = userProfile;
                            break;
                        }
                    }

                    if ((!pmessage.getActionType().equals("leave")) ||
                            (pmessage.getActionType().equals("timeout"))) {
                        presenceListArrayAdapter.remove(targetProfile);
                    } else if (!pmessage.getActionType().equals("join")) {

                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //presenceListArrayAdapter.add(pmessage.getTargetProfile());
                        }
                    });

                } catch (Exception e) {
                    log.error("Exception while executing presence callback", e);
                }
            }
        };
    }



    public static PresenceListFragment newInstance(Room room) {
        PresenceListFragment fragment = new PresenceListFragment();
        fragment.setRoom(room);
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