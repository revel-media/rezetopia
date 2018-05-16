package io.rezetopia.krito.rezetopiakrito.model.operations;

import android.os.AsyncTask;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.EventResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.GroupPostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeed;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.NewsFeedItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.VendorPostsResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchResult;
import io.rezetopia.krito.rezetopiakrito.model.pojo.user.User;

public class UserOperations {

    private static final String baseUrl = "https://rezetopia.dev-krito.com/app/";
    static RegistrationCallback registrationCallback;
    static LoginCallback loginCallback;
    static FBLoginCallback fbLoginCallback;
    static NewsFeedCallback feedCallback;
    static GetProductsCallback productsCallback;
    static CreateProductsCallback createProductsCallback;
    static SearchCallback searchCallback;
    static RequestQueue requestQueue;


    public static void setRegistrationCallback(RegistrationCallback call){
        registrationCallback = call;
    }

    public static void setLoginCallback(LoginCallback call){
        loginCallback = call;
    }

    public static void setFBLoginCallback(FBLoginCallback call){
        fbLoginCallback = call;
    }

    public static void setNewsFeedCallback(NewsFeedCallback call){
        feedCallback = call;
    }

    public static void setSearchCallback(SearchCallback call){
        searchCallback = call;
    }

    public static void setProductsCallback(GetProductsCallback call){
        productsCallback = call;
    }

    public static void setCreateProductsCallback(CreateProductsCallback call){
        createProductsCallback = call;
    }

    public static void setRequestQueue(RequestQueue queue){
        requestQueue = queue;
    }


    public static void register(final User user){
        new RegisterTask().execute(user);
    }

    public static void login(final String email, final String password){
        new LoginTask().execute(email, password);
    }

    public static void FBLogin(final String name, final String email, final String birthday, final String imageUrl){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthday);
        user.setImageUrl(imageUrl);
        new FBLoginTask().execute(user);
    }

    public static void changePassword(String oldPass, String newPass){

    }

    public static void fetchNewsFeed(String userId, String cursor){
        new FetchNewsFeedTask().execute(userId, cursor);
    }

    public static void search(String q, String cursor){
        new SearchTask().execute(q, cursor);
    }

    public static void fetchProducts(){
        new FetchProductsTask().execute();
    }

    public interface RegistrationCallback {
        void onResponse(String id);
        void onError(String error);
    }

    public interface LoginCallback{
        void onResponse(String id);
        void onError(int error);
    }

    public interface FBLoginCallback{
        void onResponse(String id);
        void onError(String error);
    }

    public interface NewsFeedCallback{
        void onResponse(NewsFeed news);
        void onError(String error);
    }

    public interface GetProductsCallback {
        void onResponse(ProductResponse[] products);
        void onError(String error);
    }

    public interface CreateProductsCallback{
        void onResponse(ProductResponse products);
        void onError(String error);
    }

    public interface SearchCallback{
        void onResponse(SearchResult result);
        void onError(String error);
    }

    private static class LoginTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            String url = baseUrl + "login.php";

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")){
                            Log.e("login_response", response);
                            loginCallback.onResponse(jsonResponse.getString("id"));
                        } else {
                            loginCallback.onError(R.string.ef_msg_empty_images);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    if (error instanceof NetworkError) {
                        //message = String.valueOf();
                        loginCallback.onError(R.string.netwok_error);
                        return;
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        loginCallback.onError(R.string.server_error);
                        return;
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        loginCallback.onError(R.string.authi_failure);
                        return;
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        loginCallback.onError(R.string.parse_error);
                        return;
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        loginCallback.onError(R.string.no_connection);
                        return;
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        loginCallback.onError(R.string.time_out);
                        return;
                    }
                    loginCallback.onError(R.string.netwok_error);
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("login", "login");
                    params.put("mail", strings[0]);
                    params.put("password", strings[1]);
                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }

    private static class RegisterTask extends AsyncTask<User, String, Void>{

        @Override
        protected Void doInBackground(final User... users) {
            String url = baseUrl + "register.php";
            final User user = users[0];
            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")) {
                            Log.e("register", response);
                            registrationCallback.onResponse(jsonResponse.getString("id"));
                        } else {
                            registrationCallback.onError(jsonResponse.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        registrationCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        registrationCallback.onError(message.toString());
                        return;
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        registrationCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        registrationCallback.onError(message.toString());
                        return;
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        registrationCallback.onError(message.toString());
                        return;
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        registrationCallback.onError(message.toString());
                        return;
                    }
                    registrationCallback.onError("Cannot connect to Internet...Please check your connection!");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("name", user.getName());
                    params.put("address", "empty");
                    params.put("mobile", user.getMobile());
                    params.put("mail", user.getEmail());
                    params.put("password", user.getPassword());
                    params.put("birthday", user.getBirthday());
                    params.put("weight", "0");
                    params.put("height", "0");
                    params.put("nationality", "empty");
                    params.put("city", "empty");
                    params.put("position", "empty");
                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }

    private static class FBLoginTask extends AsyncTask<User, String, Void>{

        @Override
        protected Void doInBackground(final User... users) {
            String url = baseUrl + "fbregister.php";

            final User user = users[0];

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")){
                            Log.e("fbregister", response);
                            fbLoginCallback.onResponse(jsonResponse.getString("id"));
                        } else if (jsonResponse.getBoolean("error") && jsonResponse.getString("id") != null){
                            fbLoginCallback.onResponse(jsonResponse.getString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        fbLoginCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        fbLoginCallback.onError(message.toString());
                        return;
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        fbLoginCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        fbLoginCallback.onError(message.toString());
                        return;
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        fbLoginCallback.onError(message.toString());
                        return;
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        fbLoginCallback.onError(message.toString());
                        return;
                    }
                    fbLoginCallback.onError(error.toString());
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("mail", user.getEmail());
                    params.put("birthday", user.getBirthday());
                    params.put("name", user.getName());
                    params.put("img_url", user.getImageUrl());
                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }

    private static class FetchNewsFeedTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            String url = baseUrl + "reze/user_post.php";
            VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_post.php", ApiResponse.class,
                    new Response.Listener<ApiResponse>() {
                        @Override
                        public void onResponse(ApiResponse response) {
                            if (!response.isError()){
                                Log.i("volley response", "onResponse: " + response.getPosts()[0].getCreatedAt());
                                PostResponse [] posts = response.getPosts();
                                ProductResponse[] products = response.getProducts();
                                VendorPostsResponse[] vendorPosts = response.getVendorPosts();
                                EventResponse [] events = response.getEvents();
                                GroupPostResponse[] groupPosts = response.getGroupPosts();
                                ArrayList<NewsFeedItem> newsFeedItems = new ArrayList<>();
                                if (posts != null) {
                                    for (PostResponse postResponse : posts) {
                                        NewsFeedItem item = new NewsFeedItem();
                                        item.setId(postResponse.getPostId());
                                        item.setCreatedAt(postResponse.getCreatedAt());
                                        item.setLikes(postResponse.getLikes());
                                        item.setOwnerId(Integer.parseInt(postResponse.getUserId()));
                                        item.setOwnerName(postResponse.getUsername());
                                        item.setPostAttachment(postResponse.getAttachment());
                                        item.setItemImage(postResponse.getImageUrl());
                                        //item.setPostComments(postResponse.getComments());
                                        item.setCommentSize(postResponse.getCommentSize());
                                        item.setPostText(postResponse.getText());
                                        item.setType(NewsFeedItem.POST_TYPE);
                                        newsFeedItems.add(item);
                                    }
                                }

//                                if (products != null){
//                                    for (ProductResponse productResponse : products) {
//                                        NewsFeedItem item = new NewsFeedItem();
//                                        item.setProductAmount(productResponse.getAmount());
//                                        item.setDescription(productResponse.getDescription());
//                                        item.setProductImageUrl(productResponse.getImageUrl());
//                                        item.setProductPrice(productResponse.getPrice());
//                                        item.setProductSale(productResponse.getSale());
//                                        item.setProductSoldAmount(productResponse.getSoldAmount());
//                                        item.setProductTitle(productResponse.getTitle());
//                                        item.setId(productResponse.getId());
//                                        item.setOwnerId(productResponse.getUserId());
//                                        item.setOwnerName(productResponse.getStoreName());
//                                        item.setItemImage(productResponse.getStoreImageUrl());
//                                        item.setStoreId(productResponse.getStoreId());
//                                        item.setType(NewsFeedItem.PRODUCT_TYPE);
//                                        newsFeedItems.add(item);
//                                    }
//                                }

                                /*if (vendorPosts != null){
                                    for (VendorPostsResponse postResponse : vendorPosts) {
                                        NewsFeedItem item = new NewsFeedItem();
                                        item.setId(postResponse.getPostId());
                                        item.setVendorId(postResponse.getVendorId());
                                        item.setItemName(postResponse.getVendorName());
                                        item.setPostText(postResponse.getText());
                                        item.setCreatedAt(postResponse.getCreatedAt());
                                        item.setLikes(postResponse.getLikes());
                                        item.setPostAttachment(postResponse.getAttachment());
                                        item.setPostComments(postResponse.getComments());
                                        item.setType(NewsFeedItem.VENDOR_POST_TYPE);
                                        newsFeedItems.add(item);
                                    }
                                }*/

//                                if (events != null){
//                                    for (EventResponse eventResponse : events) {
//                                        NewsFeedItem item = new NewsFeedItem();
//                                        item.setId(eventResponse.getId());
//                                        item.setDescription(eventResponse.getDescription());
//                                        item.setOccurDate(eventResponse.getOccurDate());
//                                        item.setItemName(eventResponse.getName());
//                                        item.setCreatedAt(eventResponse.getTimestamp());
//                                        item.setType(NewsFeedItem.EVENT_TYPE);
//                                        newsFeedItems.add(item);
//                                    }
//                                }

                                NewsFeed newsFeed = new NewsFeed();
                                int nextCursor = response.getNextCursor();
                                long now = response.getNow();
                                //Collections.shuffle(newsFeedItems);
                                //updateUi();

                                newsFeed.setItems(newsFeedItems);
                                newsFeed.setNextCursor(nextCursor);
                                newsFeed.setNow(now);

                                feedCallback.onResponse(newsFeed);

                            /*if (groupPosts != null){
                                for (GroupPostResponse groupPostResponse : groupPosts) {
                                    NewsFeedItem item = new NewsFeedItem();
                                    item.setId(groupPostResponse.getId());
                                    item.setItemName(groupPostResponse.getName());
                                    PostResponse[] postResponses = groupPostResponse.getPosts();
                                    for (PostResponse  response1 : postResponses) {

                                    }

                                }
                            }*/



                            } else {
                                feedCallback.onError("لا توجد منشورات");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        feedCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        feedCallback.onError(message.toString());
                        return;
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        feedCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        feedCallback.onError(message.toString());
                        return;
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        feedCallback.onError(message.toString());
                        return;
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        feedCallback.onError(message.toString());
                        return;
                    }
                    feedCallback.onError(error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("method", "get_news_feed");
                    map.put("userId", strings[0]);
                    map.put("cursor", strings[1]);

                    return map;
                }
            };

            requestQueue.add(stringRequest);
            return null;
        }
    }

    private static class FetchProductsTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(final String... users) {
            String url = baseUrl + "user_store.php";

            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, url,
                    io.rezetopia.krito.rezetopiakrito.model.pojo.product.ApiResponse.class, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }

    private static class CreateProductsTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            String url = baseUrl + "user_store.php";

            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, url,
                    ProductResponse.class, new Response.Listener<ProductResponse>() {
                @Override
                public void onResponse(ProductResponse response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    params.put("user_id", strings[0]);
                    params.put("image", strings[1]);
                    params.put("title", strings[2]);
                    params.put("price", strings[3]);
                    params.put("amount", strings[4]);
                    params.put("description", strings[5]);
                    params.put("sale", strings[6]);
                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }

    private static class SearchTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(final String... strings) {
            String url = baseUrl + "reze/user_search.php";

            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, url,
                    SearchResponse.class, new Response.Listener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse response) {
                    ArrayList<SearchItem> searchItems = new ArrayList<>();

                    if (response != null) {
                        if (!response.isError()) {
                            if (response.getUsers() != null && response.getUsers().length > 0) {
                                Log.i("volley_response_search", "onResponse: " + response.getUsers()[0].getUsername());

                                for (int i = 0; i < response.getUsers().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getUsers()[i].getUserId());
                                    item.setName(response.getUsers()[i].getUsername());
                                    item.setImageUrl(response.getUsers()[i].getImageUrl());
                                    item.setType("user");
                                    searchItems.add(item);
                                }

                                SearchResult result = new SearchResult();
                                result.setSearchItems(searchItems);
                                result.setCursor(response.getCursor());

                                searchCallback.onResponse(result);
                            } else {
                                searchCallback.onError("empty result");
                            }
                        } else {
                            searchCallback.onError("empty result");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        searchCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        searchCallback.onError(message.toString());
                        return;
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        searchCallback.onError(message.toString());
                        return;
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        searchCallback.onError(message.toString());
                        return;
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        searchCallback.onError(message.toString());
                        return;
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        searchCallback.onError(message.toString());
                        return;
                    }
                    searchCallback.onError("Cannot connect to Internet...Please check your connection!");
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    params.put("query", strings[0]);
                    params.put("cursor", strings[1]);

                    return params;
                }
            };
            requestQueue.add(post);
            return null;
        }
    }
}
