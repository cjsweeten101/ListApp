package sweeten.clayton.listapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

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

    private final CreateCallback mCallback;
    private Context mContext;
    private ProgressBar mProgressBar;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public interface CreateCallback {
       void createFinished(String result);
    }

    public AsyncCreate(Context context, ProgressBar progressBar) {
        mContext = context;
        mProgressBar = progressBar;
        CreateCallback createCallback = (CreateCallback) context;
        mCallback = createCallback;
    }


    @Override
    protected String doInBackground(String... params) {

        RequestBody body = RequestBody.create(JSON, params[1]);
        Request request = new Request.Builder()
                .url(params[0])
                .addHeader("Content-Type","application/json")
                .addHeader("useridaroo",params[2])
                .addHeader("autharoo-token",params[3])
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     //   mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.createFinished(s);
      //  mProgressBar.setVisibility(View.INVISIBLE);
    }
}
