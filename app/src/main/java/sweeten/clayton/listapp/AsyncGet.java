package sweeten.clayton.listapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Clayton on 6/1/2016.
 */
public class AsyncGet extends AsyncTask<Void, String, String>{

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(Void... params) {

        Request request = new Request.Builder()
                .url("http://listaroo.herokuapp.com/api/lists")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

              //Headers responseHeaders = response.headers();
              // for (int i = 0; i < responseHeaders.size(); i++) {
                 // System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
              //}
            return response.body().string();

        } catch (IOException e) {
            Log.e("OKHTTP_GET", e.getMessage());
        }
        return null;
    }
}
