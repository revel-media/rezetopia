package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import io.rezetopia.krito.rezetopiakrito.fragments.AlertFragment;
import io.rezetopia.krito.rezetopiakrito.model.operations.UserOperations;
import io.rezetopia.krito.rezetopiakrito.views.CustomButton;
import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.views.CustomTextView;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import com.facebook.*;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Login extends AppCompatActivity{
    CustomButton btnSignUp;
    private CustomEditText name,password;
    private CustomButton btnSignIn;
    private RequestQueue requestQueue;
    public static String URL_LOGIN = "http://rezetopia.dev-krito.com/app/login.php";
    private StringRequest request;
    private LoginButton fblogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private ProgressDialog pDialog;
    private Profile profile;
    private Object fbobject;
    PackageInfo info;

    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        fblogin = (LoginButton) findViewById(R.id.login_button);
        fblogin.setReadPermissions("public_profile");
        fblogin.setReadPermissions("email");

        fblogin.setReadPermissions("user_friends");
        fblogin.setReadPermissions("user_birthday");
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        new FacebookLoginTask().execute();

        btnSignUp = findViewById(R.id.btnRegister);
        name = findViewById(R.id.edName);
        password = findViewById(R.id.edPassword);
        btnSignIn =  findViewById(R.id.btnSignIn);
        requestQueue = Volley.newRequestQueue(this);
        CustomTextView tvForgetPassword = findViewById(R.id.forgetPassword);
        tvForgetPassword.setPaintFlags(tvForgetPassword.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
            }
        });
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();

            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String DEBUG_TAG = "NetworkStatusExample";
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isWifiConn = networkInfo.isConnected();
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isMobileConn = networkInfo.isConnected();
                Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
                Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

                if (validate()) {
                    showDialog();
                    UserOperations.login(name.getText().toString(), password.getText().toString());
                    UserOperations.setLoginCallback(new UserOperations.LoginCallback() {
                        @Override
                        public void onResponse(String id) {
                            String url = "https://rezetopiachat.firebaseio.com/users.json";
                            final ProgressDialog pd = new ProgressDialog(Login.this);

                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    if(s.equals("null")){

                                    }
                                    else{
                                        try {
                                            JSONObject obj = new JSONObject(s);

                                            if(!obj.has(name.getText().toString())){

                                            }
                                            else if(obj.getJSONObject(name.getText().toString()).getString("password").equals(password.getText().toString())){


                                            }
                                            else {

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    pd.dismiss();
                                }
                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    System.out.println("" + volleyError);
                                    pd.dismiss();
                                }
                            });

                            RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                            rQueue.add(request);
                            hideDialog();
                            getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
                                    .putString(AppConfig.LOGGED_IN_USER_ID_SHARED, id).apply();
                            SharedPreferences sharedPref = Login.this.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(AppConfig.LOGGED_IN_USER_ID_SHARED,id);
                            editor.commit();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(int error) {
                            hideDialog();
                            String wrong = getResources().getString(error);
                            AlertFragment.createFragment(wrong).show(getFragmentManager(), null);
                        }
                    });
                }
//                String userName = name.getText().toString();
//                String Password = password.getText().toString();
//                loginUser(userName, Password);
            }
        });
    }


//    public void fbRegister(final String name, final String mail, final String birthdate, final String imgUrl){
//        showDialog();
//
//        UserOperations.FBLogin(name, mail, birthdate, imgUrl);
//        UserOperations.setFBLoginCallback(new UserOperations.FBLoginCallback() {
//            @Override
//            public void onResponse(String id) {
//                getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
//                        .putString(AppConfig.LOGGED_IN_USER_ID_SHARED, id).apply();
//
//                hideDialog();
//
//                Intent intent = new Intent(Login.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onError(String error) {
//                String wrong = getResources().getString(R.string.wrong_login);
//                AlertFragment.createFragment(error).show(getFragmentManager(), null);
//            }
//        });
//    }
    public void fbRegister(final String name, final String mail, final String birthdate, final String imgUrl){

        showDialog();
        StringRequest request = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/fbregister.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();
//                try {
//                    JSONObject jsonObject;
//                    jsonObject = new JSONObject(response);
//                    //Toast.makeText(getBaseContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
//                    if(jsonObject.getString("msg").equals("done")){
//                        Toast.makeText(getApplicationContext(),jsonObject.getString("id"),Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//                        intent.putExtra("fbname",name);
//                        intent.putExtra("fbpicurl",imgUrl);
//                        intent.putExtra("id",jsonObject.getString("id"));
//                        startActivityForResult(intent, 0);
//                        finish();
//                    }else if(jsonObject.getString("msg").equals("This mail is already exsist you can log in")){
//                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//                        intent.putExtra("fbname",name);
//                        intent.putExtra("fbpicurl",imgUrl);
//                        intent.putExtra("id",jsonObject.getString("id"));
//                        startActivityForResult(intent, 0);
//                        finish();
//
//                        //Toast.makeText(getBaseContext(),R.string.exsistEmail,Toast.LENGTH_LONG).show();
//                    }
//                    else {
//                        Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();

                parameters.put("name",name);
                parameters.put("mail",mail);
                parameters.put("birthday",birthdate);
                parameters.put("img_url",imgUrl);

                return parameters;
            }
        };
        requestQueue.add(request);
    }


    public void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public boolean validate() {
        boolean valid = true;

        String userName = name.getText().toString();
        String userPassword = password.getText().toString();

        if (userName.isEmpty()) {
            name.setError("enter your mail Address");
            valid = false;
        } else {
            name.setError(null);
        }

        if (userPassword.isEmpty() || password.length() < 4) {
            password.setError("More Than 4 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private class FacebookLoginTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            callbackManager = CallbackManager.Factory.create();
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                }
            };
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                }
            };
            accessTokenTracker.startTracking();
            profileTracker.startTracking();
            fblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code

                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            profile = Profile.getCurrentProfile();
                            //Toast.makeText(getBaseContext(),profile.getName(),Toast.LENGTH_LONG).show();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.v("LoginActivity", response.toString());

                                            // Application code
                                            try {

                                                //String email = object.getString("email");
                                                //String birthday = object.getString("birthday"); // 01/31/1980 format
                                                Log.i("FB_DATA", "onCompleted: " + object.getString("name") + " " + object.getString("email"));
                                                fbRegister(object.getString("name"),object.getString("email"),"null","null");

                                                //Toast.makeText(getBaseContext(),email+" "+birthday,Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender,birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
            return null;
        }
    }

}


