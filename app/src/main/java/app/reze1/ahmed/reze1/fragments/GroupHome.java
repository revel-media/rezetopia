package app.reze1.ahmed.reze1.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import app.reze1.ahmed.reze1.activities.CommentActivity;
import app.reze1.ahmed.reze1.activities.CreateGroupPostActivity;
import app.reze1.ahmed.reze1.activities.OtherProfileActivity;
import app.reze1.ahmed.reze1.model.pojo.post.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.post.CommentResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.ListPopupWindowAdapter;
import app.reze1.ahmed.reze1.helper.MenuCustomItem;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

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

/**
 * Created by Mona Abdallh on 4/30/2018.
 */

public class GroupHome extends Fragment {

    private static final String GROUP_ID_EXTRA = "group_id_extra";
    private static final String GROUP_NAME_EXTRA = "group_name_extra";
    private static final int COMMENT_ACTIVITY_RESULT = 1001;
    private static final int CREATE_POST_RESULT = 1002;
    private static final int VIEW_HEADER = 1;
    private static final int VIEW_POST = 2;

    private PostResponse[] posts;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    int nextCursor = 0;
    int adapterPos;
    RequestQueue requestQueue;
    long now;
    String userId;
    String groupId;
    String groupName;


    public static GroupHome createFragment(String groupId, String groupName){
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_ID_EXTRA, groupId);
        bundle.putString(GROUP_NAME_EXTRA, groupName);
        GroupHome home = new GroupHome();
        home.setArguments(bundle);
        return home;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
        groupId = getArguments().getString(GROUP_ID_EXTRA);
        groupName = getArguments().getString(GROUP_NAME_EXTRA);

        recyclerView = view.findViewById(R.id.homePostsRecyclerView);

        requestQueue = Volley.newRequestQueue(getActivity());
        fetchPosts();
        return view;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout createPostLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            createPostLayout = itemView.findViewById(R.id.createPostLayout);

            createPostLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreateGroupPostActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("group_name", groupName);
                    startActivityForResult(intent, CREATE_POST_RESULT);
                }
            });
        }
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
            dateView.setText(DateUtils.getRelativeDateTimeString(getActivity(), milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

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


                Log.e("loggedInUserId", userId);
                for (int id : item.getLikes()) {
                    Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                        break;
                    }
                }
            }

            if (item.getComments() != null && item.getComments().length > 0){
                //commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                commentButton.setText(item.getComments().length + " Comment");
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = CommentActivity.createIntent(item.getLikes(), item.getPostId(), now, Integer.parseInt(item.getUserId()),
                            getActivity());

                    adapterPos = pos;
                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);

                    //startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

                    if (item.getComments() != null) {
                        ArrayList<CommentResponse> comments = new ArrayList<>(Arrays.asList(item.getComments()));

                    }
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (item.getLikes() != null) {
                        for (int i = 0; i < item.getLikes().length; i++) {
                            if (item.getLikes()[i] == Integer.parseInt(userId)) {
                                reverseLike(item, pos);
                                return;
                            }
                        }
                    }

                    performLike(item, pos);
                }
            });

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getUserId().contentEquals( userId)){
                        //mListener.onProfile();
                    } else {
                        startOtherProfile(pos);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getUserId().contentEquals(userId)){
                        //mListener.onProfile();
                    } else {
                        startOtherProfile(pos);
                    }
                }
            });

            postSideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(item.getUserId()).contentEquals(String.valueOf(userId))) {
                        showPostPopupWindow(postSideMenu, true, item.getPostId(), Integer.parseInt(item.getUserId()));
                    } else {
                        showPostPopupWindow(postSideMenu, false, item.getPostId(), Integer.parseInt(item.getUserId()));
                    }
                }
            });
        }

        private void startOtherProfile(int position){
            Intent intent = OtherProfileActivity.createIntent(
                    posts[position].getUserId(),
                    posts[position].getUsername(),
                    null,
                    getActivity());
            startActivity(intent);
        }

        private void performLike(final PostResponse item, final int pos){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                                    likeButton.setText((item.getLikes().length + 1) + " Like");
                                    int[] likes = new int[item.getLikes().length + 1];
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
                    map.put("owner_id", String.valueOf(item.getUserId()));
                    map.put("post_id", String.valueOf(item.getPostId()));
                    map.put("add_like", String.valueOf(true));

                    return map;
                }
            };

            requestQueue.add(stringRequest);
        }

        private void reverseLike(final PostResponse item, final int pos){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.18:80/reze/user_post.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("volley response", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){

                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);

                                    if (item.getLikes().length > 1){
                                        likeButton.setText((item.getLikes().length - 1) + " Like");
                                    } else {
                                        likeButton.setText("Like");
                                    }



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
                    map.put("owner_id", String.valueOf(item.getUserId()));
                    map.put("post_id", String.valueOf(item.getPostId()));
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.create_post_header, parent, false);
                return new HeaderViewHolder(view);
            }

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_card, parent, false);
            return new PostViewHolder(view);
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

            return VIEW_POST;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new PostRecyclerAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchPosts(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_group.php",
                ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if (response.getPosts() != null){
                            Log.i("posts", "onResponse: " + response.getPosts()[0].getCreatedAt());
                            posts = response.getPosts();

                            nextCursor = response.getNextCursor();
                            now = response.getNow();
                            updateUi();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("posts_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "get_posts");
                map.put("group_id",groupId);
                map.put("cursor", "0");

                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
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
           // Toast.makeText(getActivity(), "result", Toast.LENGTH_SHORT).show();
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

    private void removePost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("remove_post", response);
                        ArrayList<PostResponse> newPosts = new ArrayList<>(Arrays.asList(posts));
                        for (int i = 0; i < newPosts.size(); i++) {
                            if (newPosts.get(i).getPostId() == postId){
                                newPosts.remove(i);
                                posts = newPosts.toArray(new PostResponse[newPosts.size()]);
                                adapter.notifyDataSetChanged();
                                break;
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

                map.put("method", "remove_post");
                map.put("user_id", userId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void savePost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("save_post", response);
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

                map.put("method", "save_post");
                map.put("user_id", userId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void reportPost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("report_post", response);
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

                map.put("method", "report_post");
                map.put("user_id", userId);
                map.put("post_id", String.valueOf(postId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void showPostPopupWindow(View anchor, final boolean owner, final int postId, final int postOwnerId) {
        final ListPopupWindow popupWindow = new ListPopupWindow(getActivity());

        List<MenuCustomItem> itemList = new ArrayList<>();
        if (owner){
            itemList.add(new MenuCustomItem(getActivity().getResources().getString(R.string.edit), R.drawable.ic_edit));
            itemList.add(new MenuCustomItem(getActivity().getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(getActivity().getResources().getString(R.string.remove), R.drawable.ic_remove));
        } else {
            itemList.add(new MenuCustomItem(getActivity().getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(getActivity().getResources().getString(R.string.report_post), R.drawable.ic_report));
        }


        ListAdapter adapter = new ListPopupWindowAdapter(getActivity(), itemList);
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (String.valueOf(postOwnerId).contentEquals(String.valueOf(userId))){
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
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }
}
