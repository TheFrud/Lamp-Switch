package android.school.fredrik.schoolproject;

import android.content.Intent;
import android.os.Build;
import android.school.fredrik.schoolproject.dummy.DummyContent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;


/**
 * Class description
 * @author Fredrik Johansson
 */
public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    // Websocket-client.
    private WSClient c = null;

    // LOG TAG
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Creates the activity, sets up web socket connection and lamp state.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Start of onCreate method.");

        /*Web socket setup!
        Checks if using emulator.*/
        if ("google_sdk".equals( Build.PRODUCT )) {
            // Turns off IPv6 due to it having problems in emulators.
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        // Initializing web socket instance.
        c = WSClient.getINSTANCE(this);

        // Checking if the web socket instance already is connected to the server
        if(!c.isConnected()){
            // Tries to connect to server.
            Log.d(TAG, "Web socket instance: NOT connected to server.");
            Log.d(TAG, "Connecting to server...");
            c.connect();
        } else {
            // Already connected
            Log.d(TAG, "Web socket instance: IS Connected to server.");

            // Asks server for lamp state
            Log.d(TAG, "Asking server for lamp state...");
            c.send("STATE_REQUEST");
        }
        Log.d(TAG, "End of onCreate method.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Sends message to server
        This will remove this user from the receiver-list on the server.*/
        c.send("LEAVE");
        Log.d(TAG, "Sent message to server, removing user from message receivers.");
    }

/** Called from view. Navigates user to ProfileActivity.*/
    public void navigateToProfileActivity(View view) {
        Intent intent = new Intent(MainActivity.this.getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        finish();
    }

/** Called from view. Switches lamp state.*/
    public void switchLampState(View view){
        final Switch lampSwitch = (Switch) findViewById(R.id.lamp_switch);

        // Checks the state of the switch the user clicked on.
        if(lampSwitch.isChecked()){
            // Sends message to server. Server will tell all receivers that the lamp has been turned ON.
            c.send("ON");
            Log.d(TAG, "I switched lamp state to on");
        } else {
            // Sends message to server. Server will tell all receivers that the lamp has been turned OFF.
            c.send("OFF");
            Log.d(TAG, "I switched lamp state to off");
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "Interaction!!! Item " + item.content);
    }


    /** Inflates the menu so that the user can navigate to the ProfileActivity.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    /** Gets called when the user clicked a menu item. Depending on the menu item, different actions happen.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Checks which menu item the user clicked on.
        switch (item.getItemId()) {
            // If the user clicked to navigate to the ProfileActivity.
            case R.id.navigate_to_profile_activity:
                // Navigates to the ProfileActivity.
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;
            // Default.
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


