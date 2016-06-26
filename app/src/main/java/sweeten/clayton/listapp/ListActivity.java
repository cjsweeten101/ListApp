package sweeten.clayton.listapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;



public class ListActivity extends AppCompatActivity implements  AddListFragment.OnNewListSelected, ListFragment.onListSelectedInterface, EditDialogFragment.EditInterface, AsyncCreate.CreateCallback , AsyncGet.GetCallBack, AsyncUpdate.UpdateCallBack{

    FragmentManager mFragmentManager;
    FloatingActionButton mFloatingActionButton;
    private Map<Integer, String> mSortedTitles = new TreeMap<>();
    private Map<Integer,String> mSortedChildrenTitles = new TreeMap<>();
    private int mAdapterPosition;
    Boolean mParent;
    Deque<Integer> mQueue = new ArrayDeque<>();
    private int mTeamID;
    private int mParentId;
    ListFragment mCurrentFragment;
    private int mCurrentId;
    private String mTeamTitle;
    private FloatingActionButton mInviteFab;
    private boolean mAddListListener;

    private ProgressBar mProgressBar;
    private String mInviteTitle;
    private int mCreatorId;
    private String mToken;
    private boolean mBooleanCreate;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mQueue.size()<1){
            return super.onKeyDown(keyCode, event);
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                mQueue.removeLast();
                if (mQueue.size()<1) {
                    setTitle(mTeamTitle);
                    mInviteFab.show();
                    mParent = true;
                    mParentId = 0;
                   AddFragmentFromGet("http://listaroo.herokuapp.com/api/lists?teamId="+mTeamID, true);

                } else {
                    int ID = mQueue.peekLast();
                    mParentId = ID;
                    AddFragmentFromGet("https://listaroo.herokuapp.com/api/lists/" + ID, false);
                }


            }
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mParent= true;
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        mTeamID = getIntent().getExtras().getInt("ID");
        mTeamTitle = getIntent().getExtras().getString("TITLE");
        mCreatorId = getIntent().getExtras().getInt("UserId");
        mToken = getIntent().getExtras().getString("token");
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

            asyncGet.execute("http://listaroo.herokuapp.com/api/lists?teamId="+mTeamID,mCreatorId+"",mToken+"");

    }

    @Override
    public String NewList(String title) {

        if(mAddListListener==true) {
            mCurrentFragment.mAdapter.add(title, false, mTeamID, mParentId,this, mProgressBar, mCreatorId, mToken);

        } else {
            AsyncCreate asyncCreate = new AsyncCreate(this,mProgressBar);
            JSONObject jsonObject = new JSONObject();
            try {
                mInviteTitle = title;
                jsonObject.put("username", title);

                asyncCreate.execute("http://listaroo.herokuapp.com/api/teams/" + mTeamID + "/invite", jsonObject.toString(), mCreatorId+"",mToken+"");

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
                mCurrentFragment.mAdapter.delete(position,ID, mCreatorId, mToken);
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


            asyncGet.execute("https://listaroo.herokuapp.com/api/lists/"+ID,mCreatorId+"",mToken+"");

            mParentId = ID;

            mQueue.add(ID);
    }

    @Override
    public void Edit(String title) {

        mCurrentFragment.mAdapter.update(title, mAdapterPosition, mCurrentId, mProgressBar, this, mCreatorId, mToken );
        if(mParentId==0){
            mSortedTitles.put(mCurrentId,title);
        } else {
            mSortedChildrenTitles.put(mCurrentId,title);
        }
    }

    public void AddFragmentFromGet(String url, Boolean parent){
        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        mParent = parent;


            asyncGet.execute(url,mCreatorId+"",mToken+"");

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

            try {

                Bundle bundle = new Bundle();

                if(mParent==true){
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i = 0; i<jsonArray.length(); i++){
                        mSortedTitles.put(jsonArray.getJSONObject(i).optInt("id"),jsonArray.getJSONObject(i).optString("title"));
                        bundle.putString("TITLE"+i, jsonArray.getJSONObject(i).optString("title"));
                        setTitle(mTeamTitle);
                    }
                    bundle.putInt("LIST_SIZE",jsonArray.length());
                    mParent=false;
                } else {
                    JSONObject jsonObject = new JSONObject(result);
                    setTitle(jsonObject.getString("title"));
                    JSONArray jsonArray = jsonObject.getJSONArray("child_lists");
                    for(int i = 0; i<jsonArray.length(); i++){
                        mSortedChildrenTitles.put(jsonArray.getJSONObject(i).optInt("id"),jsonArray.getJSONObject(i).optString("title"));
                        bundle.putString("TITLE"+i, jsonArray.getJSONObject(i).optString("title"));
                    }
                    bundle.putInt("LIST_SIZE",jsonArray.length());

                }

                ListFragment fragment = new ListFragment();
                mCurrentFragment = fragment;
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


                transaction.replace(R.id.PlaceHolderLists, fragment, "CHILD");
                transaction.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }


    }

    @Override
    public void updateFinished(String result) {

    }
}
