package io.krito.com.rezetopia.model.operations;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.krito.com.rezetopia.model.pojo.User;

public class UserOperations {

    private static final String baseUrl = "https://rezetopia.com/app/";
    static RegistrationCallback registrationCallback;
    static LoginCallback loginCallback;
    static RequestQueue requestQueue;


    public static void setRegistrationCallback(RegistrationCallback call){
        registrationCallback = call;
    }

    public static void setLoginCallback(LoginCallback call){
        loginCallback = call;
    }

    public static void setRequestQueue(RequestQueue queue){
        requestQueue = queue;
    }

    /**
     * create new user account
     * @param user complete user object
     */
    public static void register(final User user){
        String url = baseUrl + "register.php";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (!jsonResponse.getBoolean("error")) {
                        Log.e("register", response);
                        registrationCallback.onResponse(jsonResponse.getString("id"));
                    } else {
                        registrationCallback.onError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("register", error.toString());
                registrationCallback.onError();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("name", user.getName());
                params.put("address", user.getAddress());
                params.put("mobile", user.getMobile());
                params.put("mail", user.getEmail());
                params.put("password", user.getPassword());
                params.put("birthday", user.getBirthday());
                params.put("weight", user.getWeight());
                params.put("height", user.getHeight());
                params.put("nationality", user.getNationality());
                return params;
            }
        };
        requestQueue.add(post);
    }

    /**
     * check if user exists
     * @param email email of user account
     * @param password password of user account
     */
    public static void login(final String email, final String password){
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
                        loginCallback.onError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginCallback.onError();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("login", "login");
                params.put("mail", email);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(post);
    }

    /**
     * change user account password
     * @param oldPass old account password
     * @param newPass old account password
     */
    public static void changePassword(String oldPass, String newPass){

    }

    public interface RegistrationCallback {
        void onResponse(String id);
        void onError();
    }

    public interface LoginCallback{
        void onResponse(String id);
        void onError();
    }
}
