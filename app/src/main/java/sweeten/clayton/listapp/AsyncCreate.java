package sweeten.clayton.listapp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Clayton on 6/7/2016.
 */
public class AsyncCreate extends AsyncTask<String, String, String> {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected String doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .url(params[0])
                .addHeader("Content-Type","application/json")
                .post(body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
