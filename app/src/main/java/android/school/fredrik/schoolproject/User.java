package android.school.fredrik.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Fredrik on 05-Dec-15.
 */
public class User {

    private int userId;
    private String userName;
    private String userPassword;

    private boolean stateChanged = true;
    private boolean internetAvailable = false;

    private static final User INSTANCE = new User();


    // FLYTTA
    public boolean saveUserDataOnFile(int userId, String userName, String userPassword, Context context){
        String FILENAME = context.getResources().getString(R.string.user_info);

        FileOutputStream fos = null;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user_info.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(userId));
            outputStreamWriter.write("\n");
            outputStreamWriter.write(userName);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(userPassword);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
            System.out.println("User data saved to file.");
            return true;

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // FLYTTA
    public boolean readFromFile(Context context) {

        try {
            String FILENAME = context.getResources().getString(R.string.user_info);
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                List<String> info = new ArrayList<>();


                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    info.add(receiveString);
                }

                inputStream.close();

                // Reading data from file.
                userId = Integer.parseInt(info.get(0));
                userName = info.get(1);
                userPassword = info.get(2);

                return true;
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return false;
    }

    public boolean isInternetAvailable() {
        return internetAvailable;
    }

    private User(){new CheckInternetConnectionTask().execute((Void) null);}

    public static User getINSTANCE() {
        return INSTANCE;
    }

    public int getUserId(Context context) {
        if(stateChanged){
            if(isInternetAvailable()){
                System.out.println("Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                System.out.println("Internet NOT available: Reading from local storage...");
                readFromFile(context);
            }
        }
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName(Context context) {
        System.out.println("Trying to get user name");
        if(stateChanged){
            if(isInternetAvailable()){
                System.out.println("Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                System.out.println("Internet NOT available: Reading from local storage...");
                readFromFile(context);
            }
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword(Context context) {
        if(stateChanged){
            if(isInternetAvailable()){
                System.out.println("Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                System.out.println("Internet NOT available: Reading from local storage...");
                readFromFile(context);
            }
        }
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
                String responseStringStatus = (String) response.get("status");
                String msg = (String) response.get("msg");

                // Om servern svarat med statusen "Success", så betyder det att användaren kunde registreras.
                if(responseStringStatus.equals("Success")){
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

                    saveUserDataOnFile(userId, userName, userPassword, context);

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

    public void getUsers(List users, Context context){
        new GetUsersTask(users, context).execute((Void) null);

/*        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getusers);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        try {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, future, future);
            RESTClient.getInstance(context).addToRequestQueue(request);
        } catch (Exception ex){
        System.out.println(ex.getMessage());
        }

        try {
            // När vi fått ett svar från servern spar vi ner det i en variabel.
            JSONObject response = future.get();

            // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
            String responseString = (String) response.get("status");

            // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
            if (responseString.equals("Success")) {
                System.out.println("Success handler.");
                // Try and get json array
                try {
                    JSONArray jsArray = response.getJSONArray("users");

                    // Loop through jsonarray
                    for (int i = 0; i < jsArray.length(); i++) {
                        users.add(jsArray.getJSONObject(i));
                    }

                } catch (JSONException jE) {
                    System.out.println(jE.getMessage());
                }

            } else {
                System.out.println("Gick skräp att få användare");
            }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        return users;*/
    }

    public boolean saveProfileSettings(String newUserName, String newUserPassword, Context context){

        boolean success;

        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.save_profile_settings);

        try{

            // So that I can find the user to change on the server
            int userId = getUserId(context);

            // Note to self: Ska ändra namn till eMail i jsonobjektet!!!
            final JSONObject jsonBody = new JSONObject()
                    .put("userId", userId)
                    .put("newUserName", newUserName)
                    .put("newUserPassword", newUserPassword
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

                    saveUserDataOnFile(userId, newUserName, newUserPassword, context);
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

                        stateChanged = false;

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



    }



    public class GetUsersTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private List<JSONObject> users;

        GetUsersTask(List users, Context context) {
            this.context = context;
            this.users = users;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getusers);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            try {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, future, future);
                RESTClient.getInstance(context).addToRequestQueue(request);
            } catch (Exception ex){
                System.out.println(ex.getMessage());
            }

            try {
                // När vi fått ett svar från servern spar vi ner det i en variabel.
                JSONObject response = future.get();

                // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                String responseString = (String) response.get("status");

                // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                if (responseString.equals("Success")) {
                    System.out.println("Success handler.");
                    System.out.println("GOT USERS!");
                    // Try and get json array
                    try {
                        JSONArray jsArray = response.getJSONArray("users");

                        // Loop through jsonarray
                        for (int i = 0; i < jsArray.length(); i++) {
                            users.add(jsArray.getJSONObject(i));
                        }

                        return true;

                    } catch (JSONException jE) {
                        System.out.println(jE.getMessage());
                        return false;
                    }

                } else {
                    System.out.println("Gick skräp att få användare");
                    return false;
                }
            } catch (Exception ex){
                System.out.println(ex.getMessage());
                return false;
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



    }





    public class CheckInternetConnectionTask extends AsyncTask<Void, Void, Boolean> {

        CheckInternetConnectionTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {
                try {
                    InetAddress ipAddr = InetAddress.getByName("google.se"); //You can replace it with your name

                    if (ipAddr.equals("")) {
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    return false;
                }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                System.out.println("Went well");
                internetAvailable = true;
            } else {
                System.out.println("Went bad.");
                internetAvailable = false;
            }
        }

        @Override
        protected void onCancelled() {

        }



    }






}
