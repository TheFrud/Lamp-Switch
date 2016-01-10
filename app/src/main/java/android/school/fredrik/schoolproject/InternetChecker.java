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

import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;

/**
 * Checks if internet is available.
 * @author Fredrik Johansson
 * */
public class InternetChecker {

    private boolean internetAvailable = true;

    // LOG TAG
    private static final String TAG = InternetChecker.class.getSimpleName();

    public boolean isInternetAvailable() {
        return internetAvailable;
    }

    /**
     * Checks the internet connection
     */
    public void checkInternetConnection(){
        new CheckInternetConnectionTask().execute((Void) null);
    }


    /**
     * Asyncronous task for checking the internet connection.
     * @author Fredrik Johansson
     * */
    public class CheckInternetConnectionTask extends AsyncTask<Void, Void, Boolean> {

        CheckInternetConnectionTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.d(TAG, "Checking internet connection...");
                Log.d(TAG, "Calling google.se...");
                InetAddress ipAddr = InetAddress.getByName("google.se");

                if (ipAddr.equals("")) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "Internet connection: Available.");
                internetAvailable = true;
            } else {
                Log.d(TAG, "Internet connection: NOT available.");
                internetAvailable = false;
            }
        }

    }
}
