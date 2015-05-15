package com.pubnub.chatterbox;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pubnub.chatterbox.domain.ChatterBoxMessage;

import java.util.List;


/**
 * Created by Frederick on 5/12/15.
 */
public class ChatMessageListArrayAdapter extends ArrayAdapter<ChatterBoxMessage> {


    public ChatMessageListArrayAdapter(Context context, int textViewResourceId,
                                       List<ChatterBoxMessage> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View returnedView = null;
        if ((convertView == null) || (convertView.getId() != R.layout.chat_message_item)) {
            LayoutInflater inflator =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnedView = inflator.inflate(R.layout.chat_message_item, null);
        }

        ImageView img = (ImageView) returnedView.findViewById(R.id.avatarView);
        TextView messageText = (TextView) returnedView.findViewById(R.id.messageText);
        TextView messageSentOn = (TextView) returnedView.findViewById(R.id.messageSentOn);
        TextView messageSentBy = (TextView) returnedView.findViewById(R.id.messageSentBy);


        ChatterBoxMessage message = getItem(position);


        messageText.setText(message.getMessageContent());
        messageSentBy.setText(message.getFrom());
        messageSentOn.setText(message.getSentOn().toGMTString());


        return returnedView;
    }


}
