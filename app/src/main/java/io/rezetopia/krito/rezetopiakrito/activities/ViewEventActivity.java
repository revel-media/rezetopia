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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.fragments.EventDescriptionFragment;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.EventResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewEventActivity extends AppCompatActivity {

    private static final String EVENT_ID_EXTRA = "event_id";

    int eventId;
    boolean isGoing;
    boolean isInterested;
    String userId;

    RequestQueue requestQueue;
    EventResponse eventResponse;

    TabLayout tabLayout;
    ViewPager viewPager;
    ProgressBar groupProgressBar;
    TextView eventNameView;
    TextView occurDateView;
    Button goingButton;
    Button interestedButton;


    android.support.v4.view.PagerAdapter adapter;

    public static Intent createIntent(int id, Context context){
        Intent intent = new Intent(context, ViewEventActivity.class);
        intent.putExtra(EVENT_ID_EXTRA, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        tabLayout = findViewById(R.id.groupTabLayout);
        viewPager = findViewById(R.id.eventViewPager);
        groupProgressBar = findViewById(R.id.eventProgressBar);
        eventNameView = findViewById(R.id.groupNameView);
        occurDateView = findViewById(R.id.occurDateView);
        goingButton = findViewById(R.id.goingButton);
        interestedButton = findViewById(R.id.interestedButton);

        groupProgressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.description));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.discussion));

        eventId = getIntent().getIntExtra(EVENT_ID_EXTRA, 0);
        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
        requestQueue = Volley.newRequestQueue(this);
        fetchEvent();
    }

    private void updateUi(){
        String occurDate = getResources().getString(R.string.occur_date);
        eventNameView.setText(eventResponse.getName());
        occurDateView.setText(occurDate + " " + eventResponse.getOccurDate());

        adapter = new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        EventDescriptionFragment descriptionFragment = EventDescriptionFragment.createFragment(eventResponse.getDescription());
                        return descriptionFragment;
                    case 1:
                        EventDescriptionFragment des1 = EventDescriptionFragment.createFragment(eventResponse.getDescription());
                        return des1;

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

    private void fetchEvent(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_event.php",
                EventResponse.class,
                new Response.Listener<EventResponse>() {
                    @Override
                    public void onResponse(EventResponse response) {
                        if (response.getName() != null){
                            Log.i("event_response", "onResponse: " + response.getName());
                            groupProgressBar.setVisibility(View.GONE);
                            eventResponse = response;
                            isGoing();
                            isInterested();
                            updateUi();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "get_event");
                map.put("eventId", String.valueOf(eventId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void isGoing(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_event.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                isGoing = true;
                                goingButton.setBackground(getResources().getDrawable(R.drawable.dark_button));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "is_going");
                map.put("eventId", String.valueOf(eventId));
                map.put("userId", userId);

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void isInterested(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_event.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                isInterested = true;
                                interestedButton.setBackground(getResources().getDrawable(R.drawable.dark_button));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "is_interested");
                map.put("eventId", String.valueOf(eventId));
                map.put("userId", userId);

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}
