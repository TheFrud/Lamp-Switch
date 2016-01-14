/*
Copyright 2016 Fredrik Johansson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package android.school.fredrik.schoolproject;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;


/**
 * Handles the main functionality of the application (toggle lamp state).
 * Also provides navigation to the profile view.
 * @author Fredrik Johansson
 */
public class MainActivity extends AppCompatActivity {

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
        This will remove this user from the receiver-list on the server.
        Will not recieve messages anymore.
        */
        String leaveRecipientList = this.getResources().getString(R.string.leave_recipient_list);

        // Checks if the web socket is connected. It crashes if we try to send without a connection.
        if(c.isConnected()){

            // OBS: Denna kod visade sig vara överflödig men spar koden så länge.
            // c.send(leaveRecipientList);
            // Log.d(TAG, "Sent message to server, removing user from message receivers.");
        } else {
            Log.d(TAG, "Trying to send without Web socket connection.");
        }
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
            String messageOn = this.getResources().getString(R.string.lamp_state_message_on);

            // Checks if the web socket is connected. It crashes if we try to send without a connection.
            if(c.isConnected()){
                c.send(messageOn);
                Log.d(TAG, "I switched lamp state to on (Sent to server)");
            } else {
                Log.d(TAG, "Trying to send without Web socket connection.");
            }


        } else {
            // Sends message to server. Server will tell all receivers that the lamp has been turned OFF.
            String messageOff = this.getResources().getString(R.string.lamp_state_message_off);

            // Checks if the web socket is connected. It crashes if we try to send without a connection.
            if(c.isConnected()){
                c.send(messageOff);
                Log.d(TAG, "I switched lamp state to off (Sent to server)");
            } else {
                Log.d(TAG, "Trying to send without Web socket connection.");
            }

        }
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


