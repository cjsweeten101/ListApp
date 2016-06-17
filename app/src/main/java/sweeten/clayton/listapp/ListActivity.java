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
        import java.util.ArrayDeque;
        import java.util.ArrayList;
        import java.util.Deque;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Map;
        import java.util.Queue;
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
    private Map<Integer,String> mSortedChildrenTitles;
    private Map<String, Integer> mSortedIds;
    private Map<Integer, JSONObject> mSortedObjects;
    private int mListId;
    private int mItemId;
    private Map<Integer, String> mSortedItems;
    private String mEditTitle;
    private int mAdapterPosition;
    Boolean mParent;
    Deque<Integer> mQueue = new ArrayDeque<>();
    List<Integer> mDepth = new ArrayList<>();
    private int mTeamID;
    private int mParentId;
    ListFragment mCurrentFragment;
    private int mCurrentId;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mQueue.size()<1){
            return false;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                AsyncGet asyncGet = new AsyncGet();
                Log.v("QUEUE",mQueue.toString());
                mQueue.removeLast();
                if (mQueue.size()<1) {
                   AddFragmentFromGet("http://listaroo.herokuapp.com/api/lists?teamId=2");

                } else {
                    int ID = mQueue.peekLast();
                    Log.v("QUEUE REMOVE", mQueue + "");
                    Log.v("CALL ID", ID + "");
                    mParentId = ID;
                    AddFragmentFromGet("https://listaroo.herokuapp.com/api/lists/" + ID);
                }


                //setTitle(mQueue.toString());

                mItemFragment=null;
                //return super.onKeyDown(keyCode, event);

            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mTeamID = 2;
        setTitle("Team " +mTeamID);
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
            String getResult = asyncGet.execute("http://listaroo.herokuapp.com/api/lists?teamId=2").get();
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
        mCurrentFragment = listFragment;
        listFragment.setArguments(bundle);
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager1.beginTransaction();
        fragmentTransaction.add(R.id.PlaceHolderLists, listFragment, "LIST_FROM_DIALOG");
        fragmentTransaction.commit();
        mParent=true;
    }

    @Override
    public String NewList(String title) {
        String result = mCurrentFragment.mAdapter.add(title,false,mTeamID,mParentId);
        Log.v("RESULTS",result);

        JSONObject jsonObject = null;
        int id=0;
        String Title="";
        try {
            jsonObject = new JSONObject(result);
            id = (int) jsonObject.opt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mParentId==0) {
            mSortedTitles.put(id,title);
        } else {
            mSortedChildrenTitles.put(id,title);
            Log.v("CHILD ID AND TITLE", id + Title);

        } mParentId = id;

      return null;
    }

    @Override
    public void onListSelected(int position, String title, View view) {
        Map<Integer,String> sortedChildren = new TreeMap<>();
        AsyncGet asyncGet = new AsyncGet();
        int ID=0;
        Log.v("PARENTID" ,mParentId+"");
        Log.v("CLICKEDTITLE",title);
            if(mParentId==0) {
                for (Map.Entry<Integer, String> entry : mSortedTitles.entrySet()) {
                    int id = entry.getKey();
                   // String Title = entry.getValue();
                   // Log.v("SORTEDTITLES",Title);
                    if (mSortedTitles.get(id) == title) {
                       ID = id;
                    }
                }
            } else {
                for (Map.Entry<Integer,String> entry : mSortedChildrenTitles.entrySet()) {
                    int id = entry.getKey();
                    String Title = entry.getValue();
                    Log.v("SORTED_CHILDREN_TITLES",Title);
                    if(mSortedChildrenTitles.get(id) == title) {
                       ID = id;
                        Log.v("FOUND?", id + "");

                    }
                }

            }

        switch (view.getId()){
            case R.id.deleteButton:
                mCurrentFragment.mAdapter.delete(position,ID);
                return;
            case R.id.editButton:
                mCurrentId = ID;
                mAdapterPosition = position;
                EditDialogFragment dialogFragment = new EditDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialogFragment.show(fragmentManager,"EDIT");
                return;
        }

        try {
            String results = asyncGet.execute("https://listaroo.herokuapp.com/api/lists/"+ID).get();

            mParentId = ID;

            mQueue.add(ID);
            Log.v("QUEUE ADD",mQueue + "");
            Log.v("PARENT?", String.valueOf(mParentId));
            Log.v("ID", ID +"");
            JSONObject jsonObject = new JSONObject(results);

            JSONArray jsonArray = jsonObject.getJSONArray("child_lists");

                for (int i = 0; i < jsonArray.length(); i++) {
                    String childTitle = jsonArray.getJSONObject(i).optString("title");
                    int childId = jsonArray.getJSONObject(i).optInt("id");

                    sortedChildren.put(childId, childTitle);
                    Log.v("ADDCHILDREN", childTitle);
                }
                mSortedChildrenTitles = sortedChildren;
                int i = 0;
                Bundle bundle = new Bundle();
                for (Map.Entry<Integer, String> entry : sortedChildren.entrySet()) {
                    String bundleTitle = entry.getValue();
                    bundle.putString("TITLE" + i, bundleTitle);
                    i++;
                }
                bundle.putInt("LIST_SIZE", sortedChildren.size());


                ListFragment fragment = new ListFragment();
                mItemFragment = fragment;
                mCurrentFragment = fragment;
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.PlaceHolderLists, fragment, "CHILD");
                setTitle(title);
                transaction.commit();

                mParent = false;



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Edit(String title) {

        mCurrentFragment.mAdapter.update(title, mAdapterPosition, mCurrentId);
        if(mParentId==0){
            mSortedTitles.put(mCurrentId,title);
        } else {
            mSortedChildrenTitles.put(mCurrentId,title);
        }
        Log.v("ADAPTER AND ID", mAdapterPosition + mCurrentId + "");
    }

    public void AddFragmentFromGet(String url){
        Map<Integer,String> sortedChildren = new TreeMap<>();
        AsyncGet asyncGet = new AsyncGet();
        JSONArray jsonArray = new JSONArray();

        try {

            String results = asyncGet.execute(url).get();
            Log.v("RESULTS",results);

            if(url=="http://listaroo.herokuapp.com/api/lists?teamId=2"){
                JSONArray jsonArray1 = new JSONArray(results);
                jsonArray = jsonArray1;
                setTitle("Team " +mTeamID);
            } else {

                JSONObject jsonObject = new JSONObject(results);
                setTitle(jsonObject.optString("title"));

                jsonArray = jsonObject.getJSONArray("child_lists");
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                String childTitle = jsonArray.getJSONObject(i).optString("title");
                int childId = jsonArray.getJSONObject(i).optInt("id");

                sortedChildren.put(childId, childTitle);
            }
            mSortedChildrenTitles = sortedChildren;
            int i = 0;
            Bundle bundle = new Bundle();
            for (Map.Entry<Integer, String> entry : sortedChildren.entrySet()) {
                String bundleTitle = entry.getValue();
                bundle.putString("TITLE" + i, bundleTitle);
                i++;
            }
            bundle.putInt("LIST_SIZE", sortedChildren.size());

            ListFragment fragment = new ListFragment();
            mItemFragment = fragment;
            mCurrentFragment = fragment;
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.PlaceHolderLists, fragment, "CHILD");
            transaction.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
