package app.reze1.ahmed.reze1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.views.CustomButton;
import app.reze1.ahmed.reze1.views.CustomEditText;
import app.reze1.ahmed.reze1.R;

import com.facebook.*;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity {

    CustomButton btnSignUp;
    private CustomEditText edreset;
    private CustomButton ok;
    private RequestQueue requestQueue;
    public static String URL_RESET = "https://rezetopia.com/app/resetpass.php";
    private StringRequest request;
    private LoginButton fblogin;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(app.reze1.ahmed.reze1.R.layout.activity_forget_password);
        edreset = (CustomEditText) findViewById(R.id.edreset);

        ok = (CustomButton) findViewById(R.id.ok);
        requestQueue = Volley.newRequestQueue(this);




        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean phone = validatePhoneNumber(edreset.getText().toString());
                if (!phone){
                    URL_RESET = "https://rezetopia.com/app/resetpass.php";
                }
                else if(phone){
                    URL_RESET = "https://rezetopia.com/app/resetsms.php";
                }
                validate();
               // Toast.makeText(getApplicationContext(),"clicked"+URL_RESET,Toast.LENGTH_SHORT).show();

                request = new StringRequest(Request.Method.POST, URL_RESET, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject;
                            jsonObject = new JSONObject(response);
                      //      Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();
                            if(jsonObject.getString("msg").equals("sent")){
                       //         Toast.makeText(getApplicationContext(),jsonObject.getString("sent"),Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//                                startActivityForResult(intent, 0);
//                                finish();
                            }
                            else if(jsonObject.getString("msg").equals("no")){
                         //       Toast.makeText(getBaseContext(),R.string.wrongreset,Toast.LENGTH_LONG).show();
                            }
                            else {
                                //         Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();                            }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("dist_send",edreset.getText().toString());
                        hashMap.put("reset","pass");

                        return hashMap;
                    }
                };

                requestQueue.add(request);
            }
        });
    }
    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{11}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{4}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public boolean validate() {
        boolean valid = true;

        String field = edreset.getText().toString();

        if (field.isEmpty()) {
            edreset.setError("can't be empty");
            valid = false;
        } else {
            edreset.setError(null);
        }

        return valid;
    }
}
