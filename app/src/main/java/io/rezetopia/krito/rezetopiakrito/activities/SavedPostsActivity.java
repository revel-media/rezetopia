package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.ListPopupWindowAdapter;
import io.rezetopia.krito.rezetopiakrito.helper.MenuCustomItem;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SavedPostsActivity extends AppCompatActivity {

    private static final int COMMENT_ACTIVITY_RESULT = 1001;

    ArrayList<PostResponse> posts;
    long now;
    int oldCount = 0;
    int newCount = 0;
    int nextCursor = 0;
    String userId;

    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
        posts = new ArrayList<>();


        recyclerView = findViewById(R.id.savedPostsRecView);
        fetchSavedPosts();
    }

    private class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTextView;
        Button likeButton;
        Button commentButton;
        TextView dateView;
        TextView usernameView;
        ImageView ppView;
        ImageView postSideMenu;
        ImageView hiddenMenuPositionView;
        RecyclerView.Adapter adapter;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
            ppView = itemView.findViewById(R.id.ppView);
            postSideMenu = itemView.findViewById(R.id.postSideMenu);
            //hiddenMenuPositionView = itemView.findViewById(R.id.hiddenMenuPositionView);
        }

        public void bind(final PostResponse item, final int pos) {
            String postText = null;
            if (item.getUsername() != null){
                usernameView.setText(item.getUsername());
            }
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = date.getTime();
            long millisecondsFromNow = milliseconds - now;
            dateView.setText(DateUtils.getRelativeDateTimeString(SavedPostsActivity.this, milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

            try {
                postText = URLEncoder.encode(item.getText(), "ISO-8859-1");
                postText = URLDecoder.decode(postText, "UTF-8");
                postTextView.setText(postText);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (item.getLikes() != null && item.getLikes().length > 0){
                //likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                likeButton.setText(item.getLikes().length + " Like");


                //Log.e("loggedInUserId", userId);
                for (int id : item.getLikes()) {
                    //Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                        break;
                    }
                }
            }

            if (item.getCommentSize() > 0){
                commentButton.setText(item.getCommentSize() + " Comment");
            }

            /*if (item.getPostComments() != null && item.getPostComments().length > 0){
                //commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                commentButton.setText(item.getPostComments().length + " Comment");
            }*/

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = CommentActivity.createIntent(item.getLikes(), item.getPostId(), now, Integer.parseInt(item.getUserId()),
                            SavedPostsActivity.this);

                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);

                    //startActivity(intent);
                    SavedPostsActivity.this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    /*ArrayList<CommentResponse> comments = new ArrayList<>();
                    if (item.getPostComments() != null){
                        comments = new ArrayList<>(Arrays.asList(item.getPostComments()));
                    }

                    Intent intent = CommentActivity.createIntent(comments, item.getLikes(), item.getId(), now, item.getOwnerId(),
                            SavedPostsActivity.this);

                    adapterPos = pos;
                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);

                    //startActivity(intent);
                    SavedPostsActivity.this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);*/
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String likeString = SavedPostsActivity.this.getResources().getString(R.string.like);

                    if (item.getLikes() != null) {
                        for (int i = 0; i < item.getLikes().length; i++) {
                            if (item.getLikes()[i] == Integer.parseInt(userId)) {

                                if (item.getLikes().length > 1) {
                                    likeButton.setText((item.getLikes().length - 1) + " " + likeString);
                                } else {
                                    likeButton.setText(likeString);
                                }
                                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                                reverseLike(item, pos);
                                return;
                            }
                        }
                    }


                    likeButton.setText((item.getLikes().length + 1) + " " + likeString);
                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                    performLike(item, pos);

                }
            });

            /*usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getComments() == Integer.parseInt(userId)){
                        mListener.onProfile();
                    } else if (item.getType() == NewsFeedItem.POST_TYPE){
                        startOtherProfile(pos);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getOwnerId() == Integer.parseInt(userId)){
                        mListener.onProfile();
                    } else if (item.getType() == NewsFeedItem.POST_TYPE){
                        startOtherProfile(pos);
                    }
                }
            });*/

            postSideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(item.getPostId()).contentEquals(String.valueOf(userId))) {
                        showPostPopupWindow(postSideMenu, true, item.getPostId(), Integer.parseInt(item.getUserId()));
                    } else {
                        showPostPopupWindow(postSideMenu, false, item.getPostId(), Integer.parseInt(item.getUserId()));
                    }
                }
            });
        }

        private void startOtherProfile(int position){
            Intent intent = OtherProfileActivity.createIntent(
                    String.valueOf(posts.get(position).getUserId()),
                    posts.get(position).getUsername(),
                    null,
                    SavedPostsActivity.this);
            startActivity(intent);
        }

        private void performLike(final PostResponse item, final int pos){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    int[] likes = new int[item.getLikes().length + 1];
                                    for (int i = 0; i < item.getLikes().length; i++) {
                                        likes[i] = item.getLikes()[i];
                                    }

                                    likes[likes.length - 1] = Integer.parseInt(userId);
                                    item.setLikes(likes);
                                    adapter.notifyItemChanged(pos);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("like_error", "onErrorResponse: " + error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();

                    map.put("method", "post_like");
                    map.put("userId", userId);
                    map.put("owner_id", String.valueOf(item.getUserId()));
                    map.put("post_id", String.valueOf(item.getPostId()));
                    map.put("add_like", String.valueOf(true));

                    return map;
                }
            };

            Volley.newRequestQueue(SavedPostsActivity.this).add(stringRequest);
        }

        private void reverseLike(final PostResponse item, final int pos){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){

                                    ArrayList<Integer> likesList = new ArrayList<>();

                                    for (int id : item.getLikes()) {
                                        if (id != Integer.parseInt(userId)){
                                            likesList.add(id);
                                        }
                                    }

                                    int[] likes = new int[likesList.size()];

                                    for(int i = 0; i < likesList.size(); i++) {
                                        likes[i] = likesList.get(i);
                                    }

                                    item.setLikes(likes);
                                    adapter.notifyItemChanged(pos);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("unlike_error", "onErrorResponse: " + error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();

                    map.put("method", "post_like");
                    map.put("userId", userId);
                    map.put("owner_id", item.getUserId());
                    map.put("post_id", String.valueOf(item.getPostId()));
                    map.put("remove_like", String.valueOf(true));

                    return map;
                }
            };

            Volley.newRequestQueue(SavedPostsActivity.this).add(stringRequest);
        }
    }

    private class PostRecyclerAdapter extends RecyclerView.Adapter< PostViewHolder>{

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SavedPostsActivity.this).inflate(R.layout.post_card, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            holder.bind(posts.get(position), position);
        }

        @Override
        public int getItemCount() {
            return posts.size() + 1;
        }
    }

    private void fetchSavedPosts(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php", ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if (!response.isError()){
                            Log.i("savedResponse", "onResponse: " + response.getPosts()[0].getCreatedAt());
                            if (posts.size() > 0)
                                posts.addAll(Arrays.asList(response.getPosts()));
                            else if (response.getPosts() != null)
                                posts = new ArrayList<>(Arrays.asList(response.getPosts())) ;

                            newCount = posts.size()-1;
                            nextCursor = response.getNextCursor();
                            now = response.getNow();
                            //Collections.shuffle(newsFeedItems);
                            updateUi();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("savedResponse_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("method", "get_saved_posts");
                map.put("userId", userId);
                map.put("cursor", String.valueOf(nextCursor));

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new PostRecyclerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showPostPopupWindow(View anchor, final boolean owner, final int postId, final int postOwnerId) {
        final ListPopupWindow popupWindow = new ListPopupWindow(SavedPostsActivity.this);

        List<MenuCustomItem> itemList = new ArrayList<>();
        if (owner){
            itemList.add(new MenuCustomItem(SavedPostsActivity.this.getResources().getString(R.string.edit), R.drawable.ic_edit));
            itemList.add(new MenuCustomItem(SavedPostsActivity.this.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(SavedPostsActivity.this.getResources().getString(R.string.remove), R.drawable.ic_remove));
        } else {
            itemList.add(new MenuCustomItem(SavedPostsActivity.this.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(SavedPostsActivity.this.getResources().getString(R.string.report_post), R.drawable.ic_report));
        }


        ListAdapter adapter = new ListPopupWindowAdapter(SavedPostsActivity.this, itemList, R.layout.custom_menu);
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*if (String.valueOf(postOwnerId).contentEquals(String.valueOf(userId))){
                    if (i == 0){
                        //todo edit post
                    } else if (i == 1){
                        savePost(postId);
                    } else if (i == 2){
                        removePost(postId);
                    }
                } else {
                    if (i == 0){
                        savePost(postId);
                    } else if (i == 1){
                        reportPost(postId);
                    }
                }*/
                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }
}
