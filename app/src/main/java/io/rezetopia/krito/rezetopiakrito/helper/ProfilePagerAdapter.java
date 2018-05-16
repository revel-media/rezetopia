package io.rezetopia.krito.rezetopiakrito.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.fragments.OverviewProfile;
import io.rezetopia.krito.rezetopiakrito.fragments.PhotosProfile;
import io.rezetopia.krito.rezetopiakrito.fragments.PostsProfile;
import io.rezetopia.krito.rezetopiakrito.fragments.VideosProfile;

/**
 * Created by ahmed on 4/23/2018.
 */
public class ProfilePagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
    Context context;
    ViewPager viewPager;
    TabLayout tabLayout;

    public ProfilePagerAdapter(FragmentManager manager, Context context, ViewPager viewPager,
                            TabLayout tabLayout) {
        super(manager);
        this.context = context;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0: return new OverviewProfile();
            case 1: return new PostsProfile();
            case 2: return new VideosProfile();
            case 3: return new PhotosProfile();
            default: return null;
        }
        //return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(OverviewProfile fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        tabLayout.addTab(tabLayout.newTab().setText(title));
    }

    public void removeFrag(int position) {
        removeTab(position);
        Fragment fragment = mFragmentList.get(position);
        mFragmentList.remove(fragment);
        mFragmentTitleList.remove(position);
        destroyFragmentView(viewPager, position, fragment);
        notifyDataSetChanged();
    }

   public View getTabView(final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_inner_tab, null);
        TextView tabItemName = (TextView) view.findViewById(R.id.tabName);
        tabItemName.setText(mFragmentTitleList.get(position));
        tabItemName.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        tabItemName.setTextSize(5);
       /* switch (mFragmentTitleList.get(position)) {
            case "Gaiduk":
                tabItemAvatar.setImageResource(R.drawable.gaiduk);
                break;
            case "Nguyen":
                tabItemAvatar.setImageResource(R.drawable.avatar);
                break;
            case "Balakin":
                tabItemAvatar.setImageResource(R.drawable.balakin);
                break;
            case "Golovin":
                tabItemAvatar.setImageResource(R.drawable.golovin);
                break;
            case "Ovcharov":
                tabItemAvatar.setImageResource(R.drawable.ovcharov);
                break;
            case "Solovienko":
                tabItemAvatar.setImageResource(R.drawable.solovei);
                break;
            default:
                tabItemAvatar.setImageResource(R.drawable.boy);
                break;
        }*/

        return view;
    }

    public void destroyFragmentView(ViewGroup container, int position, Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
    }

    public void removeTab(int position) {
        if (tabLayout.getChildCount() > 0) {
            tabLayout.removeTabAt(position);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}