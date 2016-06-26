package sweeten.clayton.listapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnSignUpSelected, LoginFragment.OnLoginSelected, SignupFragment.OnSignUp, AsyncCreate.CreateCallback {
    public static final String LIST_FRAGMENT = "list_fragment";
    private ProgressBar mProgressBar;
    private String mUserName;
    private String mToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPref.contains("USERNAME")) {
          //Get the preferneces...

            mUserName = sharedPref.getString("USERNAME", "FUCK");
            mToken = sharedPref.getString("TOKEN", "");
            int id = sharedPref.getInt("ID", 0 );

            Intent intent = new Intent(this, PagerActivity.class);
            intent.putExtra("Id", id );
            intent.putExtra("token",mToken);
            startActivity(intent);

        }

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
            mUserName = userName;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("password", password);
            asyncCreate.execute("http://listaroo.herokuapp.com/api/login", jsonObject.toString(), 0 + "",0+ "");
            mProgressBar.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void SignUp(String UserName, String password, String ConfirmPassword, String FirstName, String LastName, String Email) {

            try {
                mUserName = UserName;

                AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", UserName);
                jsonObject.put("password", password);
                jsonObject.put("passwordConf", ConfirmPassword);
                jsonObject.put("first_name", FirstName);
                jsonObject.put("last_name", LastName);
                jsonObject.put("email",Email);
                asyncCreate.execute("http://listaroo.herokuapp.com/api/signup", jsonObject.toString(), 0 + "", 0 + "");
                mProgressBar.setVisibility(View.VISIBLE);

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
                if (!(jsonObjectResults.opt("errors") == null)) {
                    String error = jsonObjectResults.optString("errors");
                   // String error = jsonObjectResults.getJSONArray("errors").getString(0);
                    Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
                    toast.show();
                } else {

                    int id = (int) jsonObjectResults.opt("id");
                    String token = jsonObjectResults.optString("api_token");

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("USERNAME", mUserName);
                    editor.putString("TOKEN", token);
                    editor.putInt("ID", id);
                    editor.commit();



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
