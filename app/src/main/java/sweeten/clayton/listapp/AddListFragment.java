package sweeten.clayton.listapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Clayton on 5/18/2016.
 */
public class AddListFragment extends android.support.v4.app.DialogFragment {
    Button mOkButton;
    Button mCancelButton;
    EditText mTitle;
    String mTitleString;

    public interface OnNewListSelected {
        String NewList(String title);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.addlist_dialog,null);
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

}
