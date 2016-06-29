package sweeten.clayton.listapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Clayton on 6/13/2016.
 */
public class EditDialogFragment extends DialogFragment {
    EditText mEditText;
    String mTitle;
    TextView mPromptTitle;
    private String mEditTitle;

    public interface EditInterface{
        void Edit(String title);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.addlist_dialog,null);
        mPromptTitle = (TextView) view.findViewById(R.id.textViewDialog);
        mPromptTitle.setText("Please enter a new title");
        mEditText = (EditText) view.findViewById(R.id.newTitle);
        mEditText.setText(mEditTitle);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mEditText = (EditText) view.findViewById(R.id.newTitle);
                        mTitle = mEditText.getText().toString();
                        if (mTitle.trim().length()<1){
                            Toast toast = Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT);
                            toast.show();
                        }else {
                            //call method
                            EditInterface editInterface = (EditInterface) getActivity();
                            editInterface.Edit(mTitle);
                        }


                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditDialogFragment.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        (getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
    }

    public void setTextField(String title){
       mEditTitle = title;
    }
}
