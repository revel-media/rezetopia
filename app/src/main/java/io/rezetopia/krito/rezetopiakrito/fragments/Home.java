package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.activities.CommentActivity;
import io.rezetopia.krito.rezetopiakrito.activities.CreatePostActivity;
import io.rezetopia.krito.rezetopiakrito.helper.PostRecyclerAdapter;
import io.rezetopia.krito.rezetopiakrito.model.operations.HomeOperations;
import io.rezetopia.krito.rezetopiakrito.model.operations.UserOperations;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeed;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.EventResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.GroupPostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeedItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.VendorPostsResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.ListPopupWindowAdapter;
import io.rezetopia.krito.rezetopiakrito.helper.MenuCustomItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    //todo
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int COMMENT_ACTIVITY_RESULT = 1001;
    private static final int CREATE_POST_RESULT = 1002;
    private static final int VIEW_HEADER = 1;
    private static final int VIEW_POST = 2;
    private static final int VIEW_PRODUCT = 3;
    private static final int VIEW_EVENT = 4;
    private static final int VIEW_VENDOR_POST = 5;
    private WebView webview;
    String distfile = "";
    private PostResponse[] posts;
    private ProductResponse[] products;
    private VendorPostsResponse[] vendorPosts;
    private EventResponse[] events;
    private GroupPostResponse[] groupPosts;
    private ArrayList<NewsFeedItem> newsFeedItems;
    private NewsFeed newsFeed;
    private RecyclerView recyclerView;
    ProgressBar homeProgress;
    private PostRecyclerAdapter adapter;
    private EditText commentEditText;
    int lastCursor = 0;
    //int adapterPos;
    RequestQueue requestQueue;
    long now;
    String userId;
    SwipeRefreshLayout homeSwipeView;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;
    HomeCallback homeCallback;
    boolean loadingData = false;
    String cursor = "0";
    int start = 0, end = 0;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnCallback mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homePostsRecyclerView);
        commentEditText = view.findViewById(R.id.commentEditText);
        homeProgress = view.findViewById(R.id.homeProgress);


        homeProgress.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        homeProgress.setVisibility(View.VISIBLE);

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        homeSwipeView = view.findViewById(R.id.homeSwipeView);


        requestQueue = Volley.newRequestQueue(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());

        HomeOperations.setRequestQueue(requestQueue);

        fetchNewsFeed();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 || dy < 0)
                    homeCallback.onScroll(false);

                if(dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();


                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount){
                        //Snackbar.make(homeHeader, R.string.loading, BaseTransientBottomBar.LENGTH_LONG).show();
                        //adapter.notifyItemInserted(adapter.addItem());
                        if (!loadingData){
                            Log.v("SCROLL_DOWN", "Last Item Wow !");
                            loadingData = true;
                            adapter.addItem();
                            fetchNewsFeed();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    homeCallback.onScroll(true);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        homeSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                homeSwipeView.setRefreshing(true);
//                fetchNewsFeed(true);
            }
        });

        return view;
    }

    public interface HomeCallback{
        void onScroll(boolean show);
        void onProfile();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //mListener = (OnCallback) context;

        if (context instanceof HomeCallback) {
            homeCallback = (HomeCallback) context;
        } else {

            throw new RuntimeException(context.toString()
                    + " must implement OnCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
        homeCallback = null;
    }


//    public interface OnCallback {
//        void onProfile();
//    }

//    private class HeaderViewHolder extends RecyclerView.ViewHolder{
//
//        private RelativeLayout createPostLayout;
//
//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//
//            createPostLayout = itemView.findViewById(R.id.createPostLayout);
//
//            createPostLayout.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
//                    startActivityForResult(intent, CREATE_POST_RESULT);
//                }
//            });
//        }
//    }
//
//    private class PostViewHolder extends RecyclerView.ViewHolder{
//
//        TextView postTextView;
//        Button likeButton;
//        Button commentButton;
//        TextView dateView;
//        TextView usernameView;
//        ImageView ppView;
//        ImageView postSideMenu;
//        Button postShareButton;
//        ImageView verfiyView;
//        ImageView postImage;
//        FrameLayout verifyHeader;
//
//        //todo
//        public PostViewHolder(final View itemView) {
//            super(itemView);
//
//            postTextView = itemView.findViewById(R.id.postTextView);
//            likeButton = itemView.findViewById(R.id.postLikeButton);
//            commentButton = itemView.findViewById(R.id.postCommentButton);
//            dateView = itemView.findViewById(R.id.postDateView);
//            usernameView = itemView.findViewById(R.id.postUserName);
//            ppView = itemView.findViewById(R.id.ppView);
//            postSideMenu = itemView.findViewById(R.id.postSideMenu);
//            postShareButton = itemView.findViewById(R.id.postShareButton);
//            verfiyView = itemView.findViewById(R.id.verfiyView);
//            postImage = itemView.findViewById(R.id.postImage);
//            verifyHeader = itemView.findViewById(R.id.verifyHeader);
//
//
//            postShareButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertFragment fragment = AlertFragment.createFragment("Post share is coming soon");
//                    fragment.show(getActivity().getFragmentManager(), null);
//                }
//            });
//        }
//
//        public void bind(final NewsFeedItem item, final int pos) {
//
////            if (item.getItemImage() != null){
////                Picasso.with(getActivity()).load(item.getItemImage()).into(ppView);
////            } else {
////                ppView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.default_avatar));
////            }
////
////            if (item.getOwnerId() == 1){
////                verifyHeader.setVisibility(View.VISIBLE);
////            } else {
////                verifyHeader.setVisibility(View.GONE);
////            }
////
////            if (item.getPostAttachment() != null && item.getPostAttachment().getImages() != null) {
////                if (item.getPostAttachment().getImages().length > 0) {
////                    if (item.getPostAttachment().getImages()[0].getPath() != null) {
////                        postImage.setVisibility(View.VISIBLE);
////                        Picasso.with(getActivity()).load(item.getPostAttachment().getImages()[0].getPath()).into(postImage);
////
////                        postImage.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                Intent intent = new Intent(getActivity(), ImageActivity.class);
////                                intent.putExtra("url_extra", item.getPostAttachment().getImages()[0].getPath());
////                                intent.putExtra("item_view", true);
////                                startActivity(intent);
////                            }
////                        });
////                    } else {
////                        postImage.setVisibility(View.GONE);
////                    }
////                } else {
////                    postImage.setVisibility(View.GONE);
////                }
////            } else {
////                postImage.setVisibility(View.GONE);
////            }
////
////            String postText = null;
////            if (item.getOwnerName() != null){
////                usernameView.setText(item.getOwnerName());
////            }
////            Date date = null;
////            try {
////                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
////            long milliseconds = date.getTime();
////            long millisecondsFromNow = milliseconds - now;
////            dateView.setText(DateUtils.getRelativeDateTimeString(getActivity(), milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));
////            if (item.getPostText() != null && !item.getPostText().isEmpty()){
////                postTextView.setText(item.getPostText());
////            }
//
//
//            /*try {
//                postText = URLEncoder.encode(item.getPostText(), "ISO-8859-1");
//                postText = URLDecoder.decode(postText, "UTF-8");
//
//                byte[] stringByts = postText.getBytes("UTF-8");
//                StringBuilder builder = new StringBuilder(stringByts.length);
//                for (byte b:stringByts) {
//                    builder.append(b);
//                }
//                postText = builder.toString();
//                Log.i("postText", "bind: " + postText);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }*/
////            String likeString = getActivity().getResources().getString(R.string.like);
////            if (item.getLikes() != null && item.getLikes().length > 0){
////
////                likeButton.setText(item.getLikes().length + " " + likeString);
////                Log.e("post_like ->> " + item.getPostText(), item.getLikes().length + " " + likeString);
////
////                //Log.e("loggedInUserId", userId);
////                for (int id : item.getLikes()) {
////                    //Log.e("likesUserId", String.valueOf(id));
////                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
////                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
////                        break;
////                    } else {
////                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);
////                    }
////                }
////            } else {
////                likeButton.setText(likeString);
////                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);
////            }
////            String commentString = getActivity().getResources().getString(R.string.comment);
////            if (item.getCommentSize() > 0){
////
////                commentButton.setText(item.getCommentSize() + " " + commentString);
////                Log.e("post_comment ->> " + item.getPostText(), (item.getCommentSize() + " " + commentString));
////            } else {
////                commentButton.setText(commentString);
////            }
////
////            commentButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    Intent intent = CommentActivity.createIntent(item.getLikes(), item.getId(), now, item.getOwnerId(),
////                            getActivity());
////
////                    //adapterPos = pos;
////                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);
////                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
////
////                }
////            });
////
////            //todo
////            likeButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////
////                    String likeString = getActivity().getResources().getString(R.string.like);
////
////                    if (item.getLikes() != null) {
////                        for (int i = 0; i < item.getLikes().length; i++) {
////                            if (item.getLikes()[i] == Integer.parseInt(userId)) {
////
////                                if (item.getLikes().length > 1) {
////                                    likeButton.setText((item.getLikes().length - 1) + " " + likeString);
////                                } else {
////                                    likeButton.setText(likeString);
////                                }
////                                likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
////                                reverseLike(item, pos);
////                                return;
////                            }
////                        }
////                    }
////
////
////                    likeButton.setText((item.getLikes().length + 1) + " " + likeString);
////                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
////                    performLike(item, pos);
////
////                }
////            });
////
////            usernameView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    if (item.getOwnerId() == Integer.parseInt(userId)) {
////                        mListener.onProfile();
////                    } else if (item.getType() == NewsFeedItem.POST_TYPE) {
////                        startOtherProfile(item);
////                    }
////                }
////            });
////
////            ppView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    if (item.getOwnerId() == Integer.parseInt(userId)) {
////                        mListener.onProfile();
////                    } else if (item.getType() == NewsFeedItem.POST_TYPE) {
////                        startOtherProfile(item);
////                        //Log.i("IMAGE", "onClick: " + item.getItemImage());
////                    }
////                }
////            });
////
////
////            postSideMenu.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    if (String.valueOf(item.getOwnerId()).contentEquals(String.valueOf(userId))) {
////                        showPostPopupWindow(postSideMenu, true, item.getId(), item.getOwnerId());
////                    } else {
////                        showPostPopupWindow(postSideMenu, false, item.getId(), item.getOwnerId());
////                    }
////                }
////            });
//        }
//
//        private void startOtherProfile(NewsFeedItem item){
//            Intent intent = OtherProfileActivity.createIntent(
//                    String.valueOf(item.getOwnerId()),
//                    item.getOwnerName(),
//                    item.getItemImage(),
//                    getActivity());
//            startActivity(intent);
//        }
//
//        private void performLike(final NewsFeedItem item, final int pos){
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("volley response", "onResponse: " + response);
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                if (!jsonObject.getBoolean("error")){
//                                    int[] likes = new int[item.getLikes().length + 1];
//                                    for (int i = 0; i < item.getLikes().length; i++) {
//                                        likes[i] = item.getLikes()[i];
//                                    }
//
//                                    likes[likes.length - 1] = Integer.parseInt(userId);
//                                    item.setLikes(likes);
//                                    //adapter.notifyItemChanged(pos);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("like_error", "onErrorResponse: " + error.getMessage());
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//
//                    map.put("method", "post_like");
//                    map.put("userId", userId);
//                    map.put("owner_id", String.valueOf(item.getOwnerId()));
//                    map.put("post_id", String.valueOf(item.getId()));
//                    map.put("add_like", String.valueOf(true));
//
//                    return map;
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        }
//
//        private void reverseLike(final NewsFeedItem item, final int pos){
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("volley response", "onResponse: " + response);
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                if (!jsonObject.getBoolean("error")){
//
//                                    ArrayList<Integer> likesList = new ArrayList<>();
//
//                                    for (int id : item.getLikes()) {
//                                        if (id != Integer.parseInt(userId)){
//                                            likesList.add(id);
//                                        }
//                                    }
//
//                                    int[] likes = new int[likesList.size()];
//
//                                    for(int i = 0; i < likesList.size(); i++) {
//                                        likes[i] = likesList.get(i);
//                                    }
//
//                                    item.setLikes(likes);
//                                    //adapter.notifyItemChanged(pos);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("unlike_error", "onErrorResponse: " + error.getMessage());
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//
//                    map.put("method", "post_like");
//                    map.put("userId", userId);
//                    map.put("owner_id", String.valueOf(item.getOwnerId()));
//                    map.put("post_id", String.valueOf(item.getId()));
//                    map.put("remove_like", String.valueOf(true));
//
//                    return map;
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        }
//    }
//
//    private class VendorProductHolder extends RecyclerView.ViewHolder{
//
//        TextView postUserName;
//        TextView postDateView;
//        ImageView postSideMenu;
//        TextView productTitleView;
//        TextView productDetailView;
//        TextView priceView;
//        TextView avilView;
//        CustomButton productBuyNow;
//        CircleImageView ppView;
//        ImageView productImageView;
//
//        public VendorProductHolder(View itemView) {
//            super(itemView);
//
//            postUserName = itemView.findViewById(R.id.postUserName);
//            postDateView = itemView.findViewById(R.id.postDateView);
//            postSideMenu = itemView.findViewById(R.id.postSideMenu);
//            productTitleView = itemView.findViewById(R.id.productTitleView);
//            productDetailView = itemView.findViewById(R.id.productDetailView);
//            priceView = itemView.findViewById(R.id.priceView);
//            avilView = itemView.findViewById(R.id.avilView);
//            productBuyNow = itemView.findViewById(R.id.productBuyNow);
//            ppView = itemView.findViewById(R.id.ppView);
//            productImageView = itemView.findViewById(R.id.productImageView);
//        }
//
//        public void bind(final NewsFeedItem item){
//
//            if (item.getProductImageUrl() != null){
//                Picasso.with(getActivity()).load(item.getProductImageUrl()).into(ppView);
//            }
//
//            if (item.getItemImage() != null){
//                Picasso.with(getActivity()).load(item.getItemImage()).into(productImageView);
//            }
//
//
//            productTitleView.setText(item.getProductTitle());
//            productDetailView.setText(item.getDescription());
//            priceView.setText(String.valueOf(item.getProductPrice()));
//            postUserName.setText(item.getOwnerName());
//            if (item.getProductSoldAmount() < item.getProductAmount()){
//                avilView.setText(R.string.available);
//                productBuyNow.setText(R.string.buy);
//            } else {
//                avilView.setText(R.string.unavailable);
//                productBuyNow.setText(R.string.sold_out);
//                productBuyNow.setEnabled(false);
//            }
//
//            productBuyNow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CreateBuyRequestFragment fragment = CreateBuyRequestFragment.createFragment(item.getOwnerId(), item.getId(), item.getStoreId());
//                    fragment.show(getActivity().getFragmentManager(), null);
//                }
//            });
//
//            ppView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    /*if (item.getType() == NewsFeedItem.PRODUCT_TYPE){
//                        if (item.getOwnerId() > 0) {
//                            Intent intent = VendorActivity.createIntent(String.valueOf(item.getOwnerId()), getActivity());
//                            startActivity(intent);
//                        }
//                    }*/
//                }
//            });
//
//            postUserName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    /*if (item.getType() == NewsFeedItem.PRODUCT_TYPE){
//                        if (item.getOwnerId() > 0) {
//                            Intent intent = VendorActivity.createIntent(String.valueOf(item.getOwnerId()), getActivity());
//                            startActivity(intent);
//                        }
//                    }*/
//                }
//            });
//        }
//    }
//
//    private class EventViewHolder extends RecyclerView.ViewHolder{
//
//        TextView eventUserName;
//        TextView eventDateView;
//        ImageView postSideMenu;
//        TextView descriptionView ;
//        Button eventInterestedButton;
//        Button eventGoingButton;
//
//        ImageView ppView;
//
//        public EventViewHolder(View itemView) {
//            super(itemView);
//
//            eventUserName = itemView.findViewById(R.id.postUserName);
//            eventDateView = itemView.findViewById(R.id.postDateView);
//            postSideMenu = itemView.findViewById(R.id.postSideMenu);
//            descriptionView = itemView.findViewById(R.id.descriptionView);
//            eventGoingButton = itemView.findViewById(R.id.eventGoingButton);
//            eventInterestedButton = itemView.findViewById(R.id.eventInterestedButton);
//            ppView = itemView.findViewById(R.id.ppView);
//        }
//
//        public void bind(final NewsFeedItem item){
//            eventUserName.setText(item.getItemName());
//            descriptionView.setText(item.getDescription());
//            ppView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getType() == NewsFeedItem.EVENT_TYPE){
//                        if (item.getId() > 0) {
//                            Intent intent = ViewEventActivity.createIntent(item.getId(), getActivity());
//                            startActivity(intent);
//                        }
//                    }
//                }
//            });
//
//            eventUserName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getType() == NewsFeedItem.EVENT_TYPE){
//                        if (item.getId() > 0) {
//                            Intent intent = ViewEventActivity.createIntent(item.getId(), getActivity());
//                            startActivity(intent);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    private class VendorPostViewHolder extends RecyclerView.ViewHolder{
//
//        TextView postTextView;
//        Button likeButton;
//        Button commentButton;
//        TextView dateView;
//        TextView usernameView;
//        ImageView ppView;
//        ImageView postSideMenu;
//        ImageView hiddenMenuPositionView;
//        public VendorPostViewHolder(final View itemView) {
//            super(itemView);
//
//            postTextView = itemView.findViewById(R.id.postTextView);
//            likeButton = itemView.findViewById(R.id.postLikeButton);
//            commentButton = itemView.findViewById(R.id.postCommentButton);
//            dateView = itemView.findViewById(R.id.postDateView);
//            usernameView = itemView.findViewById(R.id.postUserName);
//            ppView = itemView.findViewById(R.id.ppView);
//            postSideMenu = itemView.findViewById(R.id.postSideMenu);
//            //hiddenMenuPositionView = itemView.findViewById(R.id.hiddenMenuPositionView);
//        }
//
//        public void bind(final NewsFeedItem item, final int pos) {
//            String postText = null;
//            if (item.getItemName() != null){
//                usernameView.setText(item.getItemName());
//            }
//            Date date = null;
//            try {
//                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(item.getCreatedAt());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            long milliseconds = date.getTime();
//            long millisecondsFromNow = milliseconds - now;
//            dateView.setText(DateUtils.getRelativeDateTimeString(getActivity(), milliseconds, millisecondsFromNow, DateUtils.DAY_IN_MILLIS, 0));
//
//            try {
//                postText = URLEncoder.encode(item.getPostText(), "ISO-8859-1");
//                postText = URLDecoder.decode(postText, "UTF-8");
//                postTextView.setText(postText);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//            if (item.getLikes() != null && item.getLikes().length > 0){
//                //likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
//                likeButton.setText(item.getLikes().length + " Like");
//
//
//                Log.e("loggedInUserId", userId);
//                for (int id : item.getLikes()) {
//                    Log.e("likesUserId", String.valueOf(id));
//                    if (String.valueOf(id).contentEquals(String.valueOf(userId))){
//                        likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
//                        break;
//                    }
//                }
//            }
//
//            if (item.getPostComments() != null && item.getPostComments().length > 0){
//                //commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
//                commentButton.setText(item.getPostComments().length + " Comment");
//            }
//
//            /*commentButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ArrayList<CommentResponse> comments = new ArrayList<>(Arrays.asList(item.getPostComments()));
//                    Intent intent = CommentActivity.createIntent(comments, item.getLikes(), item.getId(), now, item.getOwnerId(),
//                            getActivity());
//
//                    adapterPos = pos;
//                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);
//
//                    //startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
//                }
//            });
//
//            likeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    for (int i = 0; i < item.getLikes().length; i++) {
//                        if (item.getLikes()[i] == Integer.parseInt(userId)){
//                            reverseLike(item, pos);
//                            return;
//                        }
//                    }
//
//                    performLike(item, pos);
//                }
//            });
//
//            usernameView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getOwnerId() == Integer.parseInt(userId)){
//                        mListener.onProfile();
//                    } else if (item.getType() == NewsFeedItem.POST_TYPE){
//                        startOtherProfile(pos);
//                    }
//                }
//            });
//
//            ppView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (item.getOwnerId() == Integer.parseInt(userId)){
//                        mListener.onProfile();
//                    } else if (item.getType() == NewsFeedItem.POST_TYPE){
//                        startOtherProfile(pos);
//                    }
//                }
//            });
//
//            postSideMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (String.valueOf(item.getOwnerId()).contentEquals(String.valueOf(userId))) {
//                        showPostPopupWindow(postSideMenu, true, item.getId(), item.getOwnerId());
//                    } else {
//                        showPostPopupWindow(postSideMenu, false, item.getId(), item.getOwnerId());
//                    }
//                }
//            });*/
//        }
//
//        private void startOtherProfile(int position){
//            Intent intent = OtherProfileActivity.createIntent(
//                    String.valueOf(newsFeedItems.get(position).getOwnerId()),
//                    newsFeedItems.get(position).getOwnerName(),
//                    newsFeedItems.get(position).getItemImage(),
//                    getActivity());
//            startActivity(intent);
//        }
//
//        private void performLike(final NewsFeedItem item, final int pos){
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("volley response", "onResponse: " + response);
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                if (!jsonObject.getBoolean("error")){
//                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
//                                    likeButton.setText((item.getLikes().length + 1) + " Like");
//                                    int[] likes = new int[item.getLikes().length + 1];
//                                    for (int i = 0; i < posts[pos].getLikes().length; i++) {
//                                        likes[i] = posts[pos].getLikes()[i];
//                                    }
//
//                                    likes[posts[pos].getLikes().length] = Integer.parseInt(userId);
//                                    posts[pos].setLikes(likes);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//
//                    map.put("method", "post_like");
//                    map.put("userId", userId);
//                    map.put("owner_id", String.valueOf(item.getOwnerId()));
//                    map.put("post_id", String.valueOf(item.getId()));
//                    map.put("add_like", String.valueOf(true));
//
//                    return map;
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        }
//
//        private void reverseLike(final NewsFeedItem item, final int pos){
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.18:80/reze/user_post.php",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("volley response", "onResponse: " + response);
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                if (!jsonObject.getBoolean("error")){
//
//                                    likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star,  0, 0, 0);
//
//                                    if (item.getLikes().length > 1){
//                                        likeButton.setText((item.getLikes().length - 1) + " Like");
//                                    } else {
//                                        likeButton.setText("Like");
//                                    }
//
//
//
//                                    ArrayList<Integer> likesList = new ArrayList<>();
//
//                                    for (int id : item.getLikes()) {
//                                        if (id != Integer.parseInt(userId)){
//                                            likesList.add(id);
//                                        }
//                                    }
//
//                                    int[] likes = new int[likesList.size()];
//
//                                    for(int i = 0; i < likesList.size(); i++) {
//                                        likes[i] = likesList.get(i);
//                                    }
//
//                                    posts[pos].setLikes(likes);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//
//                    map.put("method", "post_like");
//                    map.put("userId", userId);
//                    map.put("owner_id", String.valueOf(item.getOwnerId()));
//                    map.put("post_id", String.valueOf(item.getId()));
//                    map.put("remove_like", String.valueOf(true));
//
//                    return map;
//                }
//            };
//
//            requestQueue.add(stringRequest);
//        }
//    }

//    private class PostRecyclerAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder>{
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            if (viewType == VIEW_HEADER){
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.create_post_header, parent, false);
//                return new HeaderViewHolder(view);
//            } else if (viewType == VIEW_POST){
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_card, parent, false);
//                return new PostViewHolder(view);
//            } else if (viewType == VIEW_PRODUCT){
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_product_card, parent, false);
//                return new VendorProductHolder(view);
//            } else if (viewType == VIEW_EVENT){
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.event_card, parent, false);
//                return new EventViewHolder(view);
//            } else {
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_card, parent, false);
//                return new VendorPostViewHolder(view);
//            }
//
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            if (holder instanceof PostViewHolder) {
//                PostViewHolder pHolder = (PostViewHolder) holder;
//                pHolder.bind(newsFeedItems.get(position-1), position-1);
//            } else if (holder instanceof VendorProductHolder){
//                VendorProductHolder productHolder = (VendorProductHolder) holder;
//                productHolder.bind(newsFeedItems.get(position-1));
//            } else if (holder instanceof EventViewHolder){
//                EventViewHolder eventViewHolder = (EventViewHolder) holder;
//                eventViewHolder.bind(newsFeedItems.get(position-1));
//            } else if (holder instanceof  VendorPostViewHolder){
//                VendorPostViewHolder vendorPostViewHolder = (VendorPostViewHolder) holder;
//                //todo replace holder with adapter
//                vendorPostViewHolder.bind(newsFeedItems.get(position-1), position-1);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return newsFeedItems.size() + 1;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (isPositionHeader(position)){
//                return VIEW_HEADER;
//            } else if (newsFeedItems.get(position-1).getType() == NewsFeedItem.PRODUCT_TYPE){
//                return VIEW_PRODUCT;
//            } else if (newsFeedItems.get(position-1).getType() == NewsFeedItem.EVENT_TYPE){
//                return VIEW_EVENT;
//            } else if (newsFeedItems.get(position-1).getType() == NewsFeedItem.VENDOR_POST_TYPE){
//                return VIEW_VENDOR_POST;
//            }
//
//            return VIEW_POST;
//        }
//
//        private boolean isPositionHeader(int position) {
//            return position == 0;
//        }
//    }

//    private void fetchNewsFeed(boolean refresh){
//        if (refresh){
//            fetchNews("0", true);
//        } else if (newsFeed != null){
//            fetchNews(String.valueOf(newsFeed.getNextCursor()), false);
//        } else {
//            fetchNews("0", false);
//        }
//    }

    private void fetchNewsFeed(){
        //Log.i("POST_CURSOR", "fetchNewsFeed: " + cursor);
        HomeOperations.fetchNewsFeed(userId, cursor);
        HomeOperations.setFeedCallback(new HomeOperations.NewsFeedCallback() {
            @Override
            public void onSuccess(NewsFeed feed) {
                if (newsFeed != null && feed.getItems() != null && feed.getItems().size() > 0){
                    final int lastItem = adapter.removeLastItem();
                    Log.i("newsFeed_size", "onSuccess: " + newsFeed.getItems().size() + " " + feed.getItems().size());
                    start = newsFeed.getItems().size();
                    newsFeed.addAllItems(feed.getItems());
                    newsFeed.setNow(feed.getNow());
                    newsFeed.setNextCursor(feed.getNextCursor());
                    end = newsFeed.getItems().size()-1;
                    Log.i("Array_size", "onSuccess: " + start + " : " + end);
                    cursor = String.valueOf(Integer.parseInt(cursor) + 10);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.removeLastItem();
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(lastItem);
                            //adapter.notifyItemRangeInserted(Integer.parseInt(cursor) - 10, Integer.parseInt(cursor));
                        }
                    });
                    //updateUi(0, 0);
                } else {
                    newsFeed = feed;
                    updateUi(0, 0);
                    cursor = String.valueOf(Integer.parseInt(cursor) + 10);
                    homeProgress.setVisibility(View.GONE);
                }
                loadingData = false;
            }

            @Override
            public void onError(int error) {
                //String errorString = getActivity().getResources().getString(error);
                //Log.i("news_feed_error", "onError: " + errorString);
                //Snackbar.make(homeHeader, error, BaseTransientBottomBar.LENGTH_LONG).show();
                loadingData = false;
            }
        });
    }

    private void fetchNews(String cursor, final boolean refresh){
        Log.e("cursor => ", cursor );
        UserOperations.fetchNewsFeed(userId, cursor);
        UserOperations.setNewsFeedCallback(new UserOperations.NewsFeedCallback() {
            @Override
            public void onResponse(NewsFeed news) {
                newsFeed = news;
                if (news.getNextCursor() != lastCursor) {
                    if (newsFeedItems != null && newsFeedItems.size() > 0 && !refresh) {
                        newsFeedItems.addAll(news.getItems());
                    } else {
                        newsFeedItems = news.getItems();
                    }
                }

                homeProgress.setVisibility(View.GONE);
                homeSwipeView.setRefreshing(false);
                lastCursor = news.getNextCursor();
                //updateUi();
            }

            @Override
            public void onError(String error) {
                homeProgress.setVisibility(View.GONE);
            }
        });
    }

    private void updateUi(int s, int e){
        if (adapter == null){
            adapter = new PostRecyclerAdapter(getActivity(), newsFeed.getItems(), newsFeed.getNow(), userId);
            recyclerView.setAdapter(adapter);
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            adapter.setCallback(new PostRecyclerAdapter.AdapterCallback() {
                @Override
                public void onStartComment(NewsFeedItem item, long now) {
                    Intent intent = CommentActivity.createIntent(item.getLikes(), item.getPostId(), now, item.getOwnerId(), getActivity());

                    startActivityForResult(intent, COMMENT_ACTIVITY_RESULT);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }

                @Override
                public void onItemAdded(final int position) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(position);
                        }
                    });
                }

                @Override
                public void onItemRemoved(final int position) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemRemoved(position);
                        }
                    });
                }

                @Override
                public void onPostCreated(final int position) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(position);
                        }
                    });
                }

                @Override
                public void onStartCreatePost() {
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    startActivityForResult(intent, CREATE_POST_RESULT);
                }
            });

        } else if (s > 0 && e > 0){
            adapter.notifyItemRangeInserted(s, e);
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
                //CommentResponse commentResponse = (CommentResponse) data.getSerializableExtra("comment");
                int postId = data.getIntExtra("post_id", 0);
                for (int i = 0; i < newsFeedItems.size(); i++) {
                    Log.e("feed_size", String.valueOf(newsFeedItems.size()));
                    Log.e("postIDs", String.valueOf(newsFeedItems.get(i).getId()) );
                    if (newsFeedItems.get(i).getType() == NewsFeedItem.POST_TYPE) {
                        if (newsFeedItems.get(i).getId() == postId) {
                            int commentSize = data.getIntExtra("added_size", newsFeedItems.get(i).getCommentSize());
                            newsFeedItems.get(i).setCommentSize(commentSize);
                            adapter.notifyItemChanged(i);

                        }
                    }
                }

            }
        } else if (requestCode == CREATE_POST_RESULT){
            if (data != null){
                PostResponse returnPost = (PostResponse) data.getSerializableExtra("post");

                if (returnPost != null) {

                    NewsFeedItem item = new NewsFeedItem();
                    item.setId(returnPost.getPostId());
                    item.setCreatedAt(returnPost.getCreatedAt());
                    item.setOwnerId(Integer.parseInt(returnPost.getUserId()));
                    item.setOwnerName(returnPost.getUsername());
                    item.setPostText(returnPost.getText());
                    item.setPostAttachment(returnPost.getAttachment());
                    item.setLikes(null);
                    item.setPostComments(null);
                    item.setType(NewsFeedItem.POST_TYPE);
                    //NewsFeedItem newsTemp = newsFeedItems.get(0);
                    //newsFeedItems.set(0, item);
                    //newsFeedItems.add(newsTemp);
                    //newsFeedItems.add(item);
                    adapter.addPostItem(item);
                }

                //adapter.notifyDataSetChanged();

                //adapter.notifyItemInserted(0);
            }
        }
    }

    private void removePost(final int postId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
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

        requestQueue.add(stringRequest);
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


        ListAdapter adapter = new ListPopupWindowAdapter(getActivity(), itemList, R.layout.custom_menu);
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

    private class news extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            fetchNews(String.valueOf(newsFeed.getNextCursor()), false);
            return null;
        }
    }
}
