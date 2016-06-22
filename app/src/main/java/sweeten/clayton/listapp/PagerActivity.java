package sweeten.clayton.listapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class PagerActivity extends AppCompatActivity implements ListFragment.onListSelectedInterface, AddListFragment.OnNewListSelected{
    ViewPager mPager;
    PagerAdapter mAdapter;
    private Map<Integer, String> mSortedCreatedTeams = new TreeMap<>();
    private Map<Integer, String> mSortedInvitedTeams = new TreeMap<>();
    private int mTabPosition;
    private int mCreatorId;
    private ListFragment mCreatedFragment;
    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;
    private String mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Your Teams:");
        int id = getIntent().getExtras().getInt("Id");
        mCreatorId = id;
        Log.v("ID", id+"");

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);


        AsyncGet asyncGet = new AsyncGet(this,mProgressBar);
        AsyncGet asyncGet1 = new AsyncGet(this,mProgressBar);
        String results = null;
        String resultsInvited = null;
        try {
            results = asyncGet.execute("http://listaroo.herokuapp.com/api/teams?userId="+id+"&type=created").get();
            resultsInvited = asyncGet1.execute("http://listaroo.herokuapp.com/api/teams?userId="+id+"&type=invited").get();


            //resultsInvited = asyncGet1.mResults;
           // Log.v("INVITED", resultsInvited);

            JSONArray jsonArrayResultsCreated = new JSONArray(results);
            for(int i = 0; i<jsonArrayResultsCreated.length(); i++){
                JSONObject jsonObject = jsonArrayResultsCreated.getJSONObject(i);
               String team = (String) jsonObject.opt("name");
               int teamId = jsonObject.optInt("id");

               mSortedCreatedTeams.put(teamId, team);
            }
            JSONArray jsonArrayResultsInvited = new JSONArray(resultsInvited);
            for(int i = 0; i<jsonArrayResultsInvited.length(); i++){
                JSONObject jsonObject = jsonArrayResultsInvited.getJSONObject(i);
                String team = jsonObject.optString("name");
                int teamId = jsonObject.optInt("id");

                mSortedInvitedTeams.put(teamId,team);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
                if (entry.getValue() == title) {
                    intent.putExtra("TITLE", entry.getValue());
                    intent.putExtra("ID", entry.getKey());
                    id = entry.getKey();
                }
            }
        } else {
            for (Map.Entry<Integer, String> entry : mSortedInvitedTeams.entrySet()) {
                if (entry.getValue() == title) {
                    intent.putExtra("TITLE", entry.getValue());
                    intent.putExtra("ID", entry.getKey());
                    id = entry.getKey();
                }
            }
        }
        switch(view.getId()) {
            case R.id.deleteButton:
                ListFragment listFragment = (ListFragment) mAdapter.getRegisteredFragment(mPager.getCurrentItem());
                listFragment.mAdapter.deleteTeams(position,id);
                return;
        }
        startActivity(intent);
    }

    @Override
    public String NewList(String title) {
        try {
            ListFragment listFragment = (ListFragment) mAdapter.getRegisteredFragment(mPager.getCurrentItem());
            String result = listFragment.mAdapter.addTeams(title, mCreatorId,this, mProgressBar);
            JSONObject jsonObject = new JSONObject(result);
            int id = jsonObject.optInt("id");
            mSortedCreatedTeams.put(id,title);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
