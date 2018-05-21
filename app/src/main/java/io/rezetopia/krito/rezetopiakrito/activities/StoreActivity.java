package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.fragments.BuyRequestFragment;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.fragments.StoreProductsFragment;
import io.rezetopia.krito.rezetopiakrito.model.pojo.store.StoreResponse;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreActivity extends AppCompatActivity {

    private static final String STORE_ID_EXTRA = "store_id_extra";

    TabLayout tabLayout;
    ViewPager viewPager;
    android.support.v4.view.PagerAdapter adapter;
    RelativeLayout createHeader;
    Button createPostButton;
    Button createProductButton;
    CircleImageView vendorPpView;
    ImageView coverPpView;
    TextView vendorNameView;
    TextView vendorAddressView;

    RequestQueue requestQueue;


    StoreResponse store;
    int storeId;

    public static Intent createIntent(int id, Context context){
        Intent intent = new Intent(context, StoreActivity.class);
        intent.putExtra(STORE_ID_EXTRA, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        tabLayout = findViewById(R.id.vendorTabLayout);
        viewPager = findViewById(R.id.vendorViewPager);
        createHeader = findViewById(R.id.createVendorHeader);
        createPostButton = findViewById(R.id.createPostButton);
        createProductButton = findViewById(R.id.createProductButton);
        vendorPpView = findViewById(R.id.vendorPpView);
        //coverPpView = findViewById(R.id.coverPpView);
        vendorNameView = findViewById(R.id.vendorNameView);
        vendorAddressView = findViewById(R.id.vendorAddressView);

        requestQueue = Volley.newRequestQueue(this);
        storeId = getIntent().getIntExtra(STORE_ID_EXTRA, 0);
        performGetStore();
    }


    private void performGetStore(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_store.php",
                StoreResponse.class,
                new Response.Listener<StoreResponse>() {
                    @Override
                    public void onResponse(StoreResponse response) {
                        if (!response.isError()){
                            store = response;
                            afterFetch();
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

                map.put("method", "get_store");
                map.put("store_id", String.valueOf(storeId));

                return map;
            }
        };


        requestQueue.add(stringRequest);
    }

    private void afterFetch(){
        updateUi();

        if (store.getImageUrl() != null){
            Picasso.with(this).load(store.getImageUrl()).into(vendorPpView);
        }

        vendorNameView.setText(store.getName());
        vendorAddressView.setText(store.getAddress());
        createHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        vendorPpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = CreateProductActivity.createIntent(String.valueOf(storeId), StoreActivity.this);
               startActivity(intent);
            }
        });
    }

    private void updateUi(){
        tabLayout.addTab(tabLayout.newTab().setText(R.string.product));
        tabLayout.addTab(tabLayout.newTab().setText("Requests"));
        /*tabLayout.addTab(tabLayout.newTab().setText(R.string.review));*/

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch(position)
                {
                    case 0:
                        StoreProductsFragment product = StoreProductsFragment.createFragment(storeId);
                        return product;
                    case 1:
                        BuyRequestFragment request = BuyRequestFragment.createFragment(storeId);
                        return request;
                    /*case 2:
                        ReviewsFragment reviews = new ReviewsFragment();
                        return reviews;*/
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
