package android.school.fredrik.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.school.fredrik.schoolproject.dummy.DummyContent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    // For counting number of requests
    long requests;

    private User user = User.getINSTANCE();

    // Websocket connection.
    WSClient c;

    List<JSONObject> jsonObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Kommer hit?");

        jsonObjects = user.getUsers(jsonObjects,this);
        System.out.println(user.getUserName(this));

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
            c = new WSClient( new URI( getResources().getString(R.string.websocket_endpoint) ), new Draft_10() );
            c.connect();
        }catch (URISyntaxException ex){
            System.out.println(ex.getMessage());
        }

    }

    public void navigateToProfileActivity(View view) {
        Intent intent = new Intent(MainActivity.this.getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void makeRequest(View view){

        final TextView myText = (TextView) findViewById(R.id.myText);

        // Set url
        String url = getResources().getString(R.string.server_address) + getResources().getString(R.string.websocket_messaging);

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
        RESTClient.getInstance(this).addToRequestQueue(stringRequest);

        requests++;
        System.out.println("Create request method executed.");
        System.out.println(requests);

        TextView requests = (TextView) findViewById(R.id.requests);
        requests.setText(String.valueOf(this.requests));

        c.send("Android");

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        System.out.println("Interaction!!! Item " + item.content);
    }



    // MENU STUFF

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.navigate_to_profile_activity:
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


