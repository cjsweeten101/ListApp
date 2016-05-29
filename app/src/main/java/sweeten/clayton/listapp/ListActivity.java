package sweeten.clayton.listapp;

        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

public class ListActivity extends AppCompatActivity implements  AddListFragment.OnNewListSelected, ListFragment.OnListSelectedInterface{

    int mPosition;
    FragmentManager mFragmentManager;
    FragmentTransaction mTransaction;
    FloatingActionButton mFloatingActionButton;
    ListFragment mListFragment;
    List<String> mTitles = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mListFragment.isVisible()){
            return false;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                setTitle("Your Lists");
                mFloatingActionButton.show();
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               AddListFragment dialog = new AddListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                mFragmentManager = fragmentManager;
                dialog.show(fragmentManager,"SWAG");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void NewList(String title) {

        mTitles.add(mPosition,title);
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putInt("LIST_SIZE",mTitles.size());

         ListFragment savedFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag("LIST_FROM_DIALOG");
        mListFragment=savedFragment;

        if (savedFragment == null) {
            ListFragment fragment = new ListFragment();
           
            mListFragment = fragment;
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            mTransaction = fragmentTransaction;
            mTransaction.add(R.id.PlaceHolderLists, fragment, "LIST_FROM_DIALOG");
            mTransaction.commit();
        }
        else {
            savedFragment.addItem(title, mPosition);

        }

        mPosition++;

    }

    @Override
    public void onListSelected(int position, String title) {

        mFloatingActionButton.hide();

        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(bundle);


        fragmentTransaction.hide(mListFragment);
        fragmentTransaction.add(R.id.PlaceHolderLists, fragment, title);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
