package sweeten.clayton.listapp;

        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.DialogFragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;
        import java.util.TreeMap;
        import java.util.concurrent.ExecutionException;

        import okhttp3.Headers;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.Response;


public class ListActivity extends AppCompatActivity implements  AddListFragment.OnNewListSelected, ListFragment.OnListSelectedInterface, EditDialogFragment.EditInterface{

    int mPosition;
    FragmentManager mFragmentManager;
    FragmentTransaction mTransaction;
    FloatingActionButton mFloatingActionButton;
    ListFragment mListFragment;
    List<String> mTitles = new ArrayList<>();
    JSONArray mJSONArray;
    List<String> mItems = new ArrayList<>();
    ListFragment mItemFragment;
    private Map<Integer, String> mSortedTitles;
    private Map<String, Integer> mSortedIds;
    private Map<Integer, JSONObject> mSortedObjects;
    private int mListId;
    private int mItemId;
    private Map<Integer, String> mSortedItems;
    private String mEditTitle;
    private int mAdapterPosition;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mListFragment.isVisible()){
            return false;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                setTitle("Your Lists");
                //mFloatingActionButton.show();
                mItemFragment=null;
                return super.onKeyDown(keyCode, event);

            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setTitle("Your Lists");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton = fab;

        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentManager = fragmentManager;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    AddListFragment dialog = new AddListFragment();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    mFragmentManager = fragmentManager;
                    dialog.show(fragmentManager, "SWAG");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        AsyncGet asyncGet = new AsyncGet();
        Bundle bundle = new Bundle();
        Map<Integer,String> sortedTitles = new TreeMap<>();
        mSortedTitles = sortedTitles;
        Map<Integer,JSONObject> sortedObjects = new TreeMap<>();
        mSortedObjects = sortedObjects;

        try {
            String getResult = asyncGet.execute().get();
            JSONArray results = new JSONArray(getResult);
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                String title = jsonObject.opt("title").toString();
                int id = jsonObject.optInt("id");

                sortedObjects.put(id,jsonObject);
                sortedTitles.put(id,title);
                mTitles.add(title);

            }

            int i =0;
            for(Map.Entry<Integer,String> entry : sortedTitles.entrySet()){
                String title = entry.getValue();
                bundle.putString("TITLE"+i,title);
                i++;

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        bundle.putInt("LIST_SIZE",mTitles.size());

        ListFragment listFragment = new ListFragment();
        mListFragment = listFragment;
        listFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager1.beginTransaction();
        fragmentTransaction.add(R.id.PlaceHolderLists, listFragment, "LIST_FROM_DIALOG");
        fragmentTransaction.commit();

    }

    @Override
    public String NewList(String title) {

        String results;
        int id;

        if(mListFragment.isVisible()  && (mItemFragment==null)) {

            ListFragment savedFragment = (ListFragment) getSupportFragmentManager()
                    .findFragmentByTag("LIST_FROM_DIALOG");

            if (savedFragment == null) {
                ListFragment fragment = new ListFragment();

                //TODO new lists may be fucked up (YUP)

                mListFragment = fragment;
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                mTransaction = fragmentTransaction;
                mTransaction.add(R.id.PlaceHolderLists, fragment, "LIST_FROM_DIALOG");
                mTransaction.commit();
            } else {
                results = savedFragment.mAdapter.add(title,Boolean.FALSE,0);
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    id = jsonObject.optInt("id");
                    mSortedTitles.put(id,title);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mListFragment = savedFragment;
            }

        } else {
           results = mItemFragment.mAdapter.add(title, Boolean.FALSE,mListId);
            try {
                JSONObject jsonObject = new JSONObject(results);
               id = jsonObject.optInt("id");
                mSortedTitles.put(id,title);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    public void onListSelected(int position, String title, View view) {

        mAdapterPosition = position;
        int key = 0;

        if(!(mSortedTitles==null)) {
            for (Map.Entry<Integer, String> entry : mSortedTitles.entrySet()) {
                key = entry.getKey();
                if (mSortedTitles.get(key) == title) {
                    mListId = key;
                    Log.v("KEYS", String.valueOf(key));
                }
            }
        }
        if(!(mListFragment.isVisible())) {
            for (Map.Entry<Integer, String> entry : mSortedItems.entrySet()) {
                int itemID = entry.getKey();
                if (mSortedItems.get(itemID) == title) {
                    mItemId = itemID;
                }
            }
        }

        if(view.getId()==R.id.editButton){



            EditDialogFragment dialog = new EditDialogFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            mFragmentManager = fragmentManager;
            dialog.show(fragmentManager, "SWAG");

            return;
        }

        if(view.getId()==R.id.deleteButton) {
            if(mListFragment.isVisible()){
                mListFragment.mAdapter.delete(position,true,mListId);
            } else {
                for(Map.Entry<Integer,String> entry : mSortedItems.entrySet()){
                   int itemID = entry.getKey();
                    if(mSortedItems.get(itemID)==title){
                        mItemId = itemID;
                    }
                }
                mItemFragment.mAdapter.delete(position,false,mItemId);

            }

        } else {

            if (mListFragment.isVisible()) {

                Bundle bundle = new Bundle();
                Map<Integer, String> sortedItems = new TreeMap<>();
                JSONObject jsonObject = mSortedObjects.get(mListId);
                if (!(jsonObject == null)) {
                    try {

                        jsonObject = mSortedObjects.get(mListId);
                        JSONArray itemsArray = jsonObject.getJSONArray("list_items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            String content = itemsArray.getJSONObject(i).optString("content");
                            int id = itemsArray.getJSONObject(i).optInt("id");
                            sortedItems.put(id, content);
                        }
                        mSortedItems = sortedItems;
                        bundle.putInt("LIST_SIZE", itemsArray.length());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int i = 0;
                    for (Map.Entry<Integer, String> entry : sortedItems.entrySet()) {
                        String content = entry.getValue();
                        bundle.putString("TITLE" + i, content);
                        i++;
                    }
                }
                bundle.putInt("LIST_ID", mListId);
                setTitle(title);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ListFragment fragment = new ListFragment();
                mItemFragment = fragment;
                fragment.setArguments(bundle);


                fragmentTransaction.hide(mListFragment);
                fragmentTransaction.add(R.id.PlaceHolderLists, fragment, "LIST");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            } else {

                setTitle(title);
                ListFragment listFragment = new ListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("LIST_ID",mListId);
                bundle.putInt("LIST_SIZE",0);

                listFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(mItemFragment);
                transaction.addToBackStack("LIST");
                transaction.add(R.id.PlaceHolderLists, listFragment, "LIST");
                transaction.commit();


            }
        }
    }

    @Override
    public void Edit(String title) {
        mEditTitle = title;

        if(mListFragment.isVisible()){
            mListFragment.mAdapter.update(mEditTitle, mAdapterPosition, true, mListId);
        } else {
            if(!(mSortedItems==null)) {
                for (Map.Entry<Integer, String> entry : mSortedItems.entrySet()) {
                    int itemID = entry.getKey();
                    if (mSortedItems.get(itemID) == title) {
                        mItemId = itemID;
                    }
                }
            }
            mItemFragment.mAdapter.update(mEditTitle,mAdapterPosition,false,mItemId);
        }

    }
}
