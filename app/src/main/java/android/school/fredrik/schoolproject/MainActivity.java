package android.school.fredrik.schoolproject;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.drafts.Draft_10;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    // Serveradressen från strings.xml
    String serverAdress;
    // Request Que
    RequestQueue queue;
    // For counting number of requests
    long requests;

    // Websocket connection.
    WSClient c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiera serveradressen. 
        serverAdress = getResources().getString(R.string.server_address);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // Web socket setup!
        // Kollar ifall man kör i en emulator.
        if ("google_sdk".equals( Build.PRODUCT )) {
            // Stänger av IPv6 pg a problem med det i emulator.
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        // Koppla upp till websocket server.
        // Borde nog dra ut denna i någon sorts tjänst istället. <--------- Kolla up detta.
        try{
            c = new WSClient( new URI( getResources().getString(R.string.websocket_adress) ), new Draft_10() );
            c.connect();
        }catch (URISyntaxException ex){
            System.out.println(ex.getMessage());
        }

    }

    public void makeRequest(View view){

        final TextView myText = (TextView) findViewById(R.id.myText);

        // Set url
        String url = serverAdress + "android";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        myText.setText("Response is: "+ response.substring(0,5));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myText.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        requests++;
        System.out.println("Create request method executed.");
        System.out.println(requests);

        TextView requests = (TextView) findViewById(R.id.requests);
        requests.setText(String.valueOf(this.requests));

        c.send("Android");

    }

    public void createPerson(View view) throws Exception { // Fixa exception handling.

        String url = serverAdress + "createperson";

        final JSONObject jsonBody = new JSONObject().put("name", "Rajken").put("age", 51);

        // Request a string response from the provided URL.
        // Functional syntax
        // Plugin makes this possible with android.
        // Much more concise in my opinion.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                url,
                jsonBody,
                (response) -> System.out.println(response),
                (error) -> System.out.println(error.getMessage()));


        // Add the request to the RequestQueue.
        queue.add(jsonRequest);

        System.out.println("Create person method executed.");

    }
}



// Old code for non functional syntax.

// Request a string response from the provided URL.
// Using anonymous class
        /*
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Shit: " + error.getMessage());
            }
        });
        */