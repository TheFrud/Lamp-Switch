package android.school.fredrik.schoolproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * XXX
 * Implemented as Singleton.
 * @author Fredrik Johansson
 */
public class User {

    private int userId;
    private String userName;
    private String userPassword;

    private boolean stateChanged = true;


    private InternetChecker internetChecker = new InternetChecker();

    private static final User INSTANCE = new User();

    // LOG TAG
    private static final String TAG = User.class.getSimpleName();

    private User(){
        // Immediately checks if internet is available. (see class InternetChecker how it is implemented)
        internetChecker.checkInternetConnection();
    }

    public static User getINSTANCE() {
        return INSTANCE;
    }

    public int getUserId(Context context) {
        if(stateChanged){
            if(internetChecker.isInternetAvailable()){
                Log.d(TAG, "Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                Log.d(TAG, "Internet NOT available: Reading from local storage...");
                String userIdFromFile = UserFileUtil.readFromFile("userId", context);
                if(userIdFromFile != null){
                    userId = Integer.parseInt(userIdFromFile);
                }
            }
        }
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName(Context context) {
        Log.d(TAG, "Trying to get user name");
        if(stateChanged){
            if(internetChecker.isInternetAvailable()){
                Log.d(TAG, "Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                Log.d(TAG, "Internet NOT available: Reading from local storage...");
                String userNameFromFile = UserFileUtil.readFromFile("userName", context);
                if(userNameFromFile != null){
                    userName = userNameFromFile;
                }
            }
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword(Context context) {
        if(stateChanged){
            if(internetChecker.isInternetAvailable()){
                Log.d(TAG, "Internet available: Asking server for info...");
                new GetUserTask(context).execute((Void) null);
            }
            else {
                Log.d(TAG, "Internet NOT available: Reading from local storage...");
                String userPasswordFromFile = UserFileUtil.readFromFile("userPassword", context);
                if(userPasswordFromFile != null){
                    userPassword = userPasswordFromFile;
                }
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
                // String msg = (String) response.get("msg");

                // Om servern svarat med statusen "Success", så betyder det att användaren kunde registreras.
                if(responseStringStatus.equals("Success")){
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
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            } catch (ExecutionException e) {
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
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

                    UserFileUtil.saveUserDataOnFile(userId, userName, userPassword, context);

                }
                else {
                    // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                    success = false;
                }
                // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                return success;
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            } catch (ExecutionException e) {
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            success = false;
            return success;
        }
    }

    public void getUsers(List users, Context context){
        new GetUsersTask(users, context).execute((Void) null);
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
                    .put("oldUserName", userName)
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
                    Log.d(TAG, "Success handler");
                    Log.d(TAG, "Profile updated...");
                    // Vi sätter vår variabel till true för att visa att uppgifterna var korrekta.
                    success = true;
                    setStateChanged(true);

                    setUserName(newUserName);
                    setUserPassword(newUserPassword);


                    UserFileUtil.saveUserDataOnFile(userId, newUserName, newUserPassword, context);
                }
                else {
                    // Vi sätter vår variabel till false för att visa att uppgifterna INTE var korrekta.
                    success = false;
                }
                // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                return success;
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            } catch (ExecutionException e) {
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            }

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
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
                        Log.d(TAG, "GetUserTask: Success handler");
                        Log.d(TAG, "GetUserTask: Executed correctly.");
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
                        Log.e(TAG, "FAIL!");
                    }
                    // Vi returnerar true eller false beroende på om inloggningen lyckades eller ej.
                    return success;
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                    success = false;
                    return success;
                } catch (ExecutionException e) {
                    Log.e(TAG, e.getMessage());
                    success = false;
                    return success;
                }

            }catch (Exception e){
                Log.e(TAG, e.getMessage());
                success = false;
                return success;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "Get user: Success.");
            } else {
                Log.d(TAG, "Get user: Failed.");
            }
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
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            try {
                // När vi fått ett svar från servern spar vi ner det i en variabel.
                JSONObject response = future.get();

                // Plocka ut sträng ifrån jsonsvaret. Vi tar värdet från attributet "status"
                String responseString = (String) response.get("status");

                // Om servern svarat med statusen "Success", så betyder det att användaren hittades.
                if (responseString.equals("Success")) {

                    // Try and get json array
                    try {
                        JSONArray jsArray = response.getJSONArray("users");

                        // Adding the users we got from the server into our list.
                        for (int i = 0; i < jsArray.length(); i++) {
                            users.add(jsArray.getJSONObject(i));
                        }

                        return true;

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return false;
                    }

                } else {
                    return false;
                }
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "Get users: Success.");
            } else {
                Log.d(TAG, "Get users: Failed.");
            }
        }

    }




}
