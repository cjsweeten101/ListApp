package sweeten.clayton.listapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Clayton on 5/15/2016.
 */
public class SignupFragment extends Fragment implements View.OnClickListener {
    Button mButton;
    EditText mUserName;
    EditText mPassword;
    EditText mConfirmPassword;
    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;


    public interface OnSignUp {
        void SignUp(String UserName, String password, String ConfirmPassword, String FirstName, String LastName, String Email);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mUserName = (EditText) view.findViewById(R.id.signupUserName);
        mPassword = (EditText) view.findViewById(R.id.signupPassword);
        mConfirmPassword = (EditText) view.findViewById(R.id.ConfirmPassword);
        mFirstName = (EditText) view.findViewById(R.id.FirstName);
        mLastName = (EditText) view.findViewById(R.id.LastName);
        mEmail = (EditText) view.findViewById(R.id.Email);
        mButton = (Button) view.findViewById(R.id.signupButton);
        mButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String UserName = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String FirstName = mFirstName.getText().toString();
        String LastName = mLastName.getText().toString();
        String Email = mEmail.getText().toString();
        OnSignUp listener = (OnSignUp) getActivity();
        listener.SignUp(UserName,password, confirmPassword, FirstName, LastName, Email);
    }
}
