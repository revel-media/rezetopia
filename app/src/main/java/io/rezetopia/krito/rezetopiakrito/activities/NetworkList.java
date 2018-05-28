package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NetworkList extends AppCompatActivity {
    private static final String USER_ID = "USER_ID";
    private static final String User_NAME = "User_NAME";
    ArrayList<User> users;
    int user_id;
    String user_name;
    RecyclerView friendsRecyclerView;
    UserRecyclerAdapter adapter;
    View mCustomView;
    public RequestQueue requestQueue;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        LayoutInflater mInflater = LayoutInflater.from(this);
        userId = NetworkList.this.getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");
        mCustomView = mInflater.inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(mCustomView,layout);
        mActionBar.setDisplayShowCustomEnabled(true);
        friendsRecyclerView = findViewById(R.id.network_list);
        adapter=new UserRecyclerAdapter();

        getUsers();
    }
    private class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView username;


        public CommentViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.friendName);


        }

        public void bind(final User user){
            username.setText(user.getName());
            ((TextView)itemView.findViewById(R.id.id)).setText(String.valueOf(user.getId()));
            new MyAsyncTask(String.valueOf(user.getId()),(ImageView) itemView.findViewById(R.id.ppView)).execute();
            new MyAsyncTask1(String.valueOf(user.getId())).execute();
            new MyAsyncTask3().execute();
        }
    }
    private class UserRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder>{

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            final View view= LayoutInflater.from(NetworkList.this).inflate(R.layout.networklist_card,parent,false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
            holder.bind(users.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NetworkList.this, Chat.class);
                    intent.putExtra("guestUserId",((TextView)holder.itemView.findViewById(R.id.id)).getText());
                    startActivity(intent);
                   // Toast.makeText(getBaseContext(),((TextView)holder.itemView.findViewById(R.id.id)).getText(),Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
    private void getUsers(){
        String url = "https://rezetopiachat.firebaseio.com/users.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                // Toast.makeText(getBaseContext(),s, Toast.LENGTH_LONG).show();
                Log.i("response", "onResponse: " + s);
                users = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Iterator i = jsonObject.keys();
                    String name = "";
                    String id = "";
                    while (i.hasNext()){
                        name = i.next().toString();
                        id = new JSONObject(jsonObject.getString(name)).getString("id");
                        Log.i("response", "onResponse: " + name +" "+id);
                        User userResponse = new User();
                        userResponse.setName(name);
                        userResponse.setId(Integer.parseInt(id));
                        users.add(userResponse);
                        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NetworkList.this));
                        friendsRecyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    /*for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        userResponse.setName(object.getString("username"));
                        userResponse.setId(object.getInt("eventId"));
                        users.add(userResponse);
                        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NetworkList.this));
                        friendsRecyclerView.setAdapter(adapter);
                    }*/


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(NetworkList.this);
        rQueue.add(request);
    }
    private void getFriends(){
        StringRequest stringRequest = new  StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/friendlist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("friends_list", "onResponse: " + response);
//                        users = new ArrayList<>();
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                User userResponse = new User();
//                                JSONObject object = jsonArray.getJSONObject(i);
//                                userResponse.setName(object.getString("username"));
//                                userResponse.setId(object.getInt("eventId"));
//                                users.add(userResponse);
//                                friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NetworkList.this));
//                                friendsRecyclerView.setAdapter(adapter);
//                            }
//                        }
//                        catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
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
                map.put("id", userId);

                return map;

            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(NetworkList.this);
        rQueue.add(stringRequest);
    }
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
         String id;
         ImageView imageView;


        public MyAsyncTask(String id,ImageView imageView) {
            this.id = id;
            this.imageView =  imageView;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest request = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/getInfo.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    try {
                        final JSONObject jsonObject;
                        jsonObject = new JSONObject(response);
                        //Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                        if (jsonObject.getString("msg").equals("succ")) {
                            if (jsonObject.getString("img") != null && !jsonObject.getString("img").contentEquals("")) {
                                Picasso.with(getApplicationContext())
                                        .load("http://rezetopia.dev-krito.com/images/profileImgs/" + jsonObject.getString("img") + ".JPG")
                                        .placeholder(R.drawable.post_pp_circle).into(imageView);
                            }

                            //probar.setVisibility(View.GONE);
                            // new DownloadImage(playerImg).execute("http://rezetopia.dev-krito.com/images/profileImgs/"+jsonObject.getString("img")+".JPG");
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class MyAsyncTask1 extends AsyncTask<Void, Void, Void> {
        String id;


        public MyAsyncTask1(String id) {
            this.id = id;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "https://rezetopiachat.firebaseio.com/messages/"+userId+"_"+id+".json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                String lastMessage;
                @Override
                public void onResponse(String s) {
                    // Toast.makeText(getBaseContext(),s, Toast.LENGTH_LONG).show();
                    Log.i("msg", "onResponse: " + s);

                    if (s != null){
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Iterator i = jsonObject.keys();
                            while (i.hasNext()){

                                lastMessage = new JSONObject(jsonObject.getString(i.next().toString())).getString("message");
                                Log.i("lastMsg", "onResponse: " + lastMessage);

                            }
                            Log.i("don", "onResponse: " + lastMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /*for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        userResponse.setName(object.getString("username"));
                        userResponse.setId(object.getInt("eventId"));
                        users.add(userResponse);
                        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(NetworkList.this));
                        friendsRecyclerView.setAdapter(adapter);
                    }*/


                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                }
            });
            RequestQueue rQueue = Volley.newRequestQueue(NetworkList.this);
            rQueue.add(request);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    public class MyAsyncTask3 extends AsyncTask<Void, Void, Void> {



        public MyAsyncTask3() {


        }

        @Override
        protected Void doInBackground(Void... voids) {
            getFriends();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

