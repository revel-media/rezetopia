package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.rezetopia.krito.rezetopiakrito.fragments.AlertFragment;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.EventResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.GroupPostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeed;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeedItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.VendorPostsResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.CommentResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class OtherProfileActivity extends AppCompatActivity {

    private static String USER_ID_EXTRA = "profile_other_user_id_extra";
    private static String USERNAME_EXTRA = "profile_other_username_extra";
    private static String PP_IMAGE__EXTRA = "profile_other_username_extra";
    private static int COMMENT_ACTIVITY_RESULT = 1001;
    private static int CREATE_POST_RESULT = 1002;
    private static int VIEW_HEADER = 1;
    private static int VIEW_ITEM = 2;
    public static String nf;

    boolean searchUsers = false;
    boolean searchGroups = false;

    ArrayList<SearchItem> searchItems;
    LinearLayout rezeAccountLayout;

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
    TextView usernamePView;
    CircleImageView imageView;
    ImageView cover;
    TextView location;
    TextView position;

    public static Intent createIntent(String userId, String username, String ImageUrl, Context context){
        nf = username;
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

       Firebase.setAndroidContext(this);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        User user = new User();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(user.getId()));
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();



        postsRecyclerView = findViewById(R.id.otherProfileRecView);
        searchRecyclerView = findViewById(R.id.otherSearchRecView);
        rezeAccountLayout = findViewById(R.id.rezeAccountLayout);

        searchBox = findViewById(R.id.searchView);
        guestUserId = getIntent().getStringExtra(USER_ID_EXTRA);
        backView = findViewById(R.id.searchBackArrow);

        userId = OtherProfileActivity.this.getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");


//        searchBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s != null && s.length() > 0 && !s.toString().contentEquals("0")){
//                    q = s.toString();
//                    performSearch(q);
//                } else {
//                    searchGroups = false;
//                    searchUsers = false;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

//        searchBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ResizeWidthAnimation anim = new ResizeWidthAnimation(searchBox, 800);
//                anim.setDuration(20);
//                searchBox.startAnimation(anim);
//                searchBox.setFocusable(true);
//                if (searchItems == null){
//                    searchItems = new ArrayList<>();
//                }
//                updateSearchUi();
//            }
//        });

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
        username = nf;
       // Toast.makeText(getBaseContext(),nf,Toast.LENGTH_LONG).show();
        requestQueue = Volley.newRequestQueue(this);
        searchBox.setFocusable(false);
        fetchPosts();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        Button msgBtn = itemView.findViewById(R.id.msgSend);


        public HeaderViewHolder(View itemView) {
            super(itemView);

            usernamePView = itemView.findViewById(R.id.otherUsernameView);
            addBtn = itemView.findViewById(R.id.addfBtn);
            imageView = itemView.findViewById(R.id.imageView2);
            cover = itemView.findViewById(R.id.imageView);
            location = itemView.findViewById(R.id.loca);
            position = itemView.findViewById(R.id.pos);
            msgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OtherProfileActivity.this, Chat.class);
                    intent.putExtra("guestUserId",guestUserId);
                    startActivity(intent);
                }
            });

//            if (Integer.parseInt(userId) == 1){
//                addBtn.setVisibility(View.GONE);
//                cover.setBackground(getResources().getDrawable(R.drawable.cover_1));
//                imageView.setBackground(getResources().getDrawable(R.drawable.rezetopia));
//            }

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (addBtn.getText().toString().contentEquals(getResources().getString(R.string.add))){
                        new performAddFriend().execute();
                        //addBtn.setText(getResources().getString(R.string.pendding));
                       // performAddFriend();
                    } else if(addBtn.getText().toString().contentEquals(getResources().getString(R.string.unfriend))) {
                        new performUnFriend().execute();
                        performUnFriend();
                    } else {
                        performUnFriend();
                    }

                }
            });
        }

        public void bind(){
            usernamePView.setText(username);
//            if (getIntent().getStringExtra(PP_IMAGE__EXTRA) != null && !getIntent().getStringExtra(PP_IMAGE__EXTRA).isEmpty()){
//                Picasso.with(OtherProfileActivity.this).load(getIntent().getStringExtra(PP_IMAGE__EXTRA)).into(imageView);
//            } else {
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
//            }
            new getUser().execute(profileId);
            performFriendStatus();
        }

        private void performFriendStatus(){
            String url2 = "https://rezetopiachat.firebaseio.com/requests.json";
            StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.has("friends_pending_"+guestUserId)){
                            JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+guestUserId));
                            if (obj2.has(userId)){
                                addBtn.setText(getResources().getString(R.string.unfriend));
                            }else{
                                addBtn.setText(getResources().getString(R.string.add));
                            }
                        }else if (obj.has("friends_pending_"+userId)){
                            JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                            if (obj2.has(guestUserId)){
                                addBtn.setText(getResources().getString(R.string.unfriend));
                            }else{
                                addBtn.setText(getResources().getString(R.string.add));
                            }
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

            RequestQueue rQueue1 = Volley.newRequestQueue(OtherProfileActivity.this);
            rQueue1.add(request2);
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
                postTextView.setText(post.getText());
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
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
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
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
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php", ApiResponse.class,
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
        TextView location;
        TextView position;
        CircleImageView personalImg;

        public SearchViewHolder(View itemView) {
            super(itemView);

            searchUsername = itemView.findViewById(R.id.searchUserName);
            detailsView = itemView.findViewById(R.id.detailsView);
            location = itemView.findViewById(R.id.loca);
            position = itemView.findViewById(R.id.pos);
            personalImg = itemView.findViewById(R.id.imageView2);
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
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_search.php", SearchResponse.class,
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
    private class performAddFriend extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            performAddFriend();
            return null;
        }
    }
    private class performUnFriend extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            performUnFriend();
            return null;
        }
    }
    private class getUser extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {
            getUser(strings[0]);
            return null;
        }
    }
    public void performAddFriend(){
        String url2 = "https://rezetopiachat.firebaseio.com/requests/friends_pending_"+guestUserId+".json";
        StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Log.d("check_rse",s.toString());
                Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests/friends_pending_"+guestUserId);
                addBtn.setText(R.string.cancel_request);
                if(s.equals("null")) {
                    reference.child("count").setValue(1);
                    reference.child(userId).child("id").setValue(userId);
                }
                else {

                    try {
                        JSONObject obj = new JSONObject(s);
                        Log.d("check_rse",obj.getString("count"));
                        if (obj.has("count")) {
                            int val = Integer.parseInt(obj.getString("count"));
                            val = val+=1;
                            reference.child("count").setValue(val);
                            reference.child(userId).child("id").setValue(userId);
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

        RequestQueue rQueue1 = Volley.newRequestQueue(OtherProfileActivity.this);
        rQueue1.add(request2);

        StringRequest customRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/addfriend.php",
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
    public void performUnFriend(){
        String url2 = "https://rezetopiachat.firebaseio.com/requests.json";
        StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.has("friends_pending_"+guestUserId)){
                        JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+guestUserId));
                        Log.d("check_remove",obj2.getString("count"));
                        reference.child("friends_pending_"+guestUserId).child(userId).removeValue();
                        addBtn.setText(R.string.add);
                        int val = Integer.parseInt(obj2.getString("count"));
                        if (val != 0){
                            val = val-=1;
                            reference.child("friends_pending_"+guestUserId).child("count").setValue(val);
                        }
                        StringRequest customRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/addfriend.php",
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
                    }else if (obj.has("friends_pending_"+userId)){
                        JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                        Log.d("check_remove",obj2.getString("count"));
                        reference.child("friends_pending_"+userId).child(guestUserId).removeValue();
                        addBtn.setText(R.string.add);
                        int val = Integer.parseInt(obj2.getString("count"));
                        if (val != 0){
                            val = val-=1;
                            reference.child("friends_pending_"+userId).child("count").setValue(val);
                        }
                        StringRequest customRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/addfriend.php",
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

                                Log.i("unFriend", "getParams: " + guestUserId + " " + userId);
                                map.put("unFriend", String.valueOf(true));
                                map.put("from", guestUserId);
                                map.put("to", userId);
                                return map;
                            }
                        };

                        Volley.newRequestQueue(OtherProfileActivity.this).add(customRequest);
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

        RequestQueue rQueue1 = Volley.newRequestQueue(OtherProfileActivity.this);
        rQueue1.add(request2);
    }
    private void getUser(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/getInfo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                try {
                    final JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    //Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("msg").equals("succ")) {

                        location.setText(jsonObject.getString("city"));
                        position.setText(jsonObject.getString("position"));
                       // playerNameTv.setText(jsonObject.getString("name"));
//                        playerMatchesTv.setText("0");
//                        playerLevelsTv.setText("0");
//                        playerPointsTv.setText("0");
                        if (profileId.equals("1")){
                            imageView.setImageResource(R.drawable.rezetopia);
                        }else{
                            Picasso.with(getApplicationContext())
                                    .load("http://rezetopia.dev-krito.com/images/profileImgs/" + jsonObject.getString("img") + ".JPG")
                                    .placeholder(R.drawable.circle).into(imageView);
                        }

//                            Picasso.with(getApplicationContext())
//                                    .load("http://rezetopia.dev-krito.com/images/coverImgs/" + jsonObject.getString("cover") + ".JPG")
//                                    .placeholder(R.drawable.cover).into(cover);
                        Picasso.with(getApplicationContext()).load("http://rezetopia.dev-krito.com/images/coverImgs/" + jsonObject.getString("cover") + ".JPG").into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                cover.setBackground(new BitmapDrawable(bitmap));
                            }
                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });





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
        Volley.newRequestQueue(OtherProfileActivity.this).add(request);;

    }
}
