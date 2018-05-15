package app.reze1.ahmed.reze1.activities;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.reze1.ahmed.reze1.fragments.AlertFragment;
import app.reze1.ahmed.reze1.model.operations.UserOperations;
import app.reze1.ahmed.reze1.model.pojo.user.User;
import app.reze1.ahmed.reze1.views.CustomButton;
import app.reze1.ahmed.reze1.views.CustomEditText;
import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.DateDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AppCompatActivity {
    private static final String TAG = Registration.class.getSimpleName();

    private CustomButton btnRegister;
    private CustomEditText inputFullName;
    private CustomEditText inputMobile;
    private CustomEditText inputEmail;
    private CustomEditText inputDateOfBirth;
    private CustomEditText inputPassword;
    private CustomEditText inputRepassword;
    private CustomEditText birthdate;
    private ProgressDialog pDialog;
    private  CustomButton btnLogin;
    private CheckBox checkBox;
    private Calendar myCalendar;


    RequestQueue requestQueue;
    public static String URL_REGISTER = "https://rezetopia.dev-krito.com/app/register.php";
    public ProgressDialog progress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //Spinner spinner = (Spinner) findViewById(R.eventId.spinner);
        mAuth = FirebaseAuth.getInstance();

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.object_array, R.layout.spinner_item);

        //adapter.setDropDownViewResource(R.layout.spinner_item);
//        myCalendar = Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
//            }
//
//        };
        //spinner.setAdapter(adapter);
        inputFullName = (CustomEditText) findViewById(R.id.edName);
        inputMobile=(CustomEditText)findViewById(R.id.edPhone);
        inputEmail = (CustomEditText) findViewById(R.id.edEmail);
        inputDateOfBirth=(CustomEditText)findViewById(R.id.edDate);
        inputPassword = (CustomEditText) findViewById(R.id.edPassword);
        inputRepassword=(CustomEditText)findViewById(R.id.edConfirmPassword);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        btnRegister = (CustomButton) findViewById(R.id.btnRegister);
        btnLogin = (CustomButton) findViewById(R.id.btnLogin);
        birthdate = (CustomEditText)findViewById(R.id.edDate);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        progress = new ProgressDialog(Registration.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent, 0);
                //finish();
            }
        });
        // Session manager

        // SQLite database handler

        // Check if user is already logged in or not
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                //validate();
                if (validate()) {
                    showDialog();
                    User user = new User();
                    user.setName(inputFullName.getText().toString());
                    user.setBirthday(inputDateOfBirth.getText().toString());
                    user.setMobile(inputMobile.getText().toString());
                    user.setEmail(inputEmail.getText().toString());
                    user.setPassword(inputPassword.getText().toString());
                    progress.show();
                    UserOperations.register(user);

                    UserOperations.setRegistrationCallback(new UserOperations.RegistrationCallback() {
                        @Override
                        public void onResponse(String id) {
                            hideDialog();
                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                            intent.putExtra("fbname", inputFullName.getText().toString());
                            intent.putExtra("fbpicurl", "null");
                            intent.putExtra("id", id);

                            String display_name = inputFullName.getText().toString();
                            String email = inputEmail.getText().toString();
                            String password = inputPassword.getText().toString();


                            register_user(display_name, email, password, id);

                            startActivityForResult(intent, 0);
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            hideDialog();
                            AlertFragment.createFragment(error).show(getFragmentManager(), null);
                            //Toast.makeText(Registration.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }
//    private void updateLabel() {
//        String myFormat = "MM/dd/yy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        birthdate.setText(sdf.format(myCalendar.getTime()));
//    }

    public void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public boolean validate() {
        boolean valid = true;

        String name = inputFullName.getText().toString();
        String mobile = inputMobile.getText().toString();
        String email = inputEmail.getText().toString();
        String date_of_birth = inputDateOfBirth.getText().toString();
        String password =inputPassword.getText().toString();
        String reEnterPassword =inputRepassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            inputFullName.setError("at least 3 characters");
            valid = false;
        } else {
            inputFullName.setError(null);
        }

        if (date_of_birth.isEmpty()) {
            inputDateOfBirth.setError("Enter your birthday date");
            valid = false;
        } else {
            inputDateOfBirth.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=11) {
            inputMobile.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            inputMobile.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            inputRepassword.setError("Password Do not match");
            valid = false;
        } else {
            inputRepassword.setError(null);
        }
        if(!checkBox.isChecked()){
            checkBox.setError("Confirm Terms Please");
            valid = false;
        } else{
            checkBox.setError(null);
        }

        return valid;
    }


    public void onStart(){
        super.onStart();

        inputDateOfBirth=(CustomEditText)findViewById(R.id.edDate);
        inputDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog=new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
    }

    private void register_user(final String display_name, String email, String password, final String id) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(id));

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();


                    userMap.put("name", display_name);
                    userMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    userMap.put("device_token", device_token);


                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                pDialog.dismiss();

                                Intent mainIntent = new Intent(Registration.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });


                } else {

                    pDialog.hide();
                    Toast.makeText(Registration.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
