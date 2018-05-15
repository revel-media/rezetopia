package app.reze1.ahmed.reze1.model.operations;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import app.reze1.ahmed.reze1.model.pojo.news_feed.EventResponse;
import app.reze1.ahmed.reze1.model.pojo.news_feed.GroupPostResponse;
import app.reze1.ahmed.reze1.model.pojo.news_feed.NewsFeed;
import app.reze1.ahmed.reze1.model.pojo.news_feed.NewsFeedItem;
import app.reze1.ahmed.reze1.model.pojo.news_feed.VendorPostsResponse;
import app.reze1.ahmed.reze1.model.pojo.post.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.model.pojo.product.ProductResponse;
import app.reze1.ahmed.reze1.model.pojo.user.User;

public class UserOperations {

    private static final String baseUrl = "https://rezetopia.dev-krito.com/app/";
    static RegistrationCallback registrationCallback;
    static LoginCallback loginCallback;
    static FBLoginCallback fbLoginCallback;
    static NewsFeedCallback feedCallback;
    static GetProductsCallback productsCallback;
    static CreateProductsCallback createProductsCallback;
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

    public static void fetchProducts(){
        new FetchProductsTask().execute();
    }

    public interface RegistrationCallback {
        void onResponse(String id);
        void onError(String error);
    }

    public interface LoginCallback{
        void onResponse(String id);
        void onError(String error);
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
                            Log.e("login", response);
                            loginCallback.onResponse(jsonResponse.getString("id"));
                        } else {
                            loginCallback.onError(jsonResponse.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loginCallback.onError(error.toString());
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
                    Log.e("register", error.toString());
                    registrationCallback.onError(error.toString());
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
                                feedCallback.onError("");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
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
                    app.reze1.ahmed.reze1.model.pojo.product.ApiResponse.class, new Response.Listener<String>() {
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
}
