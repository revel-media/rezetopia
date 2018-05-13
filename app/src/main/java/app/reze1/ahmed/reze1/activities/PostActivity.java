package app.reze1.ahmed.reze1.activities;

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
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

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

        likeButton.setOnClickListener(this);
        commentButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        fetchPost();
    }

    private void fetchPost(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
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

    }
}
