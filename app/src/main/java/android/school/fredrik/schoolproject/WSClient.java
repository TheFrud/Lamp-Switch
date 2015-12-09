package android.school.fredrik.schoolproject;

/**
 * Created by Fredrik on 13-Nov-15.
 */
import android.content.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

public class WSClient extends WebSocketClient {

    private boolean connected = false;

    private static WSClient INSTANCE = null;
            // new WSClient(new URI( ().getResources().getString(R.string.websocket_endpoint) ), new Draft_10() )

    private WSClient( URI serverUri , Draft draft ) {
        super( serverUri, draft );
    }

    private WSClient( URI serverURI ) {
        super( serverURI );
    }

    public static WSClient getINSTANCE(Context context) {
        if(INSTANCE == null){
            try{
                INSTANCE = new WSClient(new URI( context.getResources().getString(R.string.websocket_endpoint) ), new Draft_10() );
            } catch(URISyntaxException ex){
                System.out.println(ex.getMessage());
            }
        }
        return INSTANCE;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "opened connection" );
        connected = true;
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "received: " + message );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    @Override
    public void send(String text) throws NotYetConnectedException {
        super.send(text);
    }


}