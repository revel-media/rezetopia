package app.reze1.ahmed.reze1.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import app.reze1.ahmed.reze1.fragments.AlertFragment;
import app.reze1.ahmed.reze1.model.operations.UserOperations;
import app.reze1.ahmed.reze1.views.CustomButton;
import app.reze1.ahmed.reze1.views.CustomEditText;
import app.reze1.ahmed.reze1.views.CustomTextView;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.app.AppConfig;
import com.facebook.*;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends AppCompatActivity{
    CustomButton btnSignUp;
    private CustomEditText name,password;
    private CustomButton btnSignIn;
    private RequestQueue requestQueue;
    public static String URL_LOGIN = "https://rezetopia.com/app/login.php";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(app.reze1.ahmed.reze1.R.layout.activity_login);


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
                if (validate()) {
                    showDialog();
                    UserOperations.login(name.getText().toString(), password.getText().toString());
                    UserOperations.setLoginCallback(new UserOperations.LoginCallback() {
                        @Override
                        public void onResponse(String id) {
                            hideDialog();
                            getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
                                    .putString(AppConfig.LOGGED_IN_USER_ID_SHARED, id).apply();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            hideDialog();
                            String wrong = getResources().getString(R.string.wrong_login);
                            AlertFragment.createFragment(wrong).show(getFragmentManager(), null);
                        }
                    });
                }
            }
        });
    }
    public void fbRegister(final String name, final String mail, final String birthdate, final String imgUrl){
        showDialog();

        UserOperations.FBLogin(name, mail, birthdate, imgUrl);
        UserOperations.setFBLoginCallback(new UserOperations.FBLoginCallback() {
            @Override
            public void onResponse(String id) {
                getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
                        .putString(AppConfig.LOGGED_IN_USER_ID_SHARED, id).apply();

                hideDialog();

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                String wrong = getResources().getString(R.string.wrong_login);
                AlertFragment.createFragment(error).show(getFragmentManager(), null);
            }
        });
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

