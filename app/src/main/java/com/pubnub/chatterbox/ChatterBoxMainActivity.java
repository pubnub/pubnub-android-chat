package com.pubnub.chatterbox;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pubnub.chatterbox.entity.Room;
import com.pubnub.chatterbox.entity.UserProfile;
import com.pubnub.chatterbox.service.ChatService;
import com.pubnub.chatterbox.service.PushNotificationListenerService;
import com.pubnub.chatterbox.service.RegistrationIntentService;
import com.pubnub.chatterbox.service.client.ChatServiceClient;
import com.pubnub.chatterbox.ui.SessionMediator;
import com.pubnub.chatterbox.ui.fragments.ChatRoomFragment;
import com.pubnub.chatterbox.ui.fragments.PresenceListFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "mainActivity")
public class ChatterBoxMainActivity extends AppCompatActivity  {

    @Getter
    private ChatServiceClient chatServiceClient;
    @Getter
    private PresenceListFragment presenceListFragment;

    @Getter
    @Setter
    private HashMap<String, Room> currentRooms = new HashMap<>();

    @Bind(R.id.mainDrawerLayout)
    DrawerLayout mDrawLayout;

    @Bind(R.id.application_toolbar)
    Toolbar mToolBar;

    @Bind(R.id.whos_online_fragment_container)
    FrameLayout mDrawFragmentLayout;






    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatServiceClient = (ChatServiceClient) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.trace("service disconnecting");
            chatServiceClient = null;
        }
    };


    private boolean checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, Constants.REQUEST_PERMISSIONS_READ_CONTACTS);
            return true;
        }

        return false;
    }

    public void changeRooms(@NonNull  Room room) {


        presenceListFragment = PresenceListFragment.newInstance(room);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.whos_online_fragment_container, presenceListFragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(room.getTitle());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        checkForPermissions();
        setContentView(R.layout.activity_pubnub_main);
        ButterKnife.bind(this);



        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawLayout.addDrawerListener(mDrawerToggle);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDrawLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        if (null == SessionMediator.getInstance().getUserProfile()) {
            startActivityForResult(new Intent(this, LoginActivity.class), Constants.SIGN_IN_REQUEST, null);
        }

        Intent intent = new Intent(this, ChatService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatterbox_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_view_history:
                log.debug("view history appbar option selected");
                break;
            case 16908332:
                log.debug("navigation indicator clicked, this seems to be the only way to capture this");
                //mDrawLayout.openDrawer(mDrawFragmentLayout); //This seems to work just fine for the drawer
                mDrawLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_whosonline:
                mDrawLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_leave_room:
                getChatServiceClient().logout();
                break;
            default:
                log.debug("option was selected with id: " + item.getItemId() + " name: " + item.getTitle());
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == Constants.SIGN_IN_REQUEST) {
            if (responseCode == Activity.RESULT_OK) {
                UserProfile upr =
                        (UserProfile) intent.getExtras().getSerializable(Constants.CURRENT_USER_PROFILE);
                SessionMediator.getInstance().setUserProfile(upr);
                getChatServiceClient().setUserProfile(upr);
                addRoom(Constants.MAIN_CHAT_ROOM, "Main");


                //Alternative way
                //Get a token
                Intent ri = new Intent(this, RegistrationIntentService.class);

                //Refresh a token
                //Intent dtr = new Intent(this, RegistrationIntentService.class);

                //startService(ri);
                //startService(dtr);
                final Context ctx = this;


                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        InstanceID instanceID = InstanceID.getInstance(ctx);
                        String token = null;
                        try {
                            token = instanceID.getToken(BuildConfig.GCM_PROJECT_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            configurePush(token);
                        } catch (IOException e) {
                            //NOTE: handle this error better, this is a demo
                            log.error("io exception attempting to get token");
                        }
                    }
                };
                t.start();




            }
        }
    }

    private void addRoom(String roomName, String roomTitle) {
        //Load up the Message View
        Room room = new Room();
        room.setName(roomName);
        room.setTitle(roomTitle);
        room.setActive(true);



        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChatRoomFragment roomFragment = ChatRoomFragment.newInstance(room, chatServiceClient);
        fragmentTransaction.replace(R.id.room_fragment_container, roomFragment);
        fragmentTransaction.commit();
    }

    private void configurePush(String token){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String currentToken = sharedPreferences.getString("currentToken", "");

        if(currentToken.equals(token) == false) {
            sharedPreferences.edit().putBoolean("tokenSent", true);
            sharedPreferences.edit().putString("currentToken",token);
            sharedPreferences.edit().commit();
            getChatServiceClient().enablePushNotification(token);

        }
    }
}


