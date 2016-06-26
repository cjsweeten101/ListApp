package sweeten.clayton.listapp;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Clayton on 6/7/2016.
 */
public class AsyncDelete extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... params) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(params[0])
                .delete(null)
                .addHeader("content-type", "application/json")
                .addHeader("useridaroo",params[1])
                .addHeader("autharoo-token",params[2])
                .build();

        String response = "";
        try {
            response = client.newCall(request).execute().toString();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
