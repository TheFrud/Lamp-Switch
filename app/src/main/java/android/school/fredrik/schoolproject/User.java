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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 *
 * Responsible for:
 * <li> Storing user data locally </li>
 * <li> Fetching user data from server </li>
 * <li> Saving updated user data on server </li>
 * <li> Login functionality </li>
 * <li> Register functionality </li>
 * Implemented as Singleton.
 * Makes use of asynchronous tasks.
 * @author Fredrik Johansson
 */
public class User {

    // Locally saved user data
    private int userId;
    private String userName;
    private String userPassword;

    // Keep tracks of changes to user data (which warrants a trip to the server)
    private boolean stateChanged = false;

    // To check for internet access
    private InternetChecker internetChecker = new InternetChecker();

    // The only instance of the class (singleton)
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

    /**
     * Returns the current User ID.
     * @param context
     * @return
     */
    public int getUserId(Context context) {
        // Checks if user data has changed
        if(stateChanged){
            // Checks if internet is available
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

    /**
     * Returns the current User name.
     * @param context
     * @return
     */
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

    /**
     * Returns the current User password.
     * @param context
     * @return
     */
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


    /**
     * Handles the registration of a new user.
     * @param mEmail
     * @param mPassword
     * @param context
     * @return
     */
    public boolean register(String mEmail, String mPassword, Context context){

        // Keeps track of the success of the registration
        boolean success;

        // Server URL
        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.register);

        try{

            // Creates a jsonobject into which we put the user data we want to register.
            // The attribute name is "name" instead off "email".
            // I haven't changed this for fear of breaking some server code.
            // Coupling between client and server = BAD (I KNOW)
            final JSONObject jsonBody = new JSONObject().put("name", mEmail).put("password", mPassword);

            // Creates a future. Need to block so that the method don't return before the server has responded.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            // Creates a request to be sent to the server.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Adds the request to the que. (executed almost instantly according to api spec)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // When the server has responded we save the response in a variable
                JSONObject response = future.get();

                // Gets the "status" attribute from the response.
                // This is the servers way of telling us if the action succeeded.
                String responseStringStatus = (String) response.get("status");

                // Checks if the registration succeeded
                if(responseStringStatus.equals("Success")){

                    // We set the variable to true to show that the registration was successful.
                    success = true;

                }

                else {

                    // We set the variable to false to show that the registration was unsuccessful.
                    success = false;
                }

                // We return true or false depending on the success of the registration
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

    /**
     * Handles the login functionality.
     * @param mEmail
     * @param mPassword
     * @param context
     * @return
     */
    public boolean login(String mEmail, String mPassword, Context context){

        // Keeps track of the success of the login
        boolean success;

        // Server URL
        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.login);

        try{
            // Creates a jsonobject into which we put the user data we want to login with.
            // The attribute name is "name" instead off "email".
            // I haven't changed this for fear of breaking some server code.
            // Coupling between client and server = BAD (I KNOW)
            final JSONObject jsonBody = new JSONObject().put("name", mEmail).put("password", mPassword);

            // Creates a future. Need to block so that the method don't return before the server has responded.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            // Creates a request to be sent to the server.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Adds the request to the que. (executed almost instantly according to api spec)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // When the server has responded we save the response in a variable
                JSONObject response = future.get();

                // Gets the "status" attribute from the response.
                // This is the servers way of telling us if the action succeeded.
                String responseString = (String) response.get("status");

                // Checks if the login succeeded
                if(responseString.equals("Success")){

                    // We set the variable to true to show that the login was successful.
                    success = true;

                    // Get response data from server
                    int userId = (int) response.get("userId");
                    String userName = (String) response.get("userName");
                    String userPassword = (String) response.get("userPassword");

                    // Set user data instance variables
                    setUserId(userId);
                    setUserName(userName);
                    setUserPassword(userPassword);

                    // Saves user data to file (can then be used locally without internet access)
                    UserFileUtil.saveUserDataOnFile(userId, userName, userPassword, context);

                }
                else {

                    // We set the variable to false to show that the login was unsuccessful.
                    success = false;
                }

                // We return true or false depending on the success of the login
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

    /**
     * Handles the updating of user data.
     * @param newUserName
     * @param newUserPassword
     * @param context
     * @return
     */
    public boolean saveProfileSettings(String newUserName, String newUserPassword, Context context){

        // Keeps track of the success of the update
        boolean success;

        // Server URL
        String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.save_profile_settings);

        try{

            // So that I can find the user to change on the server
            int userId = getUserId(context);

            // Creates a jsonobject into which we put the user data we need for update.
            // The attribute names are "name" instead off "email".
            // I haven't changed this for fear of breaking some server code.
            // Coupling between client and server = BAD (I KNOW)
            final JSONObject jsonBody = new JSONObject()
                    .put("userId", userId)
                    .put("oldUserName", userName)
                    .put("newUserName", newUserName)
                    .put("newUserPassword", newUserPassword
                    );

            // Creates a future. Need to block so that the method don't return before the server has responded.
            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            // Creates a request to be sent to the server.
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

            // Adds the request to the que. (executed almost instantly according to api spec)
            RESTClient.getInstance(context).addToRequestQueue(request);

            try {
                // When the server has responded we save the response in a variable
                JSONObject response = future.get();

                // Gets the "status" attribute from the response.
                // This is the servers way of telling us if the action succeeded.
                String responseString = (String) response.get("status");

                // Checks if the update succeeded
                if(responseString.equals("Success")){
                    Log.d(TAG, "Success handler");
                    Log.d(TAG, "Profile updated...");

                    // We set the variable to true to show that the update was successful.
                    success = true;

                    // The update succeded, so we want to show that the server state has changed
                    setStateChanged(true);

                    // Set user data instance variables
                    setUserName(newUserName);
                    setUserPassword(newUserPassword);

                    // Saves user data to file (can then be used locally without internet access)
                    UserFileUtil.saveUserDataOnFile(userId, newUserName, newUserPassword, context);
                }
                else {
                    // We set the variable to false to show that the update was unsuccessful.
                    success = false;
                }

                // We return true or false depending on the success of the update
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

    /**
     * This task is fetching the current user data from the server.
     * After this is done it sets the local values to those retrieved from the server.
     */
    public class GetUserTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;

        // Keeps track of the success of the task
        private boolean success;

        GetUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean success;

            String url = context.getResources().getString(R.string.server_address) + context.getResources().getString(R.string.getuser);

            try{
                // Creates a jsonobject into which we put the user id for the user whose info we want to get from the server
                final JSONObject jsonBody = new JSONObject().put("userId", userId);

                // Creates a future. Need to block so that the method don't return before the server has responded.
                RequestFuture<JSONObject> future = RequestFuture.newFuture();

                // Creates a request to be sent to the server.
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);

                // Adds the request to the que. (executed almost instantly according to api spec)
                RESTClient.getInstance(context).addToRequestQueue(request);

                try {

                    // When the server has responded we save the response in a variable
                    JSONObject response = future.get();

                    // Gets the "status" attribute from the response.
                    // This is the servers way of telling us if the action succeeded.
                    String responseString = (String) response.get("status");

                    // Checks if the server could find the user
                    if(responseString.equals("Success")){

                        Log.d(TAG, "GetUserTask: Success handler");
                        Log.d(TAG, "GetUserTask: Executed correctly.");

                        // We set the variable to true to show that the user could be found
                        success = true;

                        // Get response data from server
                        int userId = (int) response.get("userId");
                        String userName = (String) response.get("userName");
                        String userPassword = (String) response.get("userPassword");

                        // Set user data instance variables
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

            // We log if the task succeded or not
            if (success) {
                Log.d(TAG, "Get user: Success.");
            } else {
                Log.d(TAG, "Get user: Failed.");
            }
        }

    }




}
