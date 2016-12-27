package edu.tsinghua.iiis;

import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static edu.tsinghua.iiis.SimpleScannerActivity.TAG;

/**
 * Created by SunJc on 25/12/16.
 */
public class NetComm {

    NetComm(MainActivity callback){
        queue = Volley.newRequestQueue(callback.getBaseContext());
    }

    private static RequestQueue queue;

    public JSONObject getJSON(String url){
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        queue.add(request);

        JSONObject response = null;

        try {
            response = future.get(10, TimeUnit.SECONDS); // Blocks for at most 10 seconds.

            Log.d(TAG,response.toString());

        } catch (InterruptedException e) {
            // Exception handling
        } catch (ExecutionException e) {
            // Exception handling
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return response;
    }


}
