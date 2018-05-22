package io.rezetopia.krito.rezetopiakrito.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkList extends AppCompatActivity {
    private static final String USER_ID = "USER_ID";
    private static final String User_NAME = "User_NAME";
    ArrayList<User> users;
    int user_id;
    String user_name;
    RecyclerView friendsRecyclerView;
    UserRecyclerAdapter adapter;
    View mCustomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.action_bar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(mCustomView,layout);
        mActionBar.setDisplayShowCustomEnabled(true);
        friendsRecyclerView = findViewById(R.id.network_list);
        adapter=new UserRecyclerAdapter();
        //getUsers();
    }
    private class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView username;

        public CommentViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.friendName);

        }

        public void bind(final User user){
            username.setText(user.getName());
        }
    }
    private class UserRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder>{

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(NetworkList.this).inflate(R.layout.networklist_card,parent,false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            holder.bind(users.get(position));
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
                    Toast.makeText(getBaseContext(),jsonObject.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    /*for (int i = 0; i < jsonArray.length(); i++) {
                        User userResponse = new User();
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
}

