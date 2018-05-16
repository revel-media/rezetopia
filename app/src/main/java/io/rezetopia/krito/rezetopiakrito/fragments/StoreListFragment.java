package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.StoreActivity;
import io.rezetopia.krito.rezetopiakrito.model.pojo.store.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.store.StoreResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mona Abdallh on 5/7/2018.
 */

public class StoreListFragment extends Fragment {

    private static final String ALL_STORES_METHOD = "get_all_stores";
    private static final String MY_STORES_METHOD = "get_my_stores";

    String type;
    String userId;
    ArrayList<StoreResponse> stores;
    RecyclerView.Adapter adapter;
    RecyclerView storesRecView;

    public static StoreListFragment createFragment(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        StoreListFragment fragment = new StoreListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);

        type = getArguments().getString("type");

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        storesRecView = view.findViewById(R.id.storesRecView);
        stores = new ArrayList<>();

        performGetStores();
        return view;
    }

    private void performGetStores(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_store.php",
                ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if (!response.isError()){
                            Log.i("onResponseVendor", "onResponse: " + response.getStores()[0].getName());
                            stores = new ArrayList<>(Arrays.asList(response.getStores()));
                            updateUi();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onErrorResponseVendor", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                if (type.contentEquals("all")){
                    map.put("method", ALL_STORES_METHOD);
                } else {
                    map.put("method", MY_STORES_METHOD);
                    map.put("userId", userId);
                }

                map.put("cursor", String.valueOf(0));

                return map;
            }
        };


        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ppView;
        TextView storeNameView;
        TextView storeDecView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            storeNameView = itemView.findViewById(R.id.storeNameView);
            storeDecView = itemView.findViewById(R.id.storeDecView);
            ppView = itemView.findViewById(R.id.ppView);
        }

        public void bind(StoreResponse response){
            storeNameView.setText(response.getName());
            storeDecView.setText(response.getDescription());

            if (response.getImageUrl() != null){
                Picasso.with(getActivity()).load(response.getImageUrl()).into(ppView);
            }
        }
    }

    private class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder>{


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.store_item_card, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
            holder.bind(stores.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stores.get(position).getOwnerId() == Integer.parseInt(userId)) {
                        Intent intent = StoreActivity.createIntent(stores.get(position).getId(), getActivity());
                        startActivity(intent);
                    } else {

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return stores.size();
        }
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new ItemRecyclerAdapter();
            storesRecView.setAdapter(adapter);
            storesRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
