package sweeten.clayton.listapp;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnSignUpSelected, LoginFragment.OnLoginSelected, SignupFragment.OnSignUp {
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
            String results = asyncCreate.execute("http://listaroo.herokuapp.com/api/login", jsonObject.toString()).get();
            JSONObject jsonObjectResults = new JSONObject(results);
             Log.v("LOGIN_RESULTS", results);
            if((results.length()<1) || !(jsonObjectResults.opt("errors")==null)) {
                Toast toast = Toast.makeText(this, "Incorrect Login or Password", Toast.LENGTH_SHORT);
                toast.show();
            } else {

                int id = (int) jsonObjectResults.opt("id");

                Intent intent = new Intent(this, PagerActivity.class);
                intent.putExtra("Id",id);
                startActivity(intent);


           }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void SignUp(String UserName, String password, String ConfirmPassword, String FirstName, String LastName, String Email) {
        if (UserName.trim().length()==0  && password.trim().length() == 0){

        }
        else if(UserName.trim().length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter a user name", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (password.trim().length()==0) {
            Toast toast = Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (!(password.equals(ConfirmPassword))) {
            Toast toast = Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (FirstName.trim().length()==0 || LastName.trim().length()==0) {
            Toast toast = Toast.makeText(this, "Please enter first and last name", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (Email.trim().length()==0) {
            Toast toast = Toast.makeText(this, "Please enter an Email", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            try {
                AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", UserName);
                jsonObject.put("password", password);
                jsonObject.put("password_confirmation", ConfirmPassword);
                jsonObject.put("first_name", FirstName);
                jsonObject.put("last_name", LastName);
                jsonObject.put("email",Email);
                asyncCreate.execute("http://listaroo.herokuapp.com/api/signup", jsonObject.toString());

                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
