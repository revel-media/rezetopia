package app.reze1.ahmed.reze1.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.activities.CreatePostActivity;
import app.reze1.ahmed.reze1.activities.OtherProfileActivity;
import app.reze1.ahmed.reze1.model.pojo.post.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.post.CommentResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ahmed on 4/23/2018.
 */

public class PostsProfile extends Fragment {
    String childname;
    TextView textViewChildName;
    EditText editText;
    private static final int COMMENT_ACTIVITY_RESULT = 1001;
    private static final int CREATE_POST_RESULT = 1002;
    private static final int VIEW_HEADER = 1;
    private static final int VIEW_ITEM = 2;
    private WebView webview;
    String distfile = "";
    private PostResponse[] posts;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private EditText commentEditText;
    int nextCursor = 0;
    int adapterPos;
    RequestQueue requestQueue;
    long now;
    String userId;
    private OnCallback mListener;
    ProgressBar postsProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_profile, container, false);
        recyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        commentEditText = view.findViewById(R.id.commentEditText);

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        postsProgress = view.findViewById(R.id.postsProgress);

        postsProgress.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        requestQueue = Volley.newRequestQueue(getActivity());
        fetchPosts();
        return view;
    }
    public interface OnCallback {
        void onProfile();
    }
    private class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTextView;
        Button likeButton;
        Button commentButton;
        TextView dateView;
        TextView usernameView;
        ImageView ppView;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
            ppView = itemView.findViewById(R.id.ppView);
        }

        public void bind(final PostResponse post, final int pos) {
            String postText = null;
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
            dateView.setText(DateUtils.getRelativeDateTimeString(getActivity(), milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

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
                    Intent intent = CommentActivity.createIntent(comments, post.getLikes(), post.getPostId(), now, Integer.parseInt(post.getUserId()),
                            getActivity());

                    adapterPos = pos;
                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);

                    //startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);*/
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

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getUserId().contentEquals(userId)){
                        mListener.onProfile();
                    } else {
                        startOtherProfile(pos);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.getUserId().contentEquals(userId)){
                        mListener.onProfile();
                    } else {
                        startOtherProfile(pos);
                    }
                }
            });

        }

        private void startOtherProfile(int position){
            Intent intent = OtherProfileActivity.createIntent(posts[position].getUserId(), posts[position].getUsername(), getActivity());
            startActivity(intent);
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
                    map.put("owner_id", postResponse.getUserId());
                    map.put("post_id", String.valueOf(postResponse.getPostId()));
                    map.put("add_like", String.valueOf(true));

                    return map;
                }
            };

            requestQueue.add(stringRequest);
        }

        private void reverseLike(final PostResponse postResponse, final int pos){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.18:80/reze/user_post.php",
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
                    map.put("owner_id", postResponse.getUserId());
                    map.put("post_id", String.valueOf(postResponse.getPostId()));
                    map.put("remove_like", String.valueOf(true));

                    return map;
                }
            };

            requestQueue.add(stringRequest);
        }
    }
    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout createPostLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            createPostLayout = itemView.findViewById(R.id.createPostLayout);

            createPostLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    startActivityForResult(intent, CREATE_POST_RESULT);
                    //startActivity(intent);
                }
            });
        }
    }

    private class PostRecyclerAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_HEADER){
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.create_post_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_card, parent, false);
                return new PostViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PostViewHolder) {
                PostViewHolder pHolder = (PostViewHolder) holder;
                pHolder.bind(posts[position-1], position-1);
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
                            postsProgress.setVisibility(View.GONE);
                            Log.i("volley response", "onResponse: " + response.getPosts()[0].getCreatedAt());
                            posts = response.getPosts();
                            nextCursor = response.getNextCursor();
                            now = response.getNow();
                            updateUi();
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

                map.put("method", "get_user_posts");
                map.put("userId", userId);
                map.put("cursor", "0");

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new PostRecyclerAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        } else {

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
                        adapter.notifyDataSetChanged();
                    }
                }

            }
          //  Toast.makeText(getActivity(), "result", Toast.LENGTH_SHORT).show();
        } else if (requestCode == CREATE_POST_RESULT){
            if (data != null){
                PostResponse postResponse = (PostResponse) data.getSerializableExtra("post");
                PostResponse[] p_array = new PostResponse[posts.length + 1];

                p_array[0] = postResponse;

                for (int i = 0; i < posts.length; i++) {
                    p_array[i+1] = posts[i];
                }

                posts = p_array;
                adapter.notifyItemInserted(0);
            }
        }
    }
}
