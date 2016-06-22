package sweeten.clayton.listapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Clayton on 6/1/2016.
 */
public class AsyncGet extends AsyncTask<String, String, String>{

    private final GetCallBack mCallback;
    String mResults;


    private final OkHttpClient client = new OkHttpClient();
    private ProgressBar mProgressBar;

    public  interface GetCallBack {
        void getFinished(String result);
    }


    public AsyncGet(Context context , ProgressBar progressBar) {
        mCallback = (GetCallBack) context;
        mProgressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       // mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.getFinished(s);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

        Request request = new Request.Builder()
                .url(params[0])
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
