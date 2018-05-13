package app.reze1.ahmed.reze1.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.fragments.StoreListFragment;

public class StoresListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    android.support.v4.view.PagerAdapter adapter;
    TextView createStoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);

        tabLayout = findViewById(R.id.vendorTabLayout);
        viewPager = findViewById(R.id.vendorViewPager);
        createStoreView = findViewById(R.id.createStoreView);

        createStoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoresListActivity.this, CreateStoreActivity.class);
                startActivity(intent);
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(R.string.explore));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_store));

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        return StoreListFragment.createFragment("all");
                    case 1:
                        return  StoreListFragment.createFragment("my");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
