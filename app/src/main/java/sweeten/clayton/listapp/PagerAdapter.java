package sweeten.clayton.listapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Map;


public class PagerAdapter extends FragmentPagerAdapter {

    Map<Integer, String> mCreated;
    Map<Integer, String> mInvited;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public PagerAdapter(FragmentManager fm, Map<Integer, String> Created, Map<Integer, String> Invited) {
        super(fm);
        mCreated = Created;
        mInvited = Invited;
    }

    @Override
    public ListFragment getItem(int position) {
        switch (position){
            case 0:
                return ListFragment.newInstance(position,mCreated);
            case 1:
                return ListFragment.newInstance(position,mInvited);
        }
        return null;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
