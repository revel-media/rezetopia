package io.rezetopia.krito.rezetopiakrito.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.OtherProfileActivity;
import io.rezetopia.krito.rezetopiakrito.fragments.AlertFragment;
import io.rezetopia.krito.rezetopiakrito.model.operations.HomeOperations;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeedItem;

/**
 * Created by Ahmed Ali on 6/4/2018.
 */

public class PostRecyclerAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder>{

    private static final int VIEW_HEADER = 1;
    private static final int VIEW_POST = 2;
    private static final int VIEW_PROGRESS = 3;
    private static final int VIEW_POST_2 = 4;
    private static final int VIEW_POST_3 = 5;
    private static final int VIEW_POST_4 = 6;
    private static final int VIEW_POST_5 = 7;

    private Context context;
    private ArrayList<NewsFeedItem> items;
    private long now;
    private String userId;
    private AdapterCallback callback;


    public PostRecyclerAdapter(Context context, ArrayList<NewsFeedItem> items, long now, String userId) {
        this.context = context;
        this.items = items;
        this.now = now;
        this.userId = userId;
    }

    public ArrayList<NewsFeedItem> getItems() {
        return items;
    }

    public void addItem(){
        NewsFeedItem item = new NewsFeedItem();
        item.setType(1009);
        items.add(item);
        callback.onItemAdded(items.size());
    }

    public void addPostItem(NewsFeedItem item){
        //items.add(0, item);
        items.add(0, item);
        //callback.onPostCreated(1);
        notifyDataSetChanged();
    }

    public int removeLastItem(){

        for (NewsFeedItem item:items) {
            if (item.getType() == 1009){
                items.remove(items.size() - 1);
                callback.onItemRemoved(items.size());
                return items.size();
            }
        }

        return 0;
    }

    public void addCommentItem(int postId, int commentSize){
        for (int i = 0; i < items.size(); i++) {
            Log.e("feed_size", String.valueOf(items.size()));
            Log.e("postIDs", String.valueOf(items.get(i).getId()) );
            if (items.get(i).getType() == NewsFeedItem.POST_TYPE) {
                if (items.get(i).getId() == postId) {
                    items.get(i).setCommentSize(commentSize);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void setCallback(AdapterCallback adapterCallback){
        this.callback = adapterCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER){
            View view = LayoutInflater.from(context).inflate(R.layout.create_post_header, parent, false);
            return new HeaderViewHolder(view);
        }

        else if (viewType == VIEW_POST){
            View view = LayoutInflater.from(context).inflate(R.layout.post_card, parent, false);
            return new PostViewHolder(view);
        }
//        else if (viewType == VIEW_POST_2){
//            View view = LayoutInflater.from(context).inflate(R.layout.post_card_2, parent, false);
//            return new PostViewHolder(view);
//        } else if (viewType == VIEW_POST_3){
//            View view = LayoutInflater.from(context).inflate(R.layout.post_card_3, parent, false);
//            return new PostViewHolder(view);
//        } else if (viewType == VIEW_POST_4){
//            View view = LayoutInflater.from(context).inflate(R.layout.post_card_4, parent, false);
//            return new PostViewHolder(view);
//        } else if (viewType == VIEW_POST_5){
//            View view = LayoutInflater.from(context).inflate(R.layout.post_card_5, parent, false);
//            return new PostViewHolder(view);
//        }

        else {
            View view = LayoutInflater.from(context).inflate(R.layout.progress, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder) {
            PostViewHolder pHolder = (PostViewHolder) holder;
            pHolder.bind(items.get(position-1), position-1);
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)){
            return VIEW_HEADER;
        }
        else if (items.get(position - 1).getType() == 1009){
            return VIEW_PROGRESS;
        }

        else if (items.get(position - 1).getPostAttachment() != null && items.get(position - 1).getPostAttachment().getImages() != null && items.get(position - 1).getPostAttachment().getImages().length > 0){
            if (items.get(position - 1).getPostAttachment().getImages().length == 2){
                return VIEW_POST_2;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 3){
                return VIEW_POST_3;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 4){
                return VIEW_POST_4;
            } else if (items.get(position - 1).getPostAttachment().getImages().length == 5){
                return VIEW_POST_5;
            }
        }

        return VIEW_POST;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout createPostLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            createPostLayout = itemView.findViewById(R.id.createPostLayout);

            createPostLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartCreatePost();
                }
            });
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.homeProgress);

            progressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
                    .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

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
        Button postShareButton;
        ImageView verifyView;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        ImageView image4;
        ImageView image5;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
            dateView = itemView.findViewById(R.id.postDateView);
            usernameView = itemView.findViewById(R.id.postUserName);
            ppView = itemView.findViewById(R.id.ppView);
            postSideMenu = itemView.findViewById(R.id.postSideMenu);
            postShareButton = itemView.findViewById(R.id.postShareButton);
            image1 = itemView.findViewById(R.id.postImage);
//            verifyView = itemView.findViewById(R.id.verifyView);
//            image1 = itemView.findViewById(R.id.postImage1);
//            image2 = itemView.findViewById(R.id.postImage2);
//            image3 = itemView.findViewById(R.id.postImage3);
//            image4 = itemView.findViewById(R.id.postImage4);
//            image5 = itemView.findViewById(R.id.postImage5);


            postShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    AlertFragment fragment = AlertFragment.createFragment("Post share is coming soon");
//                    fragment.show(context.getFragmentManager(), null);
                }
            });
        }

        public void bind(final NewsFeedItem item, final int pos) {

            if (item.getItemImage() != null){
                Picasso.with(context).load(item.getItemImage()).into(ppView);
            } else {
                ppView.setImageDrawable(context.getResources().getDrawable(R.drawable.default_avatar));
            }

            if (item.getPostAttachment() != null && item.getPostAttachment().getImages() != null && item.getPostAttachment().getImages().length > 0) {
                if (item.getPostAttachment().getImages().length == 1){
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                } else {
                    image1.setVisibility(View.GONE);
                }

                if (item.getPostAttachment().getImages().length == 2){
                    Log.i("IMAGE1", "bind: " + item.getPostAttachment().getImages()[0].getPath());
                    Log.i("IMAGE2", "bind: " + item.getPostAttachment().getImages()[1].getPath());
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                } else {
                    image1.setVisibility(View.GONE);
                    if (image2 != null){
                        image2.setVisibility(View.GONE);
                    }
                }

                if (item.getPostAttachment().getImages().length == 3){
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                } else {
                    image1.setVisibility(View.GONE);
                    if (image2 != null){
                        image2.setVisibility(View.GONE);
                    }
                    if (image3 != null){
                        image3.setVisibility(View.GONE);
                    }
                }

                if (item.getPostAttachment().getImages().length == 4){
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                    image4.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[3].getPath()).into(image4);
                } else {
                    image1.setVisibility(View.GONE);
                    if (image2 != null){
                        image2.setVisibility(View.GONE);
                    }
                    if (image3 != null){
                        image3.setVisibility(View.GONE);
                    }
                    if (image4 != null){
                        image4.setVisibility(View.GONE);
                    }
                }

                if (item.getPostAttachment().getImages().length == 5){
                    image1.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
                    image2.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[1].getPath()).into(image2);
                    image3.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[2].getPath()).into(image3);
                    image4.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[3].getPath()).into(image4);
                    image5.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(item.getPostAttachment().getImages()[4].getPath()).into(image5);
                } else {
                    image1.setVisibility(View.GONE);
                    if (image2 != null){
                        image2.setVisibility(View.GONE);
                    }
                    if (image3 != null){
                        image3.setVisibility(View.GONE);
                    }
                    if (image4 != null){
                        image4.setVisibility(View.GONE);
                    }
                    if (image5 != null){
                        image5.setVisibility(View.GONE);
                    }
                }

//                if (item.getPostAttachment().getImages()[0].getPath() != null) {
//                    image1.setVisibility(View.VISIBLE);
//                    Picasso.with(context).load(item.getPostAttachment().getImages()[0].getPath()).into(image1);
//                } else {
//                    image1.setVisibility(View.GONE);
//                }
            } else {
                image1.setVisibility(View.GONE);
                if (image2 != null){
                    image2.setVisibility(View.GONE);
                }
                if (image3 != null){
                    image3.setVisibility(View.GONE);
                }
                if (image4 != null){
                    image4.setVisibility(View.GONE);
                }
                if (image5 != null){
                    image5.setVisibility(View.GONE);
                }
            }

            String postText = null;
            if (item.getOwnerName() != null){
                usernameView.setText(item.getOwnerName());
            }

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = date.getTime();
            long millisecondsFromNow = milliseconds - now;
            dateView.setText(DateUtils.getRelativeDateTimeString(context, milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));

            postTextView.setText(item.getPostText());

            String likeString = context.getResources().getString(R.string.like);
            if (item.getLikes() != null && item.getLikes().length > 0){

                likeButton.setText(item.getLikes().length + " " + likeString);
                Log.e("post_like ->> " + item.getPostText(), item.getLikes().length + " " + likeString);

                //Log.e("loggedInUserId", userId);
                for (int id : item.getLikes()) {
                    //Log.e("likesUserId", String.valueOf(id));
                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green_small,  0, 0, 0);
                        break;
                    } else {
                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);
                    }
                }
            } else {
                likeButton.setText(likeString);
                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);
            }
            String commentString = context.getResources().getString(R.string.comment);
            if (item.getCommentSize() > 0){

                commentButton.setText(item.getCommentSize() + " " + commentString);
                Log.e("post_comment ->> " + item.getPostText(), (item.getCommentSize() + " " + commentString));
            } else {
                commentButton.setText(commentString);
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStartComment(item, now);
                }
            });

            //todo
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String likeString = context.getResources().getString(R.string.like);

                    if (item.getLikes() != null) {
                        for (int i = 0; i < item.getLikes().length; i++) {
                            if (item.getLikes()[i] == Integer.parseInt(userId)) {

                                if (item.getLikes().length > 1) {
                                    likeButton.setText((item.getLikes().length - 1) + " " + likeString);
                                } else {
                                    likeButton.setText(likeString);
                                }
                                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
                                reverseLike(item);
                                return;
                            }
                        }
                    }


                    if (item.getLikes() != null && item.getLikes().length > 0){
                        likeButton.setText((item.getLikes().length + 1) + " " + likeString);
                    } else {
                        likeButton.setText(("1 " + likeString));
                    }

                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green_small,  0, 0, 0);
                    performLike(item);

                }
            });

            usernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(item.getOwnerId()).contentEquals(userId)) {
                        //todo start profile activity
                    } else  {
                        startOtherProfile(item);
                    }
                }
            });

            ppView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(item.getOwnerId()).contentEquals(userId)) {
                        //todo start profile activity
                    } else  {
                        startOtherProfile(item);
                    }
                }
            });

            postSideMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (String.valueOf(item.getOwnerId()).contentEquals(String.valueOf(userId))) {
                        showPostPopupWindow(postSideMenu, true, item.getId(), String.valueOf(item.getOwnerId()));
                    } else {
                        showPostPopupWindow(postSideMenu, false, item.getId(), String.valueOf(item.getOwnerId()));
                    }
                }
            });
        }

        private void startOtherProfile(NewsFeedItem item){
//            Intent intent = OtherProfile.createIntent(item.getOwnerId(), context);
//            context.startActivity(intent);

            Intent intent = OtherProfileActivity.createIntent(
                    String.valueOf(item.getOwnerId()),
                    item.getOwnerName(),
                    item.getItemImage(),
                    context);
            context.startActivity(intent);
        }

        private void performLike(final NewsFeedItem item){
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postLike("add_like" ,userId, String.valueOf(item.getOwnerId()), String.valueOf(item.getPostId()));
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
                    int[] likes = new int[item.getLikes().length + 1];
                    for (int i = 0; i < item.getLikes().length; i++) {
                        likes[i] = item.getLikes()[i];
                    }

                    likes[likes.length - 1] = Integer.parseInt(userId);
                    item.setLikes(likes);
                }

                @Override
                public void onError(int error) {

                }
            });
        }

        private void reverseLike(final NewsFeedItem item){
            HomeOperations homeOperations = new HomeOperations();
            homeOperations.postUnlike("remove_like", userId, String.valueOf(item.getOwnerId()), String.valueOf(item.getPostId()));
            homeOperations.setLikeCallback(new HomeOperations.LikeCallback() {
                @Override
                public void onSuccess() {
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
                }

                @Override
                public void onError(int error) {

                }
            });
        }
    }

    private void showPostPopupWindow(View anchor, final boolean owner, final int postId, final String postOwnerId) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);

        List<MenuCustomItem> itemList = new ArrayList<>();
        if (owner){
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.edit), R.drawable.ic_edit));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.remove), R.drawable.ic_remove));
        } else {
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.save_post), R.drawable.ic_save));
            itemList.add(new MenuCustomItem(context.getResources().getString(R.string.report_post), R.drawable.ic_report));
        }


        ListAdapter adapter = new ListPopupWindowAdapter(context, itemList, R.layout.custom_menu);
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

    private void removePost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("remove_post", response);
                        for (int i = 0; i < items.size(); i++) {
                            if (String.valueOf(items.get(i).getPostId()).contentEquals(String.valueOf(postId))
                                    && items.get(i).getType() == NewsFeedItem.POST_TYPE){
                                items.remove(i);
                                items = new ArrayList<NewsFeedItem>(items);
                                //adapter.notifyDataSetChanged();
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

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void savePost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
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

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void reportPost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
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

        Volley.newRequestQueue(context).add(stringRequest);
    }

    public interface AdapterCallback{
        void onStartComment(NewsFeedItem item, long now);
        void onStartCreatePost();
        void onItemAdded(int position);
        void onItemRemoved(int position);
        void onPostCreated(int position);
    }
}
