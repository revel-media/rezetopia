package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.fragments.GroupAddFriend;
import io.rezetopia.krito.rezetopiakrito.fragments.GroupDescription;
import io.rezetopia.krito.rezetopiakrito.fragments.GroupHome;
import io.rezetopia.krito.rezetopiakrito.fragments.GroupReport;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.group.GroupResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private static final String GROUP_ID_EXTRA = "item_id";
    private static final String GROUP_NAME_EXTRA = "item_name";

    TextView groupNameView;
    TextView memberNumberView;
    TabLayout tabLayout;
    ViewPager viewPager;
    ProgressBar groupProgressBar;
    TextView groupStatusView;

    android.support.v4.view.PagerAdapter adapter;
    RequestQueue requestQueue;

    int groupId;
    String groupName;
    boolean isMember;
    GroupResponse group;


    public static Intent createIntent(int groupId, String groupName, Context context){
        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra(GROUP_ID_EXTRA, groupId);
        intent.putExtra(GROUP_NAME_EXTRA, groupName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupNameView = findViewById(R.id.groupNameView);
        memberNumberView = findViewById(R.id.memberNumberView);
        tabLayout = findViewById(R.id.groupTabLayout);
        viewPager = findViewById(R.id.groupViewPager);
        groupProgressBar = findViewById(R.id.groupProgressBar);
        groupStatusView = findViewById(R.id.groupStatusView);

        groupProgressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        groupId = getIntent().getIntExtra(GROUP_ID_EXTRA, 0);
        groupName = getIntent().getStringExtra(GROUP_NAME_EXTRA);

        requestQueue = Volley.newRequestQueue(this);
        performIsMember();
    }

    private void groupMember(){
        tabLayout.addTab(tabLayout.newTab().setText(R.string.home));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.report));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.description));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.add_friend));

        adapter = new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        GroupHome home = GroupHome.createFragment(String.valueOf(groupId), groupName);
                        return home;
                    case 1:
                        GroupReport report = new GroupReport();
                        return report;
                    case 2:
                        GroupDescription description = GroupDescription.createFragment(String.valueOf(groupId));
                        return description;
                    case 3:
                        GroupAddFriend addFriend = GroupAddFriend.createFragment(String.valueOf(groupId));
                        return addFriend;
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

        performGetGroup();
    }

    private void groupGuest(){
        tabLayout.addTab(tabLayout.newTab().setText(R.string.description));

        adapter = new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        GroupDescription description =GroupDescription.createFragment(String.valueOf(groupId));
                        return description;
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

    private void performIsMember(){
        StringRequest customRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_group.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("is_member", "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            isMember = jsonObject.getBoolean("is_member");

                            groupProgressBar.setVisibility(View.GONE);
                            if (isMember){
                                groupMember();
                            } else {
                                groupGuest();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("is_member_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                String userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                map.put("method", "is_member");
                map.put("user_id", userId);
                map.put("group_id", String.valueOf(groupId));

                return map;
            }
        };

        requestQueue.add(customRequest);
    }

    private void performGetGroup(){
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_group.php",
                GroupResponse.class,
                new Response.Listener<GroupResponse>() {
                    @Override
                    public void onResponse(GroupResponse response) {
                        Log.i("get_group", "onResponse: " + response.getDescription());
                        group = response;
                        if (response.getPrivacy().contentEquals("closed")){
                            groupStatusView.setText(R.string.closed);
                        } else if (response.getPrivacy().contentEquals("public")){
                            groupStatusView.setText(R.string.public_);
                        } else if (response.getPrivacy().contentEquals("secret")){
                            groupStatusView.setText(R.string.secret);
                        }

                        groupNameView.setText(response.getName());
                        String member = getResources().getString(R.string.member);
                        memberNumberView.setText(response.getMemberCount() + " " + member);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("get_group_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "get_group");
                map.put("group_id", String.valueOf(groupId));

                return map;
            }
        };

        requestQueue.add(customRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
