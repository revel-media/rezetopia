package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.OtherProfileActivity;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class Requests extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    ArrayList<User> users;
    RecyclerView.Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request, container, false);
        swipeRefreshLayout=view.findViewById(R.id.swipe_request);
        recyclerView =  view.findViewById(R.id.requests_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UsersAsync().execute();
            }
        });
        new UsersAsync().execute();

        return view;
    }

    private class RequestViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView sugName;
        Button accept;
        Button refuse;

        public RequestViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            sugName = itemView.findViewById(R.id.sugName);
            accept = itemView.findViewById(R.id.accept);
            refuse = itemView.findViewById(R.id.refuse);
        }

        public void bind(final User user, final int position){
            if (user.getImageUrl() != null){
                Picasso.with(getActivity()).load(user.getHeight()).into(img);
            }

            sugName.setText(user.getName());

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AcceptAsync().execute(String.valueOf(user.getRequestId()));
                    users.remove(position);
                    users = new ArrayList<>(users);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, users.size());
                }
            });

            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RemoveAsync().execute(String.valueOf(user.getRequestId()));
                    users.remove(position);
                    users = new ArrayList<>(users);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, users.size());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = OtherProfileActivity.createIntent(
                            String.valueOf(user.getId()),
                            user.getName(),
                            null,
                            getActivity());
                }
            });
        }
    }

    private class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestViewHolder>{

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.request_row, parent, false);

            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            holder.bind(users.get(position), position);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    private class UsersAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/friend_request.php", ApiResponse.class,
                    new Response.Listener<ApiResponse>() {
                        @Override
                        public void onResponse(ApiResponse response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.getUsers() != null) {
                                Log.i("friend_request", "onResponse: " + response.getUsers()[0].getName());
                                users = new ArrayList<>(Arrays.asList(response.getUsers()));
                                updateUi();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
                    params.put("id", userId);
                    params.put("method", "get_requests");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }

    }

    private class AcceptAsync extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            StringRequest post = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/friend_request.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    Log.i("accept_friend_request", "onResponse: " + response);
                                } else {
                                    Log.i("accept_friend_request", "onResponse: " + response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();


                    params.put("id", strings[0]);
                    params.put("method", "accept");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }

    }

    private class RemoveAsync extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            StringRequest post = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/friend_request.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    Log.i("accept_friend_request", "onResponse: " + response);
                                } else {
                                    Log.i("accept_friend_request", "onResponse: " + response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    params.put("id", strings[0]);
                    params.put("method", "remove");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }

    }

    private void updateUi(){
        if (adapter == null){
            adapter = new RequestRecyclerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

}
