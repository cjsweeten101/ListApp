package sweeten.clayton.listapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Clayton on 5/18/2016.
 */
public class AddListFragment extends android.support.v4.app.DialogFragment {
    Button mOkButton;
    Button mCancelButton;
    EditText mTitle;
    String mTitleString;
    TextView mPromptTitle;

    public interface OnNewListSelected {
        String NewList(String title);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.addlist_dialog,null);
        mPromptTitle = (TextView) view.findViewById(R.id.textViewDialog);
        mTitle = (EditText) view.findViewById(R.id.newTitle);
        String title="";


        if(getArguments().getBoolean("INVITE")==false){
            title = "Please enter a new title";
        } else {
            title = "Please enter a username to invite";
            mTitle.setHint("Username");
        }
        mPromptTitle.setText(title);
        builder.setView(view)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mTitle = (EditText) view.findViewById(R.id.newTitle);

                mTitleString = mTitle.getText().toString();
                if (mTitleString.trim().length()<1){
                    Toast toast = Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    OnNewListSelected listener = (OnNewListSelected) getActivity();
                    listener.NewList(mTitleString);

                }


            }
        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddListFragment.this.getDialog().cancel();

                    }
                });


        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        (getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }

}
