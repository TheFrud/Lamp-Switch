package android.school.fredrik.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Fredrik on 05-Dec-15.
 */
public class User {

    private int userId;
    private String userName;
    private String userPassword;

    private boolean stateChanged;

    private static final User INSTANCE = new User();

    private User(){}

    public static User getINSTANCE() {
        return INSTANCE;
    }

    public long getUserId(Context context) {
        if(stateChanged){
            new GetUserTask(context).execute((Void) null);
        }

        stateChanged = false;
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName(Context context) {
        if(stateChanged){
            new GetUserTask(context).execute((Void) null);
        }

        stateChanged = false;
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword(Context context) {
        if(stateChanged){
            new GetUserTask(context).execute((Void) null);
        }

        stateChanged = false;
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    // This way I can check if the info has changed.
    // If not I don't have to ask the server for new information.
    public boolean isStateChanged() {
        return stateChanged;
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }


    // Functionality


    public boolean register(String mEmail, String mPassword, Context context){
        boolean success;

        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.register);

        try{
            // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
            final JSONObject jsonBody = new JSONObject().put("name", mEmail).put("password", mPassword);

            // Skapar en future. Behöver blocka så att metoden inte returnerar innan jag fått tillbaka ett svar ifrån servern.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();


            // Request a string response from the provided URL.
            // Functional syntax
            // Plugin makes this possible with android.
            // Much more concise in my opinion.

            // Skapar en request som ska skickas till servern.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Lägger till request i kö. (Körs i princip direkt)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // När vi fått ett svar från servern spar vi ner det i en variabel.
                JSONObject response = future.get();

                // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                String responseString = (String) response.get("status");

                // Om servern svarat med statusen "Success", så betyder det att användaren kunde registreras.
                if(responseString.equals("Success")){
                    System.out.println("Success handler");
                    System.out.println("Registration went well.");
                    // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                    success = true;
                    setStateChanged(true);

                }
                else {
                    // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                    success = false;
                }
                // Vi returnerar true eller false beroende på om registreringen lyckades eller ej.
                return success;
            } catch (InterruptedException e) {
                success = false;
                return success;
            } catch (ExecutionException e) {
                success = false;
                return success;
            }

        }catch (Exception e){
            System.out.println("EXCEPTION 2");
            System.out.println(e.getMessage());
            success = false;
            return success;
        }
    }


    public boolean login(String mEmail, String mPassword, Context context){
        boolean success;

        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.login);

        try{
            // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
            final JSONObject jsonBody = new JSONObject().put("name", mEmail).put("password", mPassword);

            // Skapar en future. Behöver blocka så att metoden inte returnerar innan jag fått tillbaka ett svar ifrån servern.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();


            // Request a string response from the provided URL.
            // Functional syntax
            // Plugin makes this possible with android.
            // Much more concise in my opinion.

            // Skapar en request som ska skickas till servern.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Lägger till request i kö. (Körs i princip direkt)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // När vi fått ett svar från servern spar vi ner det i en variabel.
                JSONObject response = future.get();

                // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                String responseString = (String) response.get("status");

                // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                if(responseString.equals("Success")){
                    System.out.println("Success handler");
                    System.out.println("User found");
                    // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                    success = true;

                    // Get response data from server
                    int userId = (int) response.get("userId");
                    String userName = (String) response.get("userName");
                    String userPassword = (String) response.get("userPassword");

                    // Set user data so when can use it in the Android environment
                    setUserId(userId);
                    setUserName(userName);
                    setUserPassword(userPassword);
                    // TA BORT SEN
                    // stateChanged = true;

                    System.out.println("Values: " + userId + " " + userName + " " + userPassword);

                }
                else {
                    // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                    success = false;
                }
                // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                return success;
            } catch (InterruptedException e) {
                success = false;
                return success;
            } catch (ExecutionException e) {
                success = false;
                return success;
            }

        }catch (Exception e){
            System.out.println("EXCEPTION 2");
            System.out.println(e.getMessage());
            success = false;
            return success;
        }
    }

    public List<JSONObject> getUsers(List users, Context context){
        // REST setup
        // Flytta denna sen... Testar nu bara att få in listan i front end

        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getusers);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                (response -> {
                    System.out.println("Success handler.");
                    // Try and get json array
                    try{
                        JSONArray jsArray = response.getJSONArray("users");

                        // Loop through jsonarray
                        for(int i = 0; i < jsArray.length(); i++) {
                            users.add(jsArray.getJSONObject(i));
                        }

                    } catch(JSONException jE){
                        System.out.println(jE.getMessage());
                    }
                }),
                (error -> {
                    System.out.println("Failure handler.");
                    System.out.println(error.getMessage());
                })
        );

        RESTClient.getInstance(context).addToRequestQueue(request);

        System.out.println("HIT DÅ?");
        return users;
    }

    public boolean saveProfileSettings(String name, String password, Context context){

        boolean success;

        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.save_profile_settings);

        try{
            // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
            final JSONObject jsonBody = new JSONObject()
                    .put("userId", getUserId(context))
                    .put("newUserName", name)
                    .put("newUserPassword", password
                    );

            // Skapar en future. Behöver blocka så att metoden inte returnerar innan jag fått tillbaka ett svar ifrån servern.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();


            // Request a string response from the provided URL.
            // Functional syntax
            // Plugin makes this possible with android.
            // Much more concise in my opinion.

            // Skapar en request som ska skickas till servern.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Lägger till request i kö. (Körs i princip direkt)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // När vi fått ett svar från servern spar vi ner det i en variabel.
                JSONObject response = future.get();

                // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                String responseString = (String) response.get("status");

                // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                if(responseString.equals("Success")){
                    System.out.println("Success handler");
                    System.out.println("Profile updated...");
                    // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                    success = true;
                    setStateChanged(true);
                }
                else {
                    // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                    success = false;
                }
                // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                return success;
            } catch (InterruptedException e) {
                success = false;
                return success;
            } catch (ExecutionException e) {
                success = false;
                return success;
            }

        }catch (Exception e){
            System.out.println("EXCEPTION 2");
            System.out.println(e.getMessage());
            success = false;
            return success;
        }

    }




    public class GetUserTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;

        private boolean success;

        GetUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // return user.login(mEmail, mPassword, LoginActivity.this);
            boolean success;

            String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getuser);

            try{
                // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
                final JSONObject jsonBody = new JSONObject().put("userId", userId);

                // Skapar en future. Behöver blocka så att metoden inte returnerar innan jag fått tillbaka ett svar ifrån servern.
                RequestFuture<JSONObject> future = RequestFuture.newFuture();


                // Request a string response from the provided URL.
                // Functional syntax
                // Plugin makes this possible with android.
                // Much more concise in my opinion.

                // Skapar en request som ska skickas till servern.
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

                // Lägger till request i kö. (Körs i princip direkt)
                RESTClient.getInstance(context).addToRequestQueue(request);

                try {
                    // När vi fått ett svar från servern spar vi ner det i en variabel.

                    JSONObject response = future.get();

                    // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                    String responseString = (String) response.get("status");

                    // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                    if(responseString.equals("Success")){
                        System.out.println("GetUserTask: Success handler");
                        System.out.println("GetUserTask: Executed correctly.");
                        // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                        success = true;

                        // Get response data from server
                        int userId = (int) response.get("userId");
                        String userName = (String) response.get("userName");
                        String userPassword = (String) response.get("userPassword");

                        // Set user data so when can use it in the Android environment
                        setUserId(userId);
                        setUserName(userName);
                        setUserPassword(userPassword);

                    }
                    else {
                        // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                        success = false;
                        System.out.println("FAIL!");
                    }
                    // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                    return success;
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    success = false;
                    return success;
                } catch (ExecutionException e) {
                    System.out.println(e.getMessage());
                    success = false;
                    return success;
                }

            }catch (Exception e){
                System.out.println("EXCEPTION 2");
                System.out.println(e.getMessage());
                success = false;
                return success;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                System.out.println("Went well");
            } else {
                System.out.println("Went bad.");
            }
        }

        @Override
        protected void onCancelled() {

        }










/*
        public boolean getUser(Context context){
            boolean success;

            String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getuser);

            try{
                // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
                final JSONObject jsonBody = new JSONObject().put("userId", userId);

                // Skapar en future. Behöver blocka så att metoden inte returnerar innan jag fått tillbaka ett svar ifrån servern.
                RequestFuture<JSONObject> future = RequestFuture.newFuture();


                // Request a string response from the provided URL.
                // Functional syntax
                // Plugin makes this possible with android.
                // Much more concise in my opinion.

                // Skapar en request som ska skickas till servern.
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

                // Lägger till request i kö. (Körs i princip direkt)
                RESTClient.getInstance(context).addToRequestQueue(request);

                try {
                    // När vi fått ett svar från servern spar vi ner det i en variabel.

                    JSONObject response = future.get();
                    System.out.println("KÅRKA");

                    // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                    String responseString = (String) response.get("status");

                    // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                    if(responseString.equals("Success")){
                        System.out.println("Success handler");
                        System.out.println("User found");
                        // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                        success = true;

                        // Get response data from server
                        int userId = (int) response.get("userId");
                        String userName = (String) response.get("userName");
                        String userPassword = (String) response.get("userPassword");

                        // Set user data so when can use it in the Android environment
                        setUserId(userId);
                        setUserName(userName);
                        setUserPassword(userPassword);

                    }
                    else {
                        // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                        success = false;
                        System.out.println("FAIL!");
                    }
                    // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                    return success;
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    success = false;
                    return success;
                } catch (ExecutionException e) {
                    System.out.println(e.getMessage());
                    success = false;
                    return success;
                }

            }catch (Exception e){
                System.out.println("EXCEPTION 2");
                System.out.println(e.getMessage());
                success = false;
                return success;
            }
        }
*/

    }














}
