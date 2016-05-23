package com.pubnub.chatterbox;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GcmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


//        {
//            "pn_gcm": {
//            "data": {
//                        "title": "You have been mentioned"
//                        ,"message": "You have been mentioned"
//                        ,"conversation": "AWG-global"
//            }
//        },
//            "pn_debug": true
//        }


        log.debug("your push notification logic goes here");
        CharSequence title = intent.getExtras().getCharSequence("title");
        CharSequence message = intent.getExtras().getCharSequence("message");
        CharSequence conversation = intent.getExtras().getCharSequence("conversation");


        sendNotification(context,message,title);

    }

    private void sendNotification(Context context, CharSequence title, CharSequence message) {
        Intent intent = new Intent(context, ChatterBoxMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
