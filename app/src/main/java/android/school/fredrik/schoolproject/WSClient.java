package android.school.fredrik.schoolproject;

/**
 * Created by Fredrik on 13-Nov-15.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Handles everything that has do to with the Web socket (connectivity, messaging, and so on).
 * Implemented as Singleton.
 * @author Fredrik Johansson
 * */
public class WSClient extends WebSocketClient {

    // Allows us to check if the web socket is connected.
    // This is important to avoid getting an exception.
    private boolean connected = false;

    private static Context context = null;

    private static WSClient INSTANCE = null;

    // LOG TAG
    private static final String TAG = WSClient.class.getSimpleName();

    private WSClient(URI serverURI) {
        super(serverURI);
    }

    private WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public static WSClient getINSTANCE(Context context) {
        WSClient.context = context;
        if(INSTANCE == null){
            try{
                INSTANCE = new WSClient(new URI( context.getResources().getString(R.string.websocket_endpoint) ), new Draft_10() );
            } catch(URISyntaxException ex){
                Log.d(TAG, ex.getMessage());
            }
        }
        return INSTANCE;
    }

    /**
     *   Allows us to check if the web socket is connected.
     *   This is important to avoid getting an exception.
     * */
    public boolean isConnected() {
        return connected;
    }

    /**
     *  Gets called when the server has accepted the connection.
     *  Makes the isConnected-method return true.
     * */
    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        Log.d(TAG, "opened connection");

        // So we can check later on, if we need to connect.
        // Will save us an exception...
        connected = true;

        // We immediately ask  the server for the current state of the lamp.
        send("STATE_REQUEST");
    }

    /**
     *  Gets called when we receive a message from the server.
     * */
    @Override
    public void onMessage( String message ) {
        Log.d(TAG, "received: " + message);

        // If the lamp state is ON
        if(message.equals("ON")){
            Log.d(TAG, "Lamp got turned on.");

            // We update the switch in the UI
            // Done async
            new UpdateSwitch(true).execute((Void) null);
        }
        // If the lamp state is OFF
        if(message.equals("OFF")){
            Log.d(TAG, "Lamp got turned off");

            // We update the switch in the UI
            // Done async
            new UpdateSwitch(false).execute((Void) null);
        }
    }

    /**
     *  Gets called when the connection is closed.
     *  Makes the isConnected-method return false.
     * */
    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        Log.d(TAG, "Connection closed by " + (remote ? "remote peer" : "us"));

        // So we can check later on, if we need to connect.
        // Trying to send messages to the server without a connection would not be pretty...
        connected = false;
    }

    /**
     *  Gets called when we get an error.
     *  The onClose method will be called if the error is fatal. (according to API-documentation)
     * */
    @Override
    public void onError( Exception e ) {
        Log.e(TAG, e.getMessage());
    }

    /**
     *  Gets called from an Activity-class.
     *  Sends the supplied argument to the server.
     *
     * */
    @Override
    public void send(String text) throws NotYetConnectedException {
        super.send(text);
    }

    /**
     *  This task is very simple.
     *  It fetches the Switch-object from UI and sets it to the correct state.
     *  In this context, the correct state is whatever state the switch is SERVER-SIDE.
     *  The task gets passed the server-side state in its constructor.
     *  An object of the class is meant to be created after receiving a message from the server.
     * @author Fredrik Johansson
     * */
    public class UpdateSwitch extends AsyncTask<Void, Void, Boolean> {

        private boolean state;

        UpdateSwitch(boolean state) {this.state = state;}


        @Override
        protected Boolean doInBackground(Void... params) {
            return state;
        }

        @Override
        protected void onPostExecute(final Boolean state) {

            Log.d(TAG, "Post executed.");

            // We fetch the Switch-object from the UI
            final Switch lampSwitch = (Switch) ((Activity)context).findViewById(R.id.lamp_switch);

            // True, if the state is ON.
            if (state) {
                // The switch in the UI gets set to checked (ON)
                Log.d(TAG, "Lamp switch set to ON.");
                lampSwitch.setChecked(true);
            }

            // If the state is OFF
            else {
                // The switch in the UI gets set to unchecked (OFF)
                Log.d(TAG, "Lamp switch set to OFF.");
                lampSwitch.setChecked(false);
            }
        }

    }

}