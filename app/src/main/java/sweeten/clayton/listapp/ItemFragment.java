package sweeten.clayton.listapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ItemFragment extends Fragment {

    EditText mEditText;

    @Override
    public void onStop() {
        super.onStop();

       // Toast toast = Toast.makeText(getContext(), "ITEMS STOPPED", Toast.LENGTH_SHORT);
       // toast.show();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        String title = getArguments().getString("TITLE");
       // getActivity().setTitle(title);

        mEditText = (EditText) view.findViewById(R.id.EditTextItems);

        return view;
    }
}
