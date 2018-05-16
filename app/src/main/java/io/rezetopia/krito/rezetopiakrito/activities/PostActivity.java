package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.fragments.AlertFragment;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String POST_ID_EXTRA = "post_id_extra";
    int postId;
    PostResponse postResponse;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    TextView postTextView;
    TextView postUserName;
    TextView postDateView;
    Button likeButton;
    Button commentButton;
    Button shareButton;
    RelativeLayout postLayout;
    String userId;

    public static Intent createIntent(int postId, Context context){
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra(POST_ID_EXTRA, postId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postId = getIntent().getIntExtra(POST_ID_EXTRA, 0);
        requestQueue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.postProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        postTextView = findViewById(R.id.postTextView);
        likeButton = findViewById(R.id.postLikeButton);
        commentButton = findViewById(R.id.postCommentButton);
        shareButton = findViewById(R.id.sharePostButton);
        postLayout = findViewById(R.id.postLayout);
        postUserName = findViewById(R.id.postUserName);
        postDateView = findViewById(R.id.postDateView);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        likeButton.setOnClickListener(this);
        commentButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        fetchPost();
    }

    private void fetchPost(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_post.php",
                PostResponse.class,
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        Log.i("post", "onResponse: " + response.getText());
                        postResponse = response;
                        progressBar.setVisibility(View.GONE);
                        postLayout.setVisibility(View.VISIBLE);
                        String like = getResources().getString(R.string.like);
                        String comment = getResources().getString(R.string.comment);
                        String share = getResources().getString(R.string.share);
                        postTextView.setText(response.getText());
                        postUserName.setText(response.getUsername());
                        postDateView.setText(response.getCreatedAt());

                        if (response.getLikes() != null && response.getLikes().length > 0){
                            likeButton.setText(response.getLikes().length + " " + like);
                        }

                        if (response.getComments() != null && response.getComments().length > 0){
                            commentButton.setText(response.getComments().length + " " + comment);
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


                map.put("method", "get_post");
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.postLikeButton:
                String likeString = getResources().getString(R.string.like);
                if (postResponse.getLikes() != null) {
                    for (int i = 0; i < postResponse.getLikes().length; i++) {
                        if (postResponse.getLikes()[i] == Integer.parseInt(userId)) {

                            if (postResponse.getLikes().length > 1) {
                                likeButton.setText((postResponse.getLikes().length - 1) + " " + likeString);
                            } else {
                                likeButton.setText(likeString);
                            }
                            likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                            reverseLike();
                            return;
                        }
                    }
                }


                likeButton.setText((postResponse.getLikes().length + 1) + " " + likeString);
                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                performLike();
                break;
            case R.id.postCommentButton:
                Intent intent = CommentActivity.createIntent(postResponse.getLikes(), postResponse.getPostId(), System.currentTimeMillis()
                        , Integer.parseInt(postResponse.getUserId()),
                        PostActivity.this);

                //adapterPos = pos;
                startActivityForResult(intent, 10006);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                break;
            case R.id.sharePostButton:
                AlertFragment alertFragment = AlertFragment.createFragment("قريبا فى النسخة القادمة");
                alertFragment.show(getFragmentManager(), null);
                break;
        }
    }

    private void performLike(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("volley response", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                int[] likes = new int[postResponse.getLikes().length + 1];
                                for (int i = 0; i < postResponse.getLikes().length; i++) {
                                    likes[i] = postResponse.getLikes()[i];
                                }

                                likes[likes.length - 1] = Integer.parseInt(userId);
                                postResponse.setLikes(likes);
                                //adapter.notifyItemChanged(pos);
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
                map.put("owner_id", String.valueOf(postResponse.getUserId()));
                map.put("post_id", String.valueOf(postResponse.getPostId()));
                map.put("add_like", String.valueOf(true));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void reverseLike(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("volley response", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){

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

                                postResponse.setLikes(likes);
                                //adapter.notifyItemChanged(pos);
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
                map.put("owner_id", String.valueOf(postResponse.getUserId()));
                map.put("post_id", String.valueOf(postResponse.getPostId()));
                map.put("remove_like", String.valueOf(true));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}
