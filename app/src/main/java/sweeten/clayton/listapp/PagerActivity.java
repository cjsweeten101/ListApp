package sweeten.clayton.listapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class PagerActivity extends AppCompatActivity implements ListFragment.onListSelectedInterface, AddListFragment.OnNewListSelected, AsyncCreate.CreateCallback, AsyncGet.GetCallBack, EditDialogFragment.EditInterface, AsyncUpdate.UpdateCallBack{
    ViewPager mPager;
    PagerAdapter mAdapter;
    private Map<Integer, String> mSortedCreatedTeams = new TreeMap<>();
    private Map<Integer, String> mSortedInvitedTeams = new TreeMap<>();
    private Map<Integer, String> mSortedTeams = new TreeMap<>();
    private int mTabPosition;
    private int mCreatorId;
    private ListFragment mCreatedFragment;
    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;
    private String mResult;
    int mGetCounter;
    private String mNewTitle;
    private ListFragment mListFragment;
    private int mCuurentId;
    private int mCurrentPosition;
    private String mToken;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGetCounter = 0;

        setTitle("Your Teams:");
        int id = getIntent().getExtras().getInt("Id");
        mCreatorId = id;
        String token = getIntent().getExtras().getString("token");
        mToken = token;

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);


        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        AsyncGet asyncGet1 = new AsyncGet(this,mProgressBar);


        asyncGet.execute("http://listaroo.herokuapp.com/api/teams?userId="+id+"&type=created",mCreatorId+"",mToken+"");
        asyncGet1.execute("http://listaroo.herokuapp.com/api/teams?userId="+id+"&type=invited",mCreatorId+"",mToken+"");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mFab= fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddListFragment dialog = new AddListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("INVITE",false);
                dialog.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                dialog.show(fragmentManager, "SWAG");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onListSelected(int position, String title, View view) {


        Intent intent = new Intent(this, ListActivity.class);
        int id = 0;
        if (mTabPosition == 0) {
            for (Map.Entry<Integer, String> entry : mSortedCreatedTeams.entrySet()) {
                if (entry.getValue().equals(title)) {
                    intent.putExtra("TITLE", entry.getValue());
                    intent.putExtra("ID", entry.getKey());
                    id = entry.getKey();
                }
            }
        } else {
            for (Map.Entry<Integer, String> entry : mSortedInvitedTeams.entrySet()) {
                if (entry.getValue().equals(title)) {
                    intent.putExtra("TITLE", entry.getValue());
                    intent.putExtra("ID", entry.getKey());
                    id = entry.getKey();
                }
            }
        }
        ListFragment listFragment = (ListFragment) mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        mListFragment = listFragment;
        switch(view.getId()) {

            case R.id.deleteButton:
                mListFragment.mAdapter.deleteTeams(position,id, mCreatorId, mToken);
                return;
            case R.id.editButton:
                mCuurentId = id;
                mCurrentPosition = position;
                EditDialogFragment dialogFragment = new EditDialogFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialogFragment.show(fragmentManager,"EDIT");
                return;



        }
        intent.putExtra("UserId",mCreatorId);
        intent.putExtra("token",mToken);
        startActivity(intent);
    }

    @Override
    public String NewList(String title) {

        if(title.length()<1){
            Toast toast = Toast.makeText(this, "Please enter a title", Toast.LENGTH_LONG);
            toast.show();
        }
        for(Map.Entry<Integer,String> entry : mSortedCreatedTeams.entrySet()) {
            if(entry.getValue().equals(title)){
                Toast toast = Toast.makeText(this, "A team with that name already exists", Toast.LENGTH_LONG);
                toast.show();
                return null;
            }

        }
        ListFragment listFragment = (ListFragment) mAdapter.getRegisteredFragment(mPager.getCurrentItem());
        listFragment.mAdapter.addTeams(title, mCreatorId,this, mProgressBar, mCreatorId, mToken);
        mNewTitle = title;

        return null;
    }

    @Override
    public void createFinished(String result) {

        try {
            Log.v("result",result);
            JSONObject jsonObject = new JSONObject(result);
            if(!(jsonObject.optString("errors").length()<1)){
                Toast toast = Toast.makeText(this,jsonObject.optString("errors"),Toast.LENGTH_LONG);
                toast.show();
            } else {
                int id = jsonObject.optInt("id");
                String title = jsonObject.optString("name");
                mSortedCreatedTeams.put(id,title);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getFinished(String result) {
        try {
            //mProgressBar.setVisibility(View.VISIBLE);

            if (mGetCounter == 0) {

                JSONArray jsonArrayResultsCreated = new JSONArray(result);
                for (int i = 0; i < jsonArrayResultsCreated.length(); i++) {
                    JSONObject jsonObject = jsonArrayResultsCreated.getJSONObject(i);
                    String team = (String) jsonObject.opt("name");
                    int teamId = jsonObject.optInt("id");

                    mSortedCreatedTeams.put(teamId, team);
                }
                mGetCounter++;
            } else {
                JSONArray jsonArrayResultsInvited = new JSONArray(result);
                for(int i = 0; i<jsonArrayResultsInvited.length(); i++){
                    JSONObject jsonObject = jsonArrayResultsInvited.getJSONObject(i);
                    String team = jsonObject.optString("name");
                    int teamId = jsonObject.optInt("id");

                    mSortedInvitedTeams.put(teamId,team);
                }

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.addTab(tabLayout.newTab().setText("Created"));
                tabLayout.addTab(tabLayout.newTab().setText("Invited"));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


                mAdapter = new PagerAdapter(getSupportFragmentManager(), mSortedCreatedTeams, mSortedInvitedTeams);
                mPager = (ViewPager) findViewById(R.id.pager);

                mPager.setAdapter(mAdapter);

                mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mPager.setCurrentItem(tab.getPosition());
                        mTabPosition = tab.getPosition();
                        if(mTabPosition==1){
                            mFab.hide();
                        } else {
                            mFab.show();
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
               // mProgressBar.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Edit(String title) {

        mListFragment.mAdapter.updateTeam(title,mCurrentPosition,mCuurentId, mProgressBar, this, mCreatorId, mToken);
                if(mTabPosition==0){
                    mSortedCreatedTeams.put(mCuurentId,title);
                } else {
                    mSortedInvitedTeams.put(mCuurentId,title);
                }
    }

    @Override
    public void updateFinished(String result) {

    }
}
