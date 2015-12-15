package android.school.fredrik.schoolproject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Handles the request que which are used to make the calls to the RESTful API on the server.
 * Implemented as Singleton.
 * @author Fredrik Johansson
 */
public class RESTClient {

    // The single available instance
    private static RESTClient mInstance;

    // This is where the requests will be stored.
    private RequestQueue queue;

    private static Context mCtx;

    private RESTClient(Context context) {
        mCtx = context;
        queue = getRequestQueue();

    }

    public static synchronized RESTClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RESTClient(context);
        }
        return mInstance;
    }

    /**
     * Returns the request que to the caller.
     * If it hasn't been instantiated, this will be done before returning it to the caller.
     * */
    public RequestQueue getRequestQueue() {
        if (queue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            queue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return queue;
    }

    /**
     * Adds a new request to the request que.
     * The request will be performed almost immediately.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }





}
