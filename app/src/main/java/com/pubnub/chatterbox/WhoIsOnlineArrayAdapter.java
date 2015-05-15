package com.pubnub.chatterbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pubnub.chatterbox.domain.UserProfile;

import java.util.List;

/**
 * Created by Frederick on 5/15/15.
 */
public class WhoIsOnlineArrayAdapter extends ArrayAdapter<UserProfile> {

    public WhoIsOnlineArrayAdapter(Context context,
                                   List<UserProfile> objects) {
        super(context, R.layout.whos_online_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View returnedView = null;
        if ((convertView == null) || (convertView.getId() != R.layout.whos_online_item)) {
            LayoutInflater inflator =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnedView = inflator.inflate(R.layout.whos_online_item, null);
        }


        return returnedView;
    }
}
