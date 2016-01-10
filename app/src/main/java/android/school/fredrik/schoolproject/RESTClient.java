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
