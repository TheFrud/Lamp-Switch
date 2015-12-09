package android.school.fredrik.schoolproject;

import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

/**
 * Created by Fredrik on 08-Dec-15.
 */
public class WSClientTest extends AndroidTestCase {

    private Context context;
    private WSClient wsClient;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        wsClient = WSClient.getINSTANCE(context);
    }

    public void deviceSetup(){
        // Web socket setup!
        // Kollar ifall man kör i en emulator.
        if ("google_sdk".equals( Build.PRODUCT )) {
            // Stänger av IPv6 pg a problem med det i emulator.
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
    }

    public void testConnection() throws InterruptedException {
        deviceSetup();
        wsClient.connect();

        // Wait a bit for network
        Thread.sleep(3000);

        boolean connected = wsClient.isConnected();
        assertTrue(connected);
    }


}
