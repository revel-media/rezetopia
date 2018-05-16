package io.rezetopia.krito.rezetopiakrito.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Mona Abdallh on 4/30/2018.
 */

public class GroupAddFriend extends Fragment {

    Button addButton;

    ArrayList<User> users;
    RecyclerView friendsRecyclerView;
    UserRecyclerAdapter adapter;

    int user_id;
    String groupId;

    public static GroupAddFriend createFragment(String group_id){
        Bundle bundle = new Bundle();
        bundle.putString("group_id", group_id);
        GroupAddFriend fragment = new GroupAddFriend();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_add_friend, container, false);

        friendsRecyclerView = view.findViewById(R.id.friendsRecView);
        addButton = view.findViewById(R.id.addButton);

        groupId = getArguments().getString("group_id");
        getUsers();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (users != null){
                    performAddFriend();
                }
            }
        });

        return view;
    }


    private class FriendsViewHolder extends RecyclerView.ViewHolder{

        TextView username;
        ImageView deleteView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            deleteView = itemView.findViewById(R.id.deleteView);
        }

        public void bind(final User user){
            username.setText(user.getName());
        }
    }

    private class UserRecyclerAdapter extends RecyclerView.Adapter<FriendsViewHolder>{

        @NonNull
        @Override
        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(getActivity()).inflate(R.layout.add_friend_card,parent,false);
            return new FriendsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsViewHolder holder, final int position) {
            holder.bind(users.get(position));

            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    users.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    private void getUsers(){
        StringRequest stringRequest = new  StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_group.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("friends_response", "onResponse: " + response);
                        users = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("message").contentEquals("ok")){
                                JSONArray jsonArray = jsonObject.getJSONArray("users");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    User userResponse = new User();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    userResponse.setName(object.getString("username"));
                                    userResponse.setId(object.getInt("eventId"));
                                    users.add(userResponse);
                                    friendsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    adapter = new UserRecyclerAdapter();
                                    friendsRecyclerView.setAdapter(adapter);
                                }
                            } else {

                            }

                        }
                        catch (JSONException e) {
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

                String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");
                map.put("method", "get_friends_not_members");
                map.put("user_id", userId);
                map.put("group_id", groupId);

                return map;

            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void performAddFriend(){
        StringRequest stringRequest = new  StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_group.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("performAddFriend", "onResponse: " + response);
                        users.clear();
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("performAddFriend_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> map = new HashMap<>();

                map.put("method", "add_friends");
                map.put("group_id", groupId);
                map.put("size", String.valueOf(users.size()));

                for (int i = 0; i < users.size(); i++) {
                    map.put(String.valueOf(i), String.valueOf(users.get(i).getId()));
                }

                return map;

            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
