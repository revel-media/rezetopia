package io.rezetopia.krito.rezetopiakrito.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.rezetopia.krito.rezetopiakrito.fragments.CreateEventFragment;
import io.rezetopia.krito.rezetopiakrito.R;

public class MyEventsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    android.support.v4.view.PagerAdapter adapter;

    TextView createEventView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        tabLayout = findViewById(R.id.eventTabLayout);
        viewPager = findViewById(R.id.eventViewPager);

        createEventView = findViewById(R.id.createEventView);

        createEventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventFragment fragment = new CreateEventFragment();
                fragment.show(getSupportFragmentManager(), null);
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText(R.string.interested));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.going));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.my_event));

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        return null;
                }
                return null;
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

        //viewPager.setAdapter(adapter);
    }
}
