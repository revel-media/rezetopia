package app.reze1.ahmed.reze1.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.reze1.ahmed.reze1.fragments.Home;
import app.reze1.ahmed.reze1.fragments.Notification;
import app.reze1.ahmed.reze1.fragments.Products;
import app.reze1.ahmed.reze1.fragments.Profile;
import app.reze1.ahmed.reze1.fragments.Requests;
import app.reze1.ahmed.reze1.fragments.SideMenuFragment;

/**
 * Created by ahmed on 2/28/2018.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;
    String type;

    public MainPagerAdapter(FragmentManager fm, int NumberOfTabs, String type)
    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
        this.type = type;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {

            case 0:
                Home home = new Home();
                return home;
            case 1:
                Notification notification = new Notification();
                return  notification;
//            case 2:
//                Products products = new Products();
//                return  products;
            case 2:
                Requests requests = new Requests();
                return  requests;
            case 3:
                Profile profile = new Profile();
                return profile;
            case 4:
                SideMenuFragment sideMenu = new SideMenuFragment();
                return  sideMenu;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}