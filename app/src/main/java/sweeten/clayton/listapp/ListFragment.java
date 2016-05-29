package sweeten.clayton.listapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ListFragment extends android.support.v4.app.Fragment {

    public ListAdapter mAdapter;

    public interface OnListSelectedInterface {
        void onListSelected(int position, String title);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            OnListSelectedInterface listener = (OnListSelectedInterface) getActivity();

            View view = inflater.inflate(R.layout.fragment_listoflists, container, false);

            String title = getArguments().getString("TITLE");
            int position = getArguments().getInt("POSITION");

                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.reyclerView);
                ListAdapter listAdapter = new ListAdapter(listener, title, position);
                mAdapter = listAdapter;
                recyclerView.setAdapter(listAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                getActivity().setTitle("Your Lists");


                return view;

    }

    public void addItem(String title, int Position){
        mAdapter.add(title, Position);
    }

}
