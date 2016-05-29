package sweeten.clayton.listapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Clayton on 5/15/2016.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    EditText mEmailEditText;
    EditText mPasswordEditText;
    Button mLoginButton;
    TextView mTextView;

    public interface OnSignUpSelected {
        void SignUpSwitch();
    }

    public interface OnLoginSelected {
        void LogIn();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login,container,false);
        mLoginButton = (Button) view.findViewById(R.id.signupButton);
        mLoginButton.setOnClickListener(this);
        mTextView = (TextView) view.findViewById(R.id.loginSignup);
        mTextView.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signupButton:
                OnLoginSelected listenerLogin = (OnLoginSelected) getActivity();
                listenerLogin.LogIn();
                break;
            case R.id.loginSignup:
                OnSignUpSelected listener = (OnSignUpSelected) getActivity();
                listener.SignUpSwitch();
                break;
        }
    }
}
