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
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

/**
 * xxx
 * @author Fredrik Johansson
 * */
public class WSClient extends WebSocketClient {

    private boolean connected = false;

    private static Context context = null;

    private static WSClient INSTANCE = null;

    // LOG TAG
    private static final String TAG = WSClient.class.getSimpleName();

    private WSClient(URI serverURI) {
        super(serverURI);
        this.connected = connected;
    }

    private WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
        this.connected = connected;
    }

    public static WSClient getINSTANCE(Context context) {
        if(INSTANCE == null){
            try{
                WSClient.context = context;
                INSTANCE = new WSClient(new URI( context.getResources().getString(R.string.websocket_endpoint) ), new Draft_10() );
            } catch(URISyntaxException ex){
                Log.d(TAG, ex.getMessage());
            }
        }
        return INSTANCE;
    }

    /**
     *  xxx
     *
     * */
    public boolean isConnected() {
        return connected;
    }

    /**
     *  xxx
     *
     * */
    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        Log.d(TAG, "opened connection");
        connected = true;
        send("STATE_REQUEST");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    /**
     *  xxx
     *
     * */
    @Override
    public void onMessage( String message ) {
        Log.d(TAG, "received: " + message);
        if(message.equals("ON")){
            Log.d(TAG, "Lamp got turned on.");
            new UpdateSwitch(true).execute((Void) null);
        }
        if(message.equals("OFF")){
            Log.d(TAG, "Lamp got turned off");
            new UpdateSwitch(false).execute((Void) null);
        }
    }

    /**
     *  xxx
     *
     * */
    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        Log.d(TAG, "Connection closed by " + (remote ? "remote peer" : "us"));
        connected = false;
    }

    /**
     *  xxx
     *
     * */
    @Override
    public void onError( Exception e ) {
        Log.e(TAG, e.getMessage());
        // if the error is fatal then onClose will be called additionally
    }

    /**
     *  xxx
     *
     * */
    @Override
    public void send(String text) throws NotYetConnectedException {
        super.send(text);
    }

    /**
     *  xxx
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

            // True, if the state is ON.
            if (state) {
                final Switch lampSwitch = (Switch) ((Activity)context).findViewById(R.id.lamp_switch);
                lampSwitch.setChecked(true);
            }

            // If the state is OFF
            else {
                final Switch lampSwitch = (Switch) ((Activity)context).findViewById(R.id.lamp_switch);
                lampSwitch.setChecked(false);
            }
        }

    }

}