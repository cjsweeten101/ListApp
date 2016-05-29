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
    EditText mName;
    EditText mPassword;


    public interface OnSignUp {
        void SignUp(String name, String password);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mName = (EditText) view.findViewById(R.id.signupName);
        mPassword = (EditText) view.findViewById(R.id.signupPassword);
        mButton = (Button) view.findViewById(R.id.signupButton);
        mButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        OnSignUp listener = (OnSignUp) getActivity();
        listener.SignUp(name,password);

    }
}
