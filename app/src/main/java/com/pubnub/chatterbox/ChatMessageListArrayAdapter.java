package com.pubnub.chatterbox;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;
import com.pubnub.chatterbox.domain.UserProfile;

import java.util.List;


public class ChatMessageListArrayAdapter extends ArrayAdapter<ChatterBoxMessage> {


    private UserProfile userProfile;


    public ChatMessageListArrayAdapter(Context context, int textViewResourceId, List<ChatterBoxMessage> objects, UserProfile userProfile) {
        super(context, textViewResourceId, objects);
        this.userProfile = userProfile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatterBoxMessage message = getItem(position);
        View returnedView = null;
        if ((convertView == null) || (convertView.getId() != R.layout.chat_message_item)) {
            LayoutInflater inflator =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnedView = inflator.inflate(R.layout.chat_message_item, null);
        } else {
            returnedView = convertView;
        }

        ImageView img = (ImageView) returnedView.findViewById(R.id.avatarView);
        TextView messageText = (TextView) returnedView.findViewById(R.id.messageText);
        TextView messageSentOn = (TextView) returnedView.findViewById(R.id.messageSentOn);
        TextView messageSentBy = (TextView) returnedView.findViewById(R.id.messageSentBy);
        LinearLayout bubbleLayout = (LinearLayout) returnedView.findViewById(R.id.message_bubble);


        if (isMyMessage(message)) {
            Drawable d = returnedView.getResources().getDrawable(R.drawable.bubble_outgoing);
            bubbleLayout.setBackground(d);
        } else {
            Drawable d = returnedView.getResources().getDrawable(R.drawable.bubble_incoming);
            bubbleLayout.setBackground(d);
        }


        messageText.setText(message.getMessageContent());
        messageSentBy.setText(message.getFrom());
        messageSentOn.setText(message.getSentOn().toGMTString());


        return returnedView;
    }


    private boolean isMyMessage(ChatterBoxMessage message) {
        String myProfileID = userProfile.getEmail();
        return message.getFrom().equals(myProfileID);
    }


}
