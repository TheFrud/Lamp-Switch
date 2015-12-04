package android.school.fredrik.schoolproject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Fredrik on 01-Dec-15.
 */
public class RESTClient {

    private static RESTClient mInstance;
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

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            queue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }





}
