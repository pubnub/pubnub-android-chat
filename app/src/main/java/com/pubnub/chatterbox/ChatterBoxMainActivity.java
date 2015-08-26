package com.pubnub.chatterbox;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toolbar;

import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.fragments.ChatterBoxRoomFragment;
import com.pubnub.chatterbox.fragments.RoomHost;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.HashMap;


public class ChatterBoxMainActivity extends Activity implements RoomHost {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;
    private WhoIsOnelineFragment whoIsOnelineFragment;
    private HashMap<String,Room> currentlyHostedRooms = new HashMap<>();
    private boolean connectedToRoom = false;


    private DrawerLayout mDrawLayout;
    private Toolbar mToolBar;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOGT, "connecting to service");
            chatterBoxServiceClient = (ChatterBoxClient) service;
            chatterBoxServiceClient.connect(currentUserProfile);
            addRoom(Constants.MAIN_CHAT_ROOM, "Main");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };

    //small class to track rooms
    class Room {
        public String roomName;
        public String roomTitle;
        public boolean isActive;
    }


    @Override
    public void connectedToRoom(String roomTitle, String roomChannelForHereNow) {
        whoIsOnelineFragment = WhoIsOnelineFragment.newInstance(currentUserProfile,roomChannelForHereNow,roomTitle);
        whoIsOnelineFragment.setCurrentUserProfile(currentUserProfile);
        connectedToRoom = true;

        //could use this for history
        Room r = new Room();
        r.roomName = roomChannelForHereNow;
        r.roomTitle = roomTitle;
        r.isActive = true;
        currentlyHostedRooms.put(roomChannelForHereNow, r);

    }

    @Override
    public void disconnectingFromRoom(String roomChannelName) {
        connectedToRoom = false;
        //for now just remove the room
        currentlyHostedRooms.remove(roomChannelName);
    }



    private DrawerLayout.DrawerListener mDrawListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //upodate the title
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_pubnub_main);

        mDrawLayout = (DrawerLayout)findViewById(R.id.mainDrawerLayout);
        mDrawLayout.setDrawerListener(mDrawListener);

        mToolBar = (Toolbar)findViewById(R.id.application_toolbar);
        mToolBar.inflateMenu(R.menu.chatterbox_main);

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(Constants.LOGT, item.toString());

                if((connectedToRoom) && (item.getTitle().equals(getString(R.string.whos_on)))){
                        if(mDrawLayout.isDrawerOpen(Gravity.LEFT)){
                            mDrawLayout.closeDrawer(Gravity.LEFT);
                        }else{
                            mDrawLayout.openDrawer(Gravity.LEFT);
                        }
                }

                return true;
            }
        });

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawLayout.isDrawerOpen(Gravity.LEFT)){
                    mDrawLayout.closeDrawer(Gravity.LEFT);
                }else{
                    mDrawLayout.openDrawer(Gravity.LEFT);
                }
        }});


        //TODO: clean this up and use the login activity
        if (null == currentUserProfile) {

            currentUserProfile = new UserProfile();
            currentUserProfile.setEmail("fred@pubnub.com");
            currentUserProfile.setFirstName("Frederick");
            currentUserProfile.setLastName("Brock");
            currentUserProfile.setLocation("Vancouver");
            currentUserProfile.setId("333333333");

            //startActivityForResult(new Intent(this, ChatterBoxLogin.class), Constants.SIGN_IN_REQUEST, null);
        }


         //GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
         //String registrationID = gcm.register(Constants.PROJECT_ID);

    }


    @Override
    public void onStart(){
            super.onStart();
            Intent intent = new Intent(this, ChatterBoxService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }




    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == Constants.SIGN_IN_REQUEST) {
            if (responseCode == Activity.RESULT_OK) {
                currentUserProfile =
                   (UserProfile) intent.getExtras().getSerializable(Constants.CURRENT_USER_PROFILE);
                chatterBoxServiceClient.connect(currentUserProfile);
                addRoom(Constants.MAIN_CHAT_ROOM, "Main");
            }
        }
    }

    private void addRoom(String roomName, String roomTitle) {
        //Load up the Message View
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChatterBoxRoomFragment roomFragment = ChatterBoxRoomFragment.newInstance(currentUserProfile, roomName, roomTitle);
        fragmentTransaction.replace(R.id.room_fragment_container, roomFragment);
        fragmentTransaction.commit();
    }



}
