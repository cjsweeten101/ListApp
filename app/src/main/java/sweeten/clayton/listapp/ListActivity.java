package sweeten.clayton.listapp;

        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
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
        import java.util.concurrent.ExecutionException;

        import okhttp3.Headers;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.Response;


public class ListActivity extends AppCompatActivity implements  AddListFragment.OnNewListSelected, ListFragment.OnListSelectedInterface{

    int mPosition;
    FragmentManager mFragmentManager;
    FragmentTransaction mTransaction;
    FloatingActionButton mFloatingActionButton;
    ListFragment mListFragment;
    List<String> mTitles = new ArrayList<>();
    JSONArray mJSONArray;
    List<String> mItems = new ArrayList<>();
    ListFragment mItemFragment;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mListFragment.isVisible()){
            return false;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                setTitle("Your Lists");
                //mFloatingActionButton.show();
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
        try {
            String getResult = asyncGet.execute().get();
            JSONArray results = new JSONArray(getResult);
            mJSONArray = results;
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                // Log.v("JSON_RESULT", results.getJSONObject(i).toString());
               // Log.v("JSON_NAMES", jsonObject.opt("title").toString());
                String title = jsonObject.opt("title").toString();
               // NewList(title);
                mTitles.add(title);
              //  Log.v("mTitles", mTitles.get(i));
                bundle.putString("TITLE"+i, title);
                bundle.putInt(title, i);


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
    public void NewList(String title) {

        if(mListFragment.isVisible()) {

            //mTitles.add(title);
           // Bundle bundle = new Bundle();
           // bundle.putString("TITLE", title);
           // bundle.putInt("LIST_SIZE", mTitles.size());

            ListFragment savedFragment = (ListFragment) getSupportFragmentManager()
                    .findFragmentByTag("LIST_FROM_DIALOG");
            // mListFragment=savedFragment;

            if (savedFragment == null) {
                ListFragment fragment = new ListFragment();

                //TODO no lists may be fucked up

                mListFragment = fragment;
               // fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                mTransaction = fragmentTransaction;
                mTransaction.add(R.id.PlaceHolderLists, fragment, "LIST_FROM_DIALOG");
                mTransaction.commit();
            } else {
                savedFragment.mAdapter.add(title);
            }

        } else {
            mItemFragment.mAdapter.add(title);

        }

    }

    @Override
    public void onListSelected(int position, String title) {

            // mFloatingActionButton.hide();

            Bundle bundle = new Bundle();
        try {
            JSONObject jsonObject = mJSONArray.getJSONObject(position);
            JSONArray itemsArray = jsonObject.getJSONArray("list_items");
            for (int i=0; i<itemsArray.length(); i++){
                String content = itemsArray.getJSONObject(i).optString("content");
                bundle.putString("TITLE"+i,content);
            }
                bundle.putInt("LIST_SIZE",itemsArray.length());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTitle(title);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ListFragment fragment = new ListFragment();
            mItemFragment = fragment;
            fragment.setArguments(bundle);


            fragmentTransaction.hide(mListFragment);
            fragmentTransaction.add(R.id.PlaceHolderLists, fragment, title);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


    }

}
