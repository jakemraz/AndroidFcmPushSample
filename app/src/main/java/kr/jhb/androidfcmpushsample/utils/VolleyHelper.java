package kr.jhb.androidfcmpushsample.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VolleyHelper {

    private static VolleyHelper instance;
    private static RequestQueue queue = null;
    private Context context;

    private VolleyHelper(Context context) {
        this.context = context;
        this.queue = getRequestQueue();

    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (this.queue == null)
            this.queue = Volley.newRequestQueue(this.context);
        return this.queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void execute(final int method,
                        final String url,
                        final JSONObject obj,
                        final Response.Listener<JSONObject> listener,
                        final int retry) {

        if (retry <= 0) {
            // err
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(method, url, obj, listener,
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    execute(method, url, obj, listener, retry - 1);
                }
            }
        );
        getInstance(this.context).addToRequestQueue(request);
    }
}
