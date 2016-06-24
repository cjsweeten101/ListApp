package sweeten.clayton.listapp;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnSignUpSelected, LoginFragment.OnLoginSelected, SignupFragment.OnSignUp, AsyncCreate.CreateCallback {
    public static final String LIST_FRAGMENT = "list_fragment";
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        LoginFragment loginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.PlaceHolder,loginFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void SignUpSwitch() {
        SignupFragment fragment = new SignupFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.PlaceHolder,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void LogIn(String userName, String password) {
        AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
    try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("password", password);
            asyncCreate.execute("http://listaroo.herokuapp.com/api/login", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void SignUp(String UserName, String password, String ConfirmPassword, String FirstName, String LastName, String Email) {

            try {
                Log.v("PASSWORD", password);
                Log.v("PASSWORD CONFIRM",ConfirmPassword);

                AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", UserName);
                jsonObject.put("password", password);
                jsonObject.put("passwordConf", ConfirmPassword);
                jsonObject.put("first_name", FirstName);
                jsonObject.put("last_name", LastName);
                jsonObject.put("email",Email);
                asyncCreate.execute("http://listaroo.herokuapp.com/api/signup", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void createFinished(String result) {

        try {
            if (result == null) {
                Toast toast = Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_LONG);
                toast.show();
            } else {

                JSONObject jsonObjectResults = new JSONObject(result);
                Log.v("CALLBACK RESULTS", result);
                if (!(jsonObjectResults.opt("errors") == null)) {
                    String error = jsonObjectResults.optString("errors");
                   // String error = jsonObjectResults.getJSONArray("errors").getString(0);
                    Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
                    toast.show();
                } else {

                    int id = (int) jsonObjectResults.opt("id");
                    int token = jsonObjectResults.optInt("api_token");

                    Intent intent = new Intent(this, PagerActivity.class);
                    intent.putExtra("Id", id);
                    intent.putExtra("token",token);
                    startActivity(intent);


                }
            }

            }catch(JSONException e){
                e.printStackTrace();

            }

        mProgressBar.setVisibility(View.INVISIBLE);


    }
}
