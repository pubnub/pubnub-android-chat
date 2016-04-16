package com.pubnub.chatterbox.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pubnub.chatterbox.R;
import com.pubnub.chatterbox.domain.UserProfile;

import java.util.List;

public class PresenceListArrayAdapter extends ArrayAdapter<UserProfile> {

    public PresenceListArrayAdapter(Context context,
                                    List<UserProfile> objects) {
        super(context, R.layout.whos_online_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View returnedView = convertView;
        if ((convertView == null) || (convertView.getId() != R.layout.whos_online_item)) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnedView = inflater.inflate(R.layout.whos_online_item, parent,false);
        }

        TextView txtUserName = (TextView) returnedView.findViewById(R.id.username);
        TextView txtFormatted = (TextView) returnedView.findViewById(R.id.formattedName);

        UserProfile p = getItem(position);
        String formattedName = p.getLastName() + ", " + p.getFirstName();
        txtFormatted.setText(formattedName);
        txtUserName.setText(p.getUserName());

        return returnedView;
    }
}
