package com.pubnub.chatterbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.pubnub.chatterbox.domain.UserProfile;


public class ChatterBoxMainActivity extends AppCompatActivity implements WhoIsOnelineFragment.OnFragmentInteractionListener
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private UserProfile currentUserProfile;
    private ChatterboxPresenceFragment chatterboxPresenceFragment;
    private ChatterBoxMessageSendFragment chatterBoxMessageSendFragment;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawLayout;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubnub_main);

        if (currentUserProfile == null) {
            startActivityForResult(new Intent(this, ChatterBoxLogin.class), Constants.SIGN_IN_REQUEST, savedInstanceState);
        }

    }


    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == Constants.SIGN_IN_REQUEST) {
            if (responseCode == Activity.RESULT_OK) {
                currentUserProfile = (UserProfile) intent.getExtras().getSerializable(Constants.CURRENT_USER_PROFILE);
                loadProfilePic();
                loadRooms();
            }
        }
    }

    private void loadRooms() {
        //Load up the Message View
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, ChatterBoxMessageFragment.newInstance(currentUserProfile));
        fragmentTransaction.replace(R.id.message_panal_container, ChatterBoxMessageSendFragment.newInstance(currentUserProfile));
        fragmentTransaction.commit();
    }


    public void loadWhosOnline(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, WhoIsOnelineFragment.newInstance());
        fragmentTransaction.commit();

    }


    private void loadProfilePic() {

    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.chatterbox_main, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_presence) {
            loadWhosOnline();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String id) {
            //Private Chat
    }



}
