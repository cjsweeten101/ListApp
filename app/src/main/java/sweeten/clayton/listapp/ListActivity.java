package sweeten.clayton.listapp;

        import android.content.Intent;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayDeque;
        import java.util.ArrayList;
        import java.util.Deque;
        import java.util.List;
        import java.util.Map;
        import java.util.TreeMap;
        import java.util.concurrent.ExecutionException;


public class ListActivity extends AppCompatActivity implements  AddListFragment.OnNewListSelected, ListFragment.onListSelectedInterface, EditDialogFragment.EditInterface, AsyncCreate.CreateCallback , AsyncGet.GetCallBack{

    int mPosition;
    FragmentManager mFragmentManager;
    FragmentTransaction mTransaction;
    FloatingActionButton mFloatingActionButton;
    ListFragment mListFragment;
    List<String> mTitles = new ArrayList<>();
    JSONArray mJSONArray;
    List<String> mItems = new ArrayList<>();
    ListFragment mItemFragment;
    private Map<Integer, String> mSortedTitles = new TreeMap<>();
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
    private String mTeamTitle;
    private FloatingActionButton mInviteFab;
    private boolean mAddListListener;

    private ProgressBar mProgressBar;
    private String mInviteTitle;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mQueue.size()<1){
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
                Log.v("QUEUE",mQueue.toString());
                mQueue.removeLast();
                if (mQueue.size()<1) {
                    setTitle(mTeamTitle);
                    mInviteFab.show();
                    mParent = true;
                    mParentId = 0;
                   AddFragmentFromGet("http://listaroo.herokuapp.com/api/lists?teamId="+mTeamID, true);

                } else {
                    int ID = mQueue.peekLast();
                    Log.v("QUEUE REMOVE", mQueue + "");
                    Log.v("CALL ID", ID + "");
                    mParentId = ID;
                    AddFragmentFromGet("https://listaroo.herokuapp.com/api/lists/" + ID, false);
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

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        mTeamID = getIntent().getExtras().getInt("ID");
        mTeamTitle = getIntent().getExtras().getString("TITLE");
        setTitle(mTeamTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton = fab;

        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentManager = fragmentManager;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mAddListListener = true;
                    AddListFragment dialog = new AddListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("INVITE",false);
                    dialog.setArguments(bundle);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    mFragmentManager = fragmentManager;
                    dialog.show(fragmentManager, "SWAG");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FloatingActionButton inviteFab = (FloatingActionButton) findViewById(R.id.fabInivte);
        mInviteFab= inviteFab;
        inviteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddListListener = false;
                AddListFragment dialog = new AddListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("INVITE",true);
                dialog.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                mFragmentManager = fragmentManager;
                dialog.show(fragmentManager, "SWAG");
            }
        });
        inviteFab.show();



        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        Bundle bundle = new Bundle();
        Map<Integer,JSONObject> sortedObjects = new TreeMap<>();
        mSortedObjects = sortedObjects;

        try {
            String getResult = asyncGet.execute("http://listaroo.herokuapp.com/api/lists?teamId="+mTeamID).get();
            JSONArray results = new JSONArray(getResult);
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                String title = jsonObject.opt("title").toString();
                int id = jsonObject.optInt("id");

                sortedObjects.put(id,jsonObject);
                mSortedTitles.put(id,title);
                mTitles.add(title);

            }

            int i =0;
            for(Map.Entry<Integer,String> entry : mSortedTitles.entrySet()){
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

        if(mAddListListener==true) {
            mCurrentFragment.mAdapter.add(title, false, mTeamID, mParentId,this, mProgressBar);

        } else {
            AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
            JSONObject jsonObject = new JSONObject();
            try {
                mInviteTitle = title;
                jsonObject.put("username", title);

                asyncCreate.execute("http://listaroo.herokuapp.com/api/teams/" + mTeamID + "/invite", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

      return null;
    }

    @Override
    public void onListSelected(int position, String title, View view) {

        Map<Integer,String> sortedChildren = new TreeMap<>();
        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        int ID=0;

            if(mParentId==0) {
                for (Map.Entry<Integer, String> entry : mSortedTitles.entrySet()) {
                    int id = entry.getKey();
                    if (mSortedTitles.get(id).equals(title)) {
                       ID = id;
                    }
                }
            } else {
                for (Map.Entry<Integer,String> entry : mSortedChildrenTitles.entrySet()) {
                    int id = entry.getKey();
                    if(mSortedChildrenTitles.get(id).equals(title)) {
                       ID = id;
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

        mInviteFab.hide();

        try {
            String results = asyncGet.execute("https://listaroo.herokuapp.com/api/lists/"+ID).get();

            mParentId = ID;

            mQueue.add(ID);
            JSONObject jsonObject = new JSONObject(results);

            JSONArray jsonArray = jsonObject.getJSONArray("child_lists");

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

    public void AddFragmentFromGet(String url, Boolean parent){
        Map<Integer,String> sortedChildren = new TreeMap<>();
        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        JSONArray jsonArray = new JSONArray();

        try {

            String results = asyncGet.execute(url).get();
            Log.v("RESULTS",results);

            if(parent == true) {
                JSONArray jsonArray1 = new JSONArray(results);
                jsonArray = jsonArray1;
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

    @Override
    public void createFinished(String result) {

        try {


            JSONObject jsonObject = new JSONObject(result);

            if (mAddListListener == true) {
                String title =  jsonObject.optString("title");
                int id = jsonObject.optInt("id");
                if (mParentId == 0) {
                    mSortedTitles.put(id, title);
                } else {
                    mSortedChildrenTitles.put(id,title);
                }


            } else {
                if(jsonObject.opt("errors")==null) {
                    Toast toast = Toast.makeText(this, mInviteTitle + " invited!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    String error = jsonObject.getJSONArray("errors").getString(0);
                    Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void getFinished(String result) {


    }
}
