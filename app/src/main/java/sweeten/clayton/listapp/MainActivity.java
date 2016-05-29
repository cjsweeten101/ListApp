package sweeten.clayton.listapp;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnSignUpSelected, LoginFragment.OnLoginSelected, SignupFragment.OnSignUp {
    public static final String LIST_FRAGMENT = "list_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    public void LogIn() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    @Override
    public void SignUp(String name, String password) {
        if (name.trim().length()==0  && password.trim().length() == 0){
            Toast toast = Toast.makeText(this, "Please enter a name and password", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(name.trim().length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (password.trim().length()==0) {
            Toast toast = Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            //TODO
            //HTTP POST


            }

    }
}
