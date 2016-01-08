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
