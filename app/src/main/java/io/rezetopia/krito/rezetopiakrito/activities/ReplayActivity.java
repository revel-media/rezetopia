package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiReplayResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.CommentReplyResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReplayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String REPLIES_EXTRA = "replay_activity.replies_extra";
    private static final String POST_ID_EXTRA = "replay_activity.post_id_extra";
    private static final String COMMENT_ID_EXTRA = "replay_activity.comment_id_extra";
    private static final String TIME_NOW_EXTRA = "replay_activity.time_now_extra";
    private static final String LIKES_EXTRA = "replay_activity.likes_extra";

    ArrayList<CommentReplyResponse> replies;
    ImageView backView;
    long now;
    ImageView sendReplayView;
    TextView commentLikesView;
    CustomEditText replayEditText;
    int postId;
    int commentId;
    String userId;
    CommentReplyResponse replayResponse;
    int[] likes;
    RecyclerView repliesRecyclerView;
    RecyclerView.Adapter adapter;
    ProgressBar replayProgressView;
    int replaySize;


    public static Intent createIntent(int[] likeItems, int postId, int comment_id, long now, Context context){
        Intent intent = new Intent(context, ReplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(POST_ID_EXTRA, postId);
        bundle.putLong(TIME_NOW_EXTRA, now);
        bundle.putIntArray(LIKES_EXTRA, likeItems);
        bundle.putInt(COMMENT_ID_EXTRA, comment_id);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        backView = findViewById(R.id.replayBackView);
        backView.setOnClickListener(this);

        commentLikesView = findViewById(R.id.likesCommentView);
        replayProgressView = findViewById(R.id.replayProgressView);
        replayProgressView.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        replayProgressView.setVisibility(View.VISIBLE);



        replies = new ArrayList<>();
        likes = getIntent().getExtras().getIntArray(LIKES_EXTRA);
        commentId = getIntent().getExtras().getInt(COMMENT_ID_EXTRA);

        if (likes != null && likes.length > 0){
            commentLikesView.setVisibility(View.VISIBLE);
            commentLikesView.setText(likes.length + "");
        } else {
            commentLikesView.setVisibility(View.GONE);
        }


        postId = getIntent().getExtras().getInt(POST_ID_EXTRA);
        now = getIntent().getExtras().getLong(TIME_NOW_EXTRA);
        repliesRecyclerView = findViewById(R.id.replayRecView);

        sendReplayView = findViewById(R.id.sendReplayView);
        replayEditText = findViewById(R.id.replayEditText);

        sendReplayView.setOnClickListener(this);

        fetchReplies();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.replayBackView:
                if (replayResponse != null){
                    Intent intent = new Intent();
                    //intent.putExtra("replay", replayResponse);
                    intent.putExtra("replay_size", replies.size());
                    intent.putExtra("comment_id", commentId);
                    setResult(RESULT_OK, intent);
                }
                onBackPressed();
                break;
            case R.id.sendReplayView:
                if (!replayEditText.getText().toString().contentEquals("")) {
                    CommentReplyResponse response = new CommentReplyResponse();
                    response.setReplayText(replayEditText.getText().toString());
                    response.setPending(true);
                    replies.add(response);
                    updateUi();
                    adapter.notifyItemInserted(replies.size()-1);
                    repliesRecyclerView.scrollToPosition(replies.size()-1);
                    performReplay();

                }
                break;
        }
    }

    private class ReplayViewHolder extends RecyclerView.ViewHolder{

        TextView replayTextView;
        TextView createdAtView;
        TextView replierView;
        TextView postingView;
        TextView likeReplayView;

        public ReplayViewHolder(View itemView) {
            super(itemView);

            replayTextView = itemView.findViewById(R.id.commentTextView);
            createdAtView = itemView.findViewById(R.id.commentCreatedAtView);
            replierView = itemView.findViewById(R.id.commenterView);
            postingView = itemView.findViewById(R.id.postingView);
            likeReplayView = itemView.findViewById(R.id.replayLikeView);
        }

        public void bind(final CommentReplyResponse replay, boolean pending, final int position){
            if (pending){
                postingView.setVisibility(View.VISIBLE);
            } else {
                replayTextView.setText(replay.getReplayText());
                createdAtView.setText(replay.getCreatedAt());
                replierView.setText(replay.getUsername());

                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(replay.getCreatedAt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long milliseconds = date.getTime();
                long millisecondsFromNow = milliseconds - now;
                createdAtView.setText(DateUtils.getRelativeTimeSpanString(milliseconds, now, milliseconds - now));

//                if (replay.getLikes() != null && replay.getLikes().length > 0) {
//                    String like = getResources().getString(R.string.like);
//                    replayTextView.setText(replay.getLikes().length + " " + like);
//                }

                if (replay.getLikes() != null && replay.getLikes().length > 0) {
                    String like = getResources().getString(R.string.like);
                    likeReplayView.setText(replay.getLikes().length + " " + like);
                    for (int i = 0; i < replay.getLikes().length; i++) {
                        if (replay.getLikes()[i] == Integer.parseInt(userId)) {

                            if (replay.getLikes().length > 0) {
                                likeReplayView.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }
                    }
                }


                likeReplayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String likeString = getResources().getString(R.string.like);

                        if (replay.getLikes() != null && replay.getLikes().length > 0) {
                            for (int i = 0; i < replay.getLikes().length; i++) {
                                if (replay.getLikes()[i] == Integer.parseInt(userId)) {

                                    likeReplayView.setText((replay.getLikes().length - 1) + " " + likeString);
                                    if (!(replay.getLikes().length > 1)) {
                                        likeReplayView.setText(likeString);
                                    }

                                    reverseLike(replay);
                                    return;
                                }
                            }
                        }

                        if (replay.getLikes() != null) {
                            if (replay.getLikes().length > 0) {
                                likeReplayView.setText((replay.getLikes().length + 1) + " " + likeString);
                            } else  {
                                likeReplayView.setText((replay.getLikes().length + 1) + " " + likeString);
                            }
                        }
                        performLike(replay);
                    }
                });
            }
        }

        private void performLike(final CommentReplyResponse replay){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                Log.i("comment_like", "onResponse: " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    int[] likes = new int[replay.getLikes().length + 1];
                                    for (int i = 0; i < replay.getLikes().length; i++) {
                                        likes[i] = replay.getLikes()[i];
                                    }

                                    likes[likes.length - 1] = Integer.parseInt(userId);
                                    replay.setLikes(likes);
                                    //adapter.notifyItemChanged(position);
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

                    //Log.i("add_like_parameters", "getParams: " + userId + " " + postId + " " + replay.getCommentId());
                    map.put("method", "replay_like");
                    map.put("userId", userId);
                    map.put("post_id", String.valueOf(String.valueOf(replay.getReplayId())));
                    map.put("replay_id", String.valueOf(replay.getReplayId()));
                    map.put("add_like", String.valueOf(true));

                    return map;
                }
            };

            Volley.newRequestQueue(ReplayActivity.this).add(stringRequest);
        }

        private void reverseLike(final CommentReplyResponse replay){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                Log.i("comment_dislike", "onResponse: " + response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){

                                    ArrayList<Integer> likesList = new ArrayList<>();

                                    for (int id : replay.getLikes()) {
                                        if (id != Integer.parseInt(userId)){
                                            likesList.add(id);
                                        }
                                    }

                                    int[] likes = new int[likesList.size()];

                                    for(int i = 0; i < likesList.size(); i++) {
                                        likes[i] = likesList.get(i);
                                    }

                                    replay.setLikes(likes);
                                    //adapter.notifyItemChanged(position);
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

                    //Log.i("reverse_like_parameters", "getParams: " + userId + " " + postId + " " + replay.getCommentId());
                    map.put("method", "replay_like");
                    map.put("userId", userId);
                    map.put("post_id", String.valueOf(postId));
                    map.put("comment_id", String.valueOf(replay.getReplayId()));
                    map.put("remove_like", String.valueOf(true));

                    return map;
                }
            };

            Volley.newRequestQueue(ReplayActivity.this).add(stringRequest);
        }
    }

    private class ReplayRecyclerAdapter extends RecyclerView.Adapter<ReplayViewHolder>{

        @NonNull
        @Override
        public ReplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ReplayActivity.this).inflate(R.layout.replay_card, parent, false);
            return new ReplayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReplayViewHolder holder, int position) {
            holder.bind(replies.get(position), replies.get(position).isPending(), position);
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }
    }

    private void performReplay(){
        if (replayEditText.getText().toString().length() > 0){
            final String replayText = replayEditText.getText().toString();
            replayEditText.setText(null);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("error")){
                                  //  Toast.makeText(ReplayActivity.this, "Error submitting replay", Toast.LENGTH_SHORT).show();
                                } else {
                                    replayResponse = new CommentReplyResponse();
                                    replayResponse.setReplayId(Integer.parseInt(userId));
                                    replayResponse.setReplierId(jsonObject.getInt("replierId"));
                                    replayResponse.setReplayText(jsonObject.getString("replay_text"));
                                    replayResponse.setCreatedAt(jsonObject.getString("createdAt"));
                                    replayResponse.setUsername(jsonObject.getString("username"));
                                    replayResponse.setPending(false);
                                    /*JSONArray jsonArray = jsonObject.getJSONArray("likes");
                                    if (jsonArray != null && jsonArray.length() > 0){
                                        int[] likes = new int[jsonArray.length()];
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            likes[i] = (int) jsonArray.get(0);
                                        }
                                    }*/
                                    replayResponse.setLikes(null);

                                    if (replies == null)
                                        replies = new ArrayList<>();

                                    replies.set(replies.size()-1, replayResponse);
                                    //replies.add(replayResponse);
                                    adapter.notifyItemChanged(replies.size()-1);
                                    repliesRecyclerView.scrollToPosition(replies.size()-1);
                                    replaySize ++;
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

                    map.put("method", "add_replay");
                    map.put("post_id", String.valueOf(postId));
                    map.put("comment_id", String.valueOf(commentId));
                    map.put("replay", replayText);
                    map.put("userId", userId);

                    return map;
                }
            };

            Volley.newRequestQueue(ReplayActivity.this).add(stringRequest);
        } else {
           // Toast.makeText(this, "Empty replay!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchReplies(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                ApiReplayResponse.class,
                new Response.Listener<ApiReplayResponse>() {
                    @Override
                    public void onResponse(ApiReplayResponse response) {
                        replayProgressView.setVisibility(View.GONE);
                        if (!response.isError()){
                            if (response.getReplies() != null) {
                                replies = new ArrayList<>(Arrays.asList(response.getReplies()));
                                for (CommentReplyResponse replyResponse:replies) {
                                    replyResponse.setPending(false);
                                }
                                updateUi();
                            }
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
                map.put("method", "get_replies");
                map.put("comment_id", String.valueOf(commentId));
                map.put("cursor", "0");

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new ReplayRecyclerAdapter();
            repliesRecyclerView.setAdapter(adapter);
            repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
