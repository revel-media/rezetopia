package app.reze1.ahmed.reze1.services;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.reze1.ahmed.reze1.app.AppConfig;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 4/19/2018.
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
                .putString(FirebaseInstanceId.getInstance().getToken(), AppConfig.DEVICE_TOKEN_SHARED)
                .apply();
        //Toast.makeText(this, FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
        Log.i("fcmtoken",  FirebaseInstanceId.getInstance().getToken());
        updateToken();
    }

    private void updateToken(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/push_notification.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (!error){
                                Log.i("updateToken", "updateToken: " + jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("updateTokenError", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                String token = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.DEVICE_TOKEN_SHARED, "null");

                String userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "null");

                map.put("method", "update_token");
                map.put("token", token);
                map.put("userId", userId);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
