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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.pubnub.chatterbox.domain.Room;
import com.pubnub.chatterbox.domain.UserProfile;
import com.pubnub.chatterbox.fragments.ChatterBoxRoomFragment;
import com.pubnub.chatterbox.fragments.RoomHost;
import com.pubnub.chatterbox.fragments.WhoIsOnelineFragment;
import com.pubnub.chatterbox.service.ChatterBoxService;
import com.pubnub.chatterbox.service.binder.ChatterBoxClient;

import java.util.HashMap;
import java.util.Map;


public class ChatterBoxMainActivity extends AppCompatActivity implements RoomHost {

    private ChatterBoxClient chatterBoxServiceClient;
    private UserProfile currentUserProfile;
    private WhoIsOnelineFragment whoIsOnelineFragment;
    private HashMap<String,Room> currentlyHostedRooms = new HashMap<>();

    private String currentRoomKey;
    private boolean connectedToRoom = false;


    private DrawerLayout mDrawLayout;
    private Toolbar mToolBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout mDrawFragmentLayout;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(Constants.LOGT, "connecting to service");
            chatterBoxServiceClient = (ChatterBoxClient) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(Constants.LOGT, "disconnecting from service");
        }
    };



    @Override
    public void connectedToRoom(String roomTitle, String roomChannelForHereNow) {

        connectedToRoom = true;

        Room r = new Room();
        r.setRoomName(roomChannelForHereNow);
        r.setRoomTitle(roomTitle);
        r.setActive(true);

        currentlyHostedRooms.put(roomChannelForHereNow, r);

        whoIsOnelineFragment = WhoIsOnelineFragment.newInstance(currentUserProfile,roomChannelForHereNow,roomTitle);
        whoIsOnelineFragment.setCurrentUserProfile(currentUserProfile);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.whos_online_fragment_container, whoIsOnelineFragment);
        fragmentTransaction.commit();

        getSupportActionBar().setTitle(r.getRoomTitle());
    }

    @Override
    public void disconnectingFromRoom(String roomChannelName) {
        connectedToRoom = false;
        //for now just remove the room
        currentlyHostedRooms.remove(roomChannelName);
        chatterBoxServiceClient.leaveRoom(roomChannelName);
    }

    @Override
    public Map<String, Room> getCurrentRooms() {
        return currentlyHostedRooms;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubnub_main);

        if (null == currentUserProfile) {
            startActivityForResult(new Intent(this, ChatterBoxLogin.class), Constants.SIGN_IN_REQUEST, null);
        }

        mDrawLayout = (DrawerLayout)findViewById(R.id.mainDrawerLayout);
        mToolBar = (Toolbar)findViewById(R.id.application_toolbar);
        mDrawFragmentLayout = (FrameLayout)findViewById(R.id.whos_online_fragment_container);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawLayout,mToolBar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){

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

        mDrawLayout.setDrawerListener(mDrawerToggle);


        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDrawLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chatterbox_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case R.id.action_view_history:
                Log.d(Constants.LOGT, "view history appbar option selected");
                break;
            case 16908332:
                Log.d(Constants.LOGT, "navigation indicator clicked, this seems to be the only way to capture this");
                //mDrawLayout.openDrawer(mDrawFragmentLayout); //This seems to work just fine for the drawer
                mDrawLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_whosonline:
                mDrawLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_leave_room:
                chatterBoxServiceClient.leaveRoom(currentRoomKey);
                break;
            default:
                Log.d(Constants.LOGT, "option was selected with id: " + item.getItemId() + " name: " + item.getTitle());
            }


        return super.onOptionsItemSelected(item);
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
                currentUserProfile = (UserProfile) intent.getExtras().getSerializable(Constants.CURRENT_USER_PROFILE);
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
