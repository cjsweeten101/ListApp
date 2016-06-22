package sweeten.clayton.listapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Clayton on 5/13/2016.
 */
public class ListFragment extends android.support.v4.app.Fragment {

    public ListAdapter mAdapter;
    private ProgressBar mProgressBar;

    public static ListFragment newInstance(int position, Map<Integer, String> items) {
        Bundle bundle = new Bundle();
        int i =0;
        for(Map.Entry<Integer,String > entry : items.entrySet()){
            String title = entry.getValue();
            bundle.putString("TITLE"+i,title);
            i++;
        }
        bundle.putInt("LIST_SIZE",items.size());
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

    public interface onListSelectedInterface {
        void onListSelected(int position, String title, View view);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        onListSelectedInterface listener = (onListSelectedInterface) getActivity();

        View view = inflater.inflate(R.layout.fragment_listoflists, container, false);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        int size = getArguments().getInt("LIST_SIZE");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.reyclerView);
        ListAdapter listAdapter = new ListAdapter(listener, "", size);
        mAdapter = listAdapter;
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


       for (int i = 0; i<size; i++ ){
           String content = getArguments().getString("TITLE"+i);
           mAdapter.add(content,Boolean.TRUE, 0 , 0, getContext(),mProgressBar);
       }

        return view;

    }

}
  