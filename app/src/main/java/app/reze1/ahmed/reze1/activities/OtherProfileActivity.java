package app.reze1.ahmed.reze1.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import app.reze1.ahmed.reze1.fragments.AlertFragment;
import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.post.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.post.CommentResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.model.pojo.search.SearchItem;
import app.reze1.ahmed.reze1.model.pojo.search.SearchResponse;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.ResizeWidthAnimation;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import app.reze1.ahmed.reze1.model.pojo.user.User;
import de.hdodenhof.circleimageview.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OtherProfileActivity extends AppCompatActivity {

    private static final String USER_ID_EXTRA = "profile_other_user_id_extra";
    private static final String USERNAME_EXTRA = "profile_other_username_extra";
    private static final String PP_IMAGE__EXTRA = "profile_other_username_extra";
    private static final int COMMENT_ACTIVITY_RESULT = 1001;
    private static final int CREATE_POST_RESULT = 1002;
    private static final int VIEW_HEADER = 1;
    private static final int VIEW_ITEM = 2;

    boolean searchUsers = false;
    boolean searchGroups = false;

    ArrayList<SearchItem> searchItems;

    ImageView backView;
    EditText searchBox;

    long now;
    String profileId;
    String username;
    int adapterPos;
    PostResponse[] posts;
    RequestQueue requestQueue;
    int nextCursor = 0;
    String q;
    int searchBoxWidth = 300;
    String userId;
    String guestUserId;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    private RecyclerView.Adapter postsAdapter;
    RecyclerView postsRecyclerView;
    public Button addBtn;
    private RecyclerView.Adapter searchAdapter;
    RecyclerView searchRecyclerView;

    public static Intent createIntent(String userId, String username, String ImageUrl, Context context){
        Intent intent = new Intent(context, OtherProfileActivity.class);
        intent.putExtra(USER_ID_EXTRA, userId);
        intent.putExtra(USERNAME_EXTRA, username);
        intent.putExtra(PP_IMAGE__EXTRA, ImageUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        User user = new User();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(user.getId()));
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();



        postsRecyclerView = findViewById(R.id.otherProfileRecView);
        searchRecyclerView = findViewById(R.id.otherSearchRecView);

        searchBox = findViewById(R.id.searchView);
        guestUserId = getIntent().getStringExtra(USER_ID_EXTRA);
        backView = findViewById(R.id.searchBackArrow);

        userId = OtherProfileActivity.this.getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0 && !s.toString().contentEquals("0")){
                    q = s.toString();
                    performSearch(q);
                } else {
                    searchGroups = false;
                    searchUsers = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResizeWidthAnimation anim = new ResizeWidthAnimation(searchBox, 800);
                anim.setDuration(20);
                searchBox.startAnimation(anim);
                searchBox.setFocusable(true);
                if (searchItems == null){
                    searchItems = new ArrayList<>();
                }
                updateSearchUi();
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchAdapter != null) {
                    ViewGroup.LayoutParams lp = searchBox.getLayoutParams();
                    lp.width = searchBoxWidth;
                    searchBox.setLayoutParams(lp);
                    searchBox.setText("");
                    searchBox.setFocusable(false);
                    updatePostsUi();
                } else {
                    onBackPressed();
                }
            }
        });


        profileId = getIntent().getStringExtra(USER_ID_EXTRA);
        username = getIntent().getStringExtra(USERNAME_EXTRA);
        requestQueue = Volley.newRequestQueue(this);
        searchBox.setFocusable(false);
        fetchPosts();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView usernamePView;
        CircleImageView imageView;
        Button msgBtn = itemView.findViewById(R.id.msgSend);


        public HeaderViewHolder(View itemView) {
            super(itemView);

            usernamePView = itemView.findViewById(R.id.otherUsernameView);
            addBtn = itemView.findViewById(R.id.addfBtn);
            imageView = itemView.findViewById(R.id.imageView2);

            msgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//
                    AlertFragment fragment = AlertFragment.createFragment("coming soon");
                    fragment.show(getFragmentManager(), null);
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (addBtn.getText().toString().contentEquals(getResources().getString(R.string.add))){
                        performAddFriend();
                    } else if(addBtn.getText().toString().contentEquals(getResources().getString(R.string.unfriend))) {
                        performUnFriend();
                    } else {
                        performUnFriend();
                    }

//                    AlertFragment fragment = AlertFragment.createFragment("coming soon");
//                    fragment.show(getFragmentManager(), null);
                    final String user_id = getIntent().getStringExtra("user_id");
                    User user = new User();
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" +user.getId() + "/" + user.getId()+ "/date", currentDate);
                    friendsMap.put("Friends/" + user.getId() + "/"  +user.getId() + "/date", currentDate);


                    friendsMap.put("Friend_req/" + user.getId() + "/" + user.getId(), null);
                    friendsMap.put("Friend_req/" + user.getId() + "/" + user.getId(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                addBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                //  addBtn.setText("Unfriend this Person");

                                //  mDeclineBtn.setVisibility(View.INVISIBLE);
                                //  mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(OtherProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });
                }
            });
        }

        public void bind(){
            usernamePView.setText(username);
            if (getIntent().getStringExtra(PP_IMAGE__EXTRA) != null && !getIntent().getStringExtra(PP_IMAGE__EXTRA).isEmpty()){
                Picasso.with(OtherProfileActivity.this).load(getIntent().getStringExtra(PP_IMAGE__EXTRA)).into(imageView);
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
            }
            performFriendStatus();
        }

        private void performFriendStatus(){
            StringRequest customRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/addfriend.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("friend", "onErrorResponse: " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    int friendState = jsonObject.getInt("state");
                                    if (friendState == 0){
                                        addBtn.setText(R.string.cancel_request);
                                    } else if (friendState == 1){
                                        addBtn.setText(R.string.unfriend);
                                    }
                                } else {
                                    addBtn.setText(R.string.add);
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

                    Log.i("isFriend", "getParams: " + userId + " " + guestUserId);
                    map.put("isFriend", String.valueOf(true));
                    map.put("from", userId);
                    map.put("to", guestUserId);
                    return map;
                }
            };

            Volley.newRequestQueue(OtherProfileActivity.this).add(customRequest);
        }

        private void performAddFriend(){
            StringRequest customRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/addfriend.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("add_friend", "onErrorResponse: " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    addBtn.setText(R.string.cancel_request);
                                } else {
                                    addBtn.setText(R.string.add);
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

                    map.put("add", String.valueOf(true));
                    map.put("from", userId);
                    map.put("to", guestUserId);
                    return map;
                }
            };

            Volley.newRequestQueue(OtherProfileActivity.this).add(customRequest);
        }

        private void performUnFriend(){
            StringRequest customRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/addfriend.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("unFriend", "onErrorResponse: " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    addBtn.setText(R.string.add);
                                } else if (jsonObject.getInt("state") == 0){
                                    addBtn.setText(R.string.cancel_request);
                                } else if (jsonObject.getInt("state") > 1){
                                    addBtn.setText(R.string.unfriend);
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

                    Log.i("unFriend", "getParams: " + userId + " " + guestUserId);
                    map.put("unFriend", String.valueOf(true));
                    map.put("from", userId);
                    map.put("to", guestUserId);
                    return map;
                }
            };

            Volley.newRequestQueue(OtherProfileActivity.this).add(customRequest);
        }

    }

    private class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTextView;
        Button likeButton;
        Button commentButton;
        TextView dateView;
        TextView usernameView;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
        }

        public void bind(final PostResponse post, final int pos) {
            String postText;
            if (post.getUsername() != null){
                usernameView.setText(post.getUsername());
            }
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(post.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = date.getTime();
            long millisecondsFromNow = milliseconds - now;
            dateView.setText(DateUtils.getRelativeDateTimeString(OtherProfileActivity.this, milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

            try {
                postText = URLEncoder.encode(post.getText(), "ISO-8859-1");
                postText = URLDecoder.decode(postText, "UTF-8");
                postTextView.setText(postText);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (post.getLikes() != null && post.getLikes().length > 0){
                //likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                likeButton.setText(post.getLikes().length + " Like");


                Log.e("loggedInUserId", userId);
                for (int id : post.getLikes()) {
                    Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                        break;
                    }
                }
            }

            if (post.getComments() != null && post.getComments().length > 0){
                //commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                commentButton.setText(post.getComments().length + " Comment");
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*ArrayList<CommentResponse> comments = new ArrayList<>(Arrays.asList(post.getComments()));
                    Intent intent = CommentActivity.createIntent( post.getLikes(), post.getPostId(), now,
                            Integer.parseInt(post.getUserId()), OtherProfileActivity.this);
                    adapterPos = pos;
                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);

                    //startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);*/
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < post.getLikes().length; i++) {
                        if (post.getLikes()[i] == Integer.parseInt(userId)){
                            reverseLike(post, pos);
                            return;
                        }
                    }

                    performLike(post, pos);
                }
            });
        }


        private void performLike(final PostResponse postResponse, final int pos){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                                    likeButton.setText((postResponse.getLikes().length + 1) + " Like");
                                    int[] likes = new int[postResponse.getLikes().length + 1];
                                    for (int i = 0; i < posts[pos].getLikes().length; i++) {
                                        likes[i] = posts[pos].getLikes()[i];
                                    }

                                    likes[posts[pos].getLikes().length] = Integer.parseInt(userId);
                                    posts[pos].setLikes(likes);
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

                    map.put("method", "post_like");
                    map.put("userId", userId);
                    map.put("post_id", String.valueOf(postResponse.getPostId()));
                    map.put("add_like", String.valueOf(true));

                    return map;
                }
            };

            requestQueue.add(stringRequest);
        }

        private void reverseLike(final PostResponse postResponse, final int pos){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){

                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);

                                    if (postResponse.getLikes().length > 1){
                                        likeButton.setText((postResponse.getLikes().length - 1) + " Like");
                                    } else {
                                        likeButton.setText("Like");
                                    }



                                    ArrayList<Integer> likesList = new ArrayList<>();

                                    for (int id : postResponse.getLikes()) {
                                        if (id != Integer.parseInt(userId)){
                                            likesList.add(id);
                                        }
                                    }

                                    int[] likes = new int[likesList.size()];

                                    for(int i = 0; i < likesList.size(); i++) {
                                        likes[i] = likesList.get(i);
                                    }

                                    posts[pos].setLikes(likes);
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

                    map.put("method", "post_like");
                    map.put("userId", userId);
                    map.put("post_id", String.valueOf(postResponse.getPostId()));
                    map.put("remove_like", String.valueOf(true));

                    return map;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    private class PostRecyclerAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_HEADER){
                View view = LayoutInflater.from(OtherProfileActivity.this).inflate(R.layout.other_header_layout, parent, false);
                return new OtherProfileActivity.HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(OtherProfileActivity.this).inflate(R.layout.post_card, parent, false);
                return new OtherProfileActivity.PostViewHolder(view);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof OtherProfileActivity.PostViewHolder) {
                OtherProfileActivity.PostViewHolder pHolder = (OtherProfileActivity.PostViewHolder) holder;
                pHolder.bind(posts[position-1], position-1);
            } else  if (holder instanceof OtherProfileActivity.HeaderViewHolder) {
                OtherProfileActivity.HeaderViewHolder pHolder = (OtherProfileActivity.HeaderViewHolder) holder;
                pHolder.bind();
            }
        }

        @Override
        public int getItemCount() {
            return posts.length + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)){
                return VIEW_HEADER;
            }

            return VIEW_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }

    private void fetchPosts(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php", ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if (response.getPosts() != null){
                            Log.i("volley response", "onResponse: " + response.getPosts()[0].getCreatedAt());
                            posts = response.getPosts();
                            nextCursor = response.getNextCursor();
                            now = response.getNow();
                            updatePostsUi();
                        } else{
                            posts = new PostResponse[0];
                            updatePostsUi();
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

                map.put("userId", guestUserId);
                map.put("method", "get_user_posts");
                map.put("cursor", "0");

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updatePostsUi(){
        if (postsAdapter == null){
            searchAdapter = null;
            searchRecyclerView.setVisibility(View.GONE);
            postsRecyclerView.setVisibility(View.VISIBLE);
            postsAdapter = new PostRecyclerAdapter();
            postsRecyclerView.setLayoutManager(new LinearLayoutManager(OtherProfileActivity.this));
            postsRecyclerView.setAdapter(postsAdapter);
        } else {

        }
    }

    private void updateSearchUi(){
        if (searchAdapter == null){
            postsAdapter = null;
            postsRecyclerView.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
            searchAdapter = new SearchRecyclerAdapter();
            searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            searchRecyclerView.setAdapter(searchAdapter);
        } else {
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMMENT_ACTIVITY_RESULT){
            if (data != null){
                CommentResponse commentResponse = (CommentResponse) data.getSerializableExtra("comment");
                int postId = data.getIntExtra("post_id", 0);
                for (int i = 0; i < posts.length; i++) {
                    if (posts[i].getPostId() == postId){
                        int c_size = posts[i].getComments().length;
                        CommentResponse[] c_resArray = new CommentResponse[c_size+1];
                        for (int j = 0; j <  posts[i].getComments().length; j++) {
                            c_resArray[j] = posts[i].getComments()[j];
                        }

                        c_resArray[c_size] = commentResponse;
                        posts[i].setComments(c_resArray);
                        postsAdapter.notifyDataSetChanged();
                    }
                }

            }
            //  Toast.makeText(OtherProfileActivity.this, "result", Toast.LENGTH_SHORT).show();
        } else if (requestCode == CREATE_POST_RESULT){
            if (data != null){
                PostResponse postResponse = (PostResponse) data.getSerializableExtra("post");
                PostResponse[] p_array = new PostResponse[posts.length + 1];

                p_array[0] = postResponse;

                for (int i = 0; i < posts.length; i++) {
                    p_array[i+1] = posts[i];
                }

                posts = p_array;
                postsAdapter.notifyItemInserted(0);
            }
        }
    }

    private class SearchHeaderViewHolder extends RecyclerView.ViewHolder{

        Button userSearchFilter;
        Button groupSearchFilter;

        public SearchHeaderViewHolder(View itemView) {
            super(itemView);

            userSearchFilter = itemView.findViewById(R.id.userSearchFilter);
            groupSearchFilter = itemView.findViewById(R.id.groupSearchFilter);

            userSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchUsers = true;
                        searchGroups = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("user")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchUi();
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            groupSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchGroups = true;
                        searchUsers = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("group")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchUi();
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView searchUsername;
        TextView detailsView;

        public SearchViewHolder(View itemView) {
            super(itemView);

            searchUsername = itemView.findViewById(R.id.searchUserName);
            detailsView = itemView.findViewById(R.id.detailsView);
        }

        public void bind(final SearchItem item){
            searchUsername.setText(item.getName());
            if (item.getDescription() != null && !item.getDescription().contentEquals("")){
                detailsView.setText(item.getDescription());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (item.getType().contentEquals("user")) {
                        if (userId.contentEquals(String.valueOf(item.getId()))){

                        } else {
                            Intent intent = OtherProfileActivity.createIntent(
                                    String.valueOf(item.getId()),
                                    item.getName(),
                                    null,
                                    OtherProfileActivity.this);

                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    private class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_HEADER){
                View view = LayoutInflater.from(OtherProfileActivity.this).inflate(R.layout.search_header, parent, false);
                return new SearchHeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(OtherProfileActivity.this).inflate(R.layout.search_card, parent, false);
                return new SearchViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof SearchViewHolder) {
                SearchViewHolder pHolder = (SearchViewHolder) holder;
                pHolder.bind(searchItems.get(position-1));
            }
        }

        @Override
        public int getItemCount() {
            return searchItems.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)){
                return VIEW_HEADER;
            }

            return VIEW_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    }

    private void performSearch(final String query){
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_search.php", SearchResponse.class,
                new Response.Listener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse response) {
                        searchItems = new ArrayList<>();
                        if (searchUsers){
                            if (response.getUsers() != null && response.getUsers().length > 0){
                                Log.i("volley response", "onResponse: " + response.getUsers()[0].getUsername());

                                for (int i = 0; i < response.getUsers().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getUsers()[i].getUserId());
                                    item.setName(response.getUsers()[i].getUsername());
                                    item.setType("user");
                                    searchItems.add(item);
                                }
                            }
                        } else if (searchGroups){
                            if (response.getGroups() != null && response.getGroups().length > 0){
                                for (int i = 0; i < response.getGroups().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getGroups()[i].getGroupId());
                                    item.setName(response.getGroups()[i].getGroup_name());
                                    if (response.getGroups()[i].getGroup_description() != null && !response.getGroups()[i].getGroup_description().contentEquals("")){
                                        item.setDescription(response.getGroups()[i].getGroup_description());
                                    }
                                    item.setType("group");
                                    searchItems.add(item);
                                }
                            }
                        } else {
                            if (response.getUsers() != null && response.getUsers().length > 0){
                                Log.i("volley response", "onResponse: " + response.getUsers()[0].getUsername());

                                for (int i = 0; i < response.getUsers().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getUsers()[i].getUserId());
                                    item.setName(response.getUsers()[i].getUsername());
                                    item.setType("user");
                                    searchItems.add(item);
                                }
                            }

                            if (response.getGroups() != null && response.getGroups().length > 0){
                                for (int i = 0; i < response.getGroups().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getGroups()[i].getGroupId());
                                    item.setName(response.getGroups()[i].getGroup_name());
                                    if (response.getGroups()[i].getGroup_description() != null && !response.getGroups()[i].getGroup_description().contentEquals("")){
                                        item.setDescription(response.getGroups()[i].getGroup_description());
                                    }
                                    item.setType("group");
                                    searchItems.add(item);
                                }
                            }
                        }


                        if (searchItems.size() > 0){
                            updateSearchUi();
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

                map.put("query", query);
                //map.put("cursor", "0");

                return map;
            }
        };

        Volley.newRequestQueue(this).add(customRequest);
    }

    @Override
    public void onBackPressed() {
        if (searchAdapter != null){
            ViewGroup.LayoutParams lp = searchBox.getLayoutParams();
            lp.width = searchBoxWidth;
            searchBox.setLayoutParams(lp);
            searchBox.setText("");
            searchBox.setFocusable(false);
            updatePostsUi();
        } else {
            super.onBackPressed();
        }
    }
}
