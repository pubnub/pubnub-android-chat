package com.pubnub.chatterbox.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.pubnub.chatterbox.ChatterBoxMainActivity;
import com.pubnub.chatterbox.R;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "pushNotificationListenerService")
public class PushNotificationListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);
        String conversation = data.getString("conversation");
        //String message = data.get("message");

        sendNotification("You got mentioned", "You got mentioned in room " + conversation);
    }


    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, ChatterBoxMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
