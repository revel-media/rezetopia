package io.rezetopia.krito.rezetopiakrito.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.fragments.TeamFragment;


public class ViewTeamsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    android.support.v4.view.PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teams);

        tabLayout = findViewById(R.id.teamTabLayout);
        viewPager = findViewById(R.id.teamViewPager);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.member));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.owner));


        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        TeamFragment team = TeamFragment.createFragment("member");
                        return team;
                    case 1:
                        TeamFragment team2 = TeamFragment.createFragment("owner");
                        return team2;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        };

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(adapter);
    }
}
