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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.BuildProfile;
import io.rezetopia.krito.rezetopiakrito.activities.OtherProfileActivity;
import io.rezetopia.krito.rezetopiakrito.activities.UserImageActivity;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Requests extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    ArrayList<User> users;
    RecyclerView.Adapter adapter;
    String userId;
    String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Toast.makeText(getActivity(), "open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request, container, false);
        swipeRefreshLayout=view.findViewById(R.id.swipe_request);
        recyclerView =  view.findViewById(R.id.requests_list);
        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
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
           // Toast.makeText(getActivity(), "inner", Toast.LENGTH_SHORT).show();
            new NameAsync().execute();
            new ReadAsync().execute();
            if (user.getImageUrl() != null){
                Picasso.with(getActivity()).load(user.getHeight()).into(img);
            }

            sugName.setText(user.getName());

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AcceptAsync().execute(String.valueOf(user.getId()),String.valueOf(user.getRequestId()),user.getName(),userName);
                    users.remove(position);
                    users = new ArrayList<>(users);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, users.size());
                }
            });

            refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RemoveAsync().execute(String.valueOf(user.getId()),String.valueOf(user.getRequestId()));
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
            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/friend_request.php", ApiResponse.class,
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

                    String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
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
            String url4 = "https://rezetopiachat.firebaseio.com/requests.json";
            StringRequest request4 = new StringRequest(Request.Method.GET, url4, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                        Log.d("check_remove",obj2.getString("count"));
                       // reference.child("friends_pending_"+userId).child(strings[0]).removeValue();
                        int val = Integer.parseInt(obj2.getString("count"));
                        if (val !=0){
                            val = val-=1;
                            reference.child("friends_pending_"+userId).child("count").setValue(val);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue4 = Volley.newRequestQueue(getActivity());
            rQueue4.add(request4);
            String url2 = "https://rezetopiachat.firebaseio.com/friends/friends_"+userId+".json";
            StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    Log.d("check_rse",s.toString());
                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/friends/friends_"+userId);

                    if(s.equals("null")) {
                        reference.child("count").setValue(1);
                       // reference.child("messages").setValue(1);
                        reference.child(strings[0]).child("id").setValue(strings[0]);
                        reference.child(strings[0]).child("name").setValue(strings[2]);
                        reference.child(strings[0]).child("time").setValue(System.currentTimeMillis()/1000);
                        reference.child(strings[0]).child("lastMsg").setValue(getResources().getString(R.string.startMsg));
                        reference.child(strings[0]).child("read").setValue("0");
                    }
                    else {

                        try {
                            JSONObject obj = new JSONObject(s);
                            Log.d("check_rse",obj.getString("count"));
                            if (obj.has("count")) {
                                int val = Integer.parseInt(obj.getString("count"));
                                val = val+=1;

                                reference.child("count").setValue(val);
                               // reference.child("messages").setValue(1);
                                reference.child(strings[0]).child("id").setValue(strings[0]);
                                reference.child(strings[0]).child("name").setValue(strings[2]);
                                reference.child(strings[0]).child("time").setValue(System.currentTimeMillis()/1000);
                                reference.child(strings[0]).child("lastMsg").setValue(getResources().getString(R.string.startMsg));
                                reference.child(strings[0]).child("read").setValue("0");
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue1 = Volley.newRequestQueue(getActivity());
            rQueue1.add(request2);
            String url3 = "https://rezetopiachat.firebaseio.com/friends/friends_"+strings[0]+".json";
            StringRequest request3 = new StringRequest(Request.Method.GET, url3, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    Log.d("check_rse",s.toString());
                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/friends/friends_"+strings[0]);

                    if(s.equals("null")) {
                        reference.child("count").setValue(1);
                        //reference.child("messages").setValue(1);
                        reference.child(userId).child("id").setValue(userId);
                        reference.child(userId).child("name").setValue(strings[3]);
                        reference.child(userId).child("time").setValue(System.currentTimeMillis()/1000);
                        reference.child(userId).child("lastMsg").setValue(getResources().getString(R.string.startMsg));
                        reference.child(userId).child("read").setValue("0");
                    }
                    else {

                        try {
                            JSONObject obj = new JSONObject(s);
                            Log.d("check_rse",obj.getString("count"));
                            if (obj.has("count")) {
                                int val = Integer.parseInt(obj.getString("count"));
                                val = val+=1;

                                reference.child("count").setValue(val);
                               // reference.child("messages").setValue(1);
                                reference.child(userId).child("id").setValue(userId);
                                reference.child(userId).child("name").setValue(strings[3]);
                                reference.child(userId).child("time").setValue(System.currentTimeMillis()/1000);
                                reference.child(userId).child("lastMsg").setValue(getResources().getString(R.string.startMsg));
                                reference.child(userId).child("read").setValue("0");
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue2 = Volley.newRequestQueue(getActivity());
            rQueue2.add(request3);
            StringRequest post = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/friend_request.php",
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


                    params.put("id", strings[1]);
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

            String url2 = "https://rezetopiachat.firebaseio.com/requests.json";
            StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                    try {
                        JSONObject obj = new JSONObject(s);
                            JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                            Log.d("check_remove",obj2.getString("count"));
                            reference.child("friends_pending_"+userId).child(strings[0]).removeValue();
                            int val = Integer.parseInt(obj2.getString("count"));
                            if (val !=0){
                                val = val-=1;
                                reference.child("friends_pending_"+userId).child("count").setValue(val);
                            }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue1 = Volley.newRequestQueue(getActivity());
            rQueue1.add(request2);
            StringRequest post = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/friend_request.php",
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

                    params.put("id", strings[1]);
                    params.put("method", "remove");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }

    }
    private class ReadAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(final Void... voids) {

            String url2 = "https://rezetopiachat.firebaseio.com/requests.json";
            StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                        Log.d("check_remove",obj2.getString("count"));
                        int val = Integer.parseInt(obj2.getString("count"));
                        if (val !=0){
                            val = val-=1;
                            reference.child("friends_pending_"+userId).child("count").setValue(val);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue1 = Volley.newRequestQueue(getActivity());
            rQueue1.add(request2);
            return null;
        }

    }
    private class NameAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(final Void... voids) {
            RequestQueue rQueue1 = Volley.newRequestQueue(getActivity());
            getUser(userId,rQueue1);
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
    private void getUser(final String id, RequestQueue requestQueue) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/getInfo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                try {
                    final JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    //Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("msg").equals("succ")) {
                         userName = jsonObject.getString("name");
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("id", id);
                parameters.put("getInfo", "");


                return parameters;
            }
        };
        requestQueue.add(request);

    }


}
