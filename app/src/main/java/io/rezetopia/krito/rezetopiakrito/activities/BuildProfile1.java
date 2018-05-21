package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.helper.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BuildProfile1 extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    public int[] layouts;
    private Button btnSkip,btnNext;
    private PrefManager prefManager;
    private String fbname;
    private String fbpicurl;
    private TextView user_namae;
    public static final int PICK_IMAGE = 1;
    public  EditText national;
    public ArrayList<String> Nagitems=new ArrayList<>();
    public Spinner spinnerPosition;
    public Spinner spinnerNag;

    public Spinner spinnerFoot;
    public Button hplus;
    public Button hminus;
    public RequestQueue requestQueue;
    public EditText heightPlayer;
    int maxHeight = 200;
    int minHeight = 100;
    int inputHeight = 0;
    public Button wplus;
    public Button wminus;
    public EditText weightPlayer;
    int maxWeight = 80;
    int minWeight = 20;
    int inputWeight = 0;
    public String user_id;
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            //launchHomeScreen();
            //finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_buildprofile);
        Bundle inBundle = getIntent().getExtras();
        user_id = inBundle.get("id").toString();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        requestQueue = Volley.newRequestQueue(this);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnNext = (Button) findViewById(R.id.btn_next);
        progress = new ProgressDialog(BuildProfile1.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        layouts = new int[]{
                R.layout.build_profile2,
                /*R.layout.build_profile2,
                R.layout.build_profile3*/};

        // adding bottom dots
        //addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //launchHomeScreen();
//            }
//        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){

                }else{
                    progress.show();
//                    startActivity(new Intent(BuildProfile1.this,BuildProfile2.class));
//                    finish();
                    StringRequest request = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/bp1.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(getBaseContext(),"test",Toast.LENGTH_LONG).show();
                            //Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();

                            //hideDialog();
                            progress.dismiss();
                            try {
                                JSONObject jsonObject;
                                jsonObject = new JSONObject(response);
                                //Toast.makeText(getBaseContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                                if(jsonObject.getString("msg").equals("done")){
                                    Intent intent = new Intent(BuildProfile1.this,BuildProfile2.class);
                                    intent.putExtra("id",user_id);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                  //  Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> parameters  = new HashMap<String, String>();

                            parameters.put("height",heightPlayer.getText().toString());
                            parameters.put("weight",weightPlayer.getText().toString());
                            parameters.put("nationality",spinnerNag.getSelectedItem().toString());
                            parameters.put("position",spinnerPosition.getSelectedItem().toString());
                            parameters.put("df",spinnerFoot.getSelectedItem().toString());
                            parameters.put("id",user_id);

                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                }

            }
        });


    }

//    private void addBottomDots(int currentPage) {
//        dots = new TextView[layouts.length];
//
//        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
//        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
//
//        dotsLayout.removeAllViews();
//        for (int i = 0; i < dots.length; i++) {
//            dots[i] = new TextView(this);
//            dots[i].setText(Html.fromHtml("&#8226;"));
//            dots[i].setTextSize(35);
//            dots[i].setTextColor(colorsInactive[currentPage]);
//            dotsLayout.addView(dots[i]);
//        }
//
//        if (dots.length > 0)
//            dots[currentPage].setTextColor(colorsActive[currentPage]);
//    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(BuildProfile1.this, MainActivity.class));
        finish();
    }

    ViewPager.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };



    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {

            //Toast.makeText(getBaseContext(),viewPager.getCurrentItem()+"",Toast.LENGTH_LONG).show();
            //viewPager.getAdapter().notifyDataSetChanged();
            //addBottomDots(position);
            //btnNext.setVisibility(View.GONE);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                //btnSkip.setVisibility(View.GONE);
            }
//            if(position == 0){
//                user_namae = (TextView)findViewById(R.eventId.user_build_name);
//                user_namae.setText(fbname);
//                if (fbpicurl != "null"){
//                    new DownloadImage((ImageView)findViewById(R.eventId.profile_upload_image)).execute(fbpicurl);
//                }
//                else{
//                    ((ImageView)findViewById(R.eventId.profile_upload_image)).setImageResource(R.drawable.default_avatar);
//                }
//                Spinner spinnerCity = (Spinner) findViewById(R.eventId.spinner_city);
//// Create an ArrayAdapter using the string array and a default spinner layout
//                ArrayAdapter<CharSequence> adapterSpinnerCity = ArrayAdapter.createFromResource(getApplicationContext(),
//                        R.array.spinner_city, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//                adapterSpinnerCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//                spinnerCity.setAdapter(adapterSpinnerCity);
//
//
//                Spinner spinnerCarrer = (Spinner) findViewById(R.eventId.spinner_carrer);
//// Create an ArrayAdapter using the string array and a default spinner layout
//                ArrayAdapter<CharSequence> adapterSpinnerCarrer = ArrayAdapter.createFromResource(getApplicationContext(),
//                        R.array.spinner_carrer, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//                adapterSpinnerCarrer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//                spinnerCarrer.setAdapter(adapterSpinnerCarrer);
//
//            }
//            else {
//                // still pages are left
//                //btnNext.setText(getString(R.string.next));
//                //btnSkip.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //Toast.makeText(getBaseContext(),"5050",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //Toast.makeText(getBaseContext(),"12",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imgSelectedUri = data.getData();
            ((ImageView)findViewById(R.id.profile_upload_image)).setImageURI(imgSelectedUri);
        }
    }
    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends android.support.v4.view.PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            spinnerPosition = (Spinner) view.findViewById(R.id.spinner_playerPosition);
            spinnerFoot = (Spinner) view.findViewById(R.id.spinner_foot);
            spinnerNag = (Spinner) view.findViewById(R.id.spinner_nag);
            heightPlayer = (EditText) view.findViewById(R.id.height_player);
            hplus = (Button)view.findViewById(R.id.hplus);
            hminus = (Button)view.findViewById(R.id.hminus);
            weightPlayer = (EditText) view.findViewById(R.id.weight_player);
            wplus = (Button)view.findViewById(R.id.wplus);
            wminus = (Button)view.findViewById(R.id.wminus);
            hplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputHeight = Integer.parseInt(heightPlayer.getText().toString());
                    inputHeight++;
                    if(inputHeight > maxHeight){
                        heightPlayer.setText("200");
                    }else{
                     heightPlayer.setText(inputHeight+"");
                    }
                }
            });
            hminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputHeight = Integer.parseInt(heightPlayer.getText().toString());
                    inputHeight--;
                    if(inputHeight < minHeight){
                        heightPlayer.setText("100");
                    }else{
                        heightPlayer.setText(inputHeight+"");
                    }
                }
            });
            wplus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputWeight = Integer.parseInt(weightPlayer.getText().toString());
                    inputWeight++;
                    if(inputWeight > maxWeight){
                        weightPlayer.setText("80");
                    }else{
                        weightPlayer.setText(inputWeight+"");
                    }
                }
            });
            wminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputWeight = Integer.parseInt(weightPlayer.getText().toString());
                    inputWeight--;
                    if(inputWeight < minWeight){
                        heightPlayer.setText("20");
                    }else{
                        weightPlayer.setText(inputWeight+"");
                    }
                }
            });
            heightPlayer.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    inputHeight = Integer.parseInt(s.toString());
                    if (inputHeight > 100){
                        hminus.setTextColor(getResources().getColor(R.color.btn_on));
                    }
                    else{
                        hminus.setTextColor(getResources().getColor(R.color.btn_off));
                    }
                    if (inputHeight < 200){
                        hplus.setTextColor(getResources().getColor(R.color.btn_on));
                    }
                    else{
                        hplus.setTextColor(getResources().getColor(R.color.btn_off));
                    }
                    if(inputHeight < minHeight )
                    {
                        heightPlayer.setText("100");
                        //hplus.setTextColor(getResources().getColor(R.color.btn_on));
                        //hminus.setTextColor(getResources().getColor(R.color.btn_off));
                    }
                    if(inputHeight > maxHeight){
                        heightPlayer.setText("200");
                        //hplus.setTextColor(getResources().getColor(R.color.btn_off));
                        //hminus.setTextColor(getResources().getColor(R.color.btn_on));
                    }

                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {


                }

                public void afterTextChanged(Editable s) {


                }
            });
            weightPlayer.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    inputWeight = Integer.parseInt(s.toString());
                    if (inputWeight > 20){
                        wminus.setTextColor(getResources().getColor(R.color.btn_on));
                    }
                    else{
                        wminus.setTextColor(getResources().getColor(R.color.btn_off));
                    }
                    if (inputWeight < 80){
                        wplus.setTextColor(getResources().getColor(R.color.btn_on));
                    }
                    else{
                        wplus.setTextColor(getResources().getColor(R.color.btn_off));
                    }

                    if(inputWeight < minWeight )
                    {
                        weightPlayer.setText("20");
                        //wplus.setTextColor(getResources().getColor(R.color.btn_on));
                        //wminus.setTextColor(getResources().getColor(R.color.btn_off));
                    }
                    if(inputWeight > maxWeight){
                        weightPlayer.setText("80");
                        //wplus.setTextColor(getResources().getColor(R.color.btn_off));
                        //wminus.setTextColor(getResources().getColor(R.color.btn_on));
                    }

                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {


                }

                public void afterTextChanged(Editable s) {

                }
            });
            ArrayAdapter<CharSequence> adapterSpinnerPosition = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.spinner_playerPosition, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> adapterSpinnerFoot = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.spinner_foot, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> adapterSpinnerNag = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.countries_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapterSpinnerPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterSpinnerFoot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterSpinnerNag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPosition.setAdapter(adapterSpinnerPosition);
            spinnerFoot.setAdapter(adapterSpinnerFoot);
            spinnerNag.setAdapter(adapterSpinnerNag);
            spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   // Toast.makeText(getBaseContext(),spinnerPosition.getSelectedItemPosition()+"",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerFoot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   // Toast.makeText(getBaseContext(),spinnerFoot.getSelectedItemPosition()+"",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerNag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   // Toast.makeText(getBaseContext(),spinnerNag.getSelectedItemPosition()+"",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    public boolean validate() {
        boolean valid = true;
        if (spinnerNag.getSelectedItemPosition() == 0) {
          //  Toast.makeText(getBaseContext(),R.string.validate_nag,Toast.LENGTH_LONG).show();
            TextView errorText = (TextView)spinnerNag.getSelectedView();
            errorText.setError(getResources().getString(R.string.validate_nag));
            valid = false;
        } else {
            TextView errorText = (TextView)spinnerNag.getSelectedView();
            errorText.setError(null);
        }
        if (spinnerFoot.getSelectedItemPosition() == 0) {
         //   Toast.makeText(getBaseContext(),R.string.validate_foot,Toast.LENGTH_LONG).show();
            TextView errorText = (TextView)spinnerFoot.getSelectedView();
            errorText.setError(getResources().getString(R.string.validate_foot));
            valid = false;
        } else {
            TextView errorText = (TextView)spinnerFoot.getSelectedView();
            errorText.setError(null);
        }
        if (spinnerPosition.getSelectedItemPosition() == 0) {
         //   Toast.makeText(getBaseContext(),R.string.validate_position,Toast.LENGTH_LONG).show();
            TextView errorText = (TextView)spinnerPosition.getSelectedView();
            errorText.setError(getResources().getString(R.string.validate_position));
            valid = false;
        } else {
            TextView errorText = (TextView)spinnerFoot.getSelectedView();
            errorText.setError(null);
        }

        return valid;
    }

}

