package sweeten.clayton.listapp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Clayton on 6/12/2016.
 */


public class AsyncUpdate extends AsyncTask<String, String, String> {

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private ProgressBar mProgressBar;
    private UpdateCallBack mCallBack;

    public AsyncUpdate(Context context, ProgressBar progressBar) {
        mProgressBar = progressBar;
        mCallBack = (UpdateCallBack) context;
    }

    public  interface UpdateCallBack {
        void updateFinished(String result);
    }

    @Override
    protected String doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .url(params[0])
                .addHeader("Content-Type","application/json")
                .put(body)
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallBack.updateFinished(s);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
