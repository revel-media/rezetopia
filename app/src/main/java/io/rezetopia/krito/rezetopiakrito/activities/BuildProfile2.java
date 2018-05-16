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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuildProfile2 extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    public int[] layouts;
    private Button btnSkip,btnNext;
    private PrefManager prefManager;
    public RequestQueue requestQueue;
    private String fbname;
    private String fbpicurl;
    private TextView user_namae;
    public static final int PICK_IMAGE = 1;
   public  String user_id;
   public TextView seekval1;
   public TextView seekval2;
   public TextView seekval3;
   public TextView seekval4;
   public TextView seekval5;
   public TextView seekval6;
   public TextView seekval7;
   public TextView seekval8;
   public TextView seekval9;
   public TextView seekval10;
   public TextView seekval11;
   public TextView seekval12;
   public DiscreteSeekBar discreteSeekBar1;
   public DiscreteSeekBar discreteSeekBar2;
   public DiscreteSeekBar discreteSeekBar3;
   public DiscreteSeekBar discreteSeekBar4;
   public DiscreteSeekBar discreteSeekBar5;
   public DiscreteSeekBar discreteSeekBar6;
   public DiscreteSeekBar discreteSeekBar7;
   public DiscreteSeekBar discreteSeekBar8;
   public DiscreteSeekBar discreteSeekBar9;
   public DiscreteSeekBar discreteSeekBar10;
   public DiscreteSeekBar discreteSeekBar11;
   public DiscreteSeekBar discreteSeekBar12;

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
        //Bundle inBundle = getIntent().getExtras();
        //fbname = inBundle.get("fbname").toString();
        //fbpicurl = inBundle.get("fbpicurl").toString();
        Bundle inBundle = getIntent().getExtras();
        user_id = inBundle.getString("id");
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        requestQueue = Volley.newRequestQueue(this);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        //btnSkip = (Button) findViewById(R.eventId.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        progress = new ProgressDialog(BuildProfile2.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        //btnSkip.setVisibility(View.GONE);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.build_profile3,
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
                progress.show();
                Intent intent = new Intent(BuildProfile2.this, MainActivity.class);
                intent.putExtra("type", "user");
                startActivity(intent);
                finish();
                progress.dismiss();
                StringRequest request = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/us.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getBaseContext(),"test",Toast.LENGTH_LONG).show();
                       // Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();

                        //hideDialog();

                        try {
                            JSONObject jsonObject;
                            jsonObject = new JSONObject(response);
                            //Toast.makeText(getBaseContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                            if(jsonObject.getString("msg").equals("done")){
                                Intent intent = new Intent(BuildProfile2.this,BuildNetwork.class);
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

                        parameters.put("attack",discreteSeekBar1.getProgress()*10+"");
                        parameters.put("defense",discreteSeekBar2.getProgress()*10+"");
                        parameters.put("stamina",discreteSeekBar3.getProgress()*10+"");
                        parameters.put("speed",discreteSeekBar4.getProgress()*10+"");
                        parameters.put("ballcontroll",discreteSeekBar5.getProgress()*10+"");
                        parameters.put("lowpass",discreteSeekBar6.getProgress()*10+"");
                        parameters.put("loftedpass",discreteSeekBar7.getProgress()*10+"");
                        parameters.put("shootacc",discreteSeekBar8.getProgress()*10+"");
                        parameters.put("shootpower",discreteSeekBar9.getProgress()*10+"");
                        parameters.put("freekick",discreteSeekBar10.getProgress()*10+"");
                        parameters.put("header",discreteSeekBar11.getProgress()*10+"");
                        parameters.put("jump",discreteSeekBar12.getProgress()*10+"");
                        parameters.put("id",user_id);

                        return parameters;
                    }
                };
                requestQueue.add(request);
                // checking for last page
                // if last page home screen will be launched
//                int current = getItem(+1);
//                //Toast.makeText(getBaseContext(),getItem(0)+"",Toast.LENGTH_LONG).show();
//                if(current == layouts.length-2){
//                    //Toast.makeText(getBaseContext(),"compelete",Toast.LENGTH_LONG).show();
//                }
//                if (current < layouts.length) {
//                    // move to next screen
//                    viewPager.setCurrentItem(current);
//                } else {
//                    launchHomeScreen();
//                }
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
        startActivity(new Intent(BuildProfile2.this, BuildNetwork.class));
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
            btnNext.setText(R.string.start);
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            discreteSeekBar1 = (DiscreteSeekBar) view.findViewById(R.id.discrete1);
            seekval1 = (TextView)view.findViewById(R.id.perval1);
            discreteSeekBar1.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval1.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar1.getProgress();
            discreteSeekBar2 = (DiscreteSeekBar) view.findViewById(R.id.discrete2);
            seekval2 = (TextView)view.findViewById(R.id.perval2);
            discreteSeekBar2.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval2.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar3 = (DiscreteSeekBar) view.findViewById(R.id.discrete3);
            seekval3 = (TextView)view.findViewById(R.id.perval3);
            discreteSeekBar3.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval3.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar4 = (DiscreteSeekBar) view.findViewById(R.id.discrete4);
            seekval4 = (TextView)view.findViewById(R.id.perval4);
            discreteSeekBar4.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval4.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar5 = (DiscreteSeekBar) view.findViewById(R.id.discrete5);
            seekval5 = (TextView)view.findViewById(R.id.perval5);
            discreteSeekBar5.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval5.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar6 = (DiscreteSeekBar) view.findViewById(R.id.discrete6);
            seekval6 = (TextView)view.findViewById(R.id.perval6);
            discreteSeekBar6.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval6.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar7 = (DiscreteSeekBar) view.findViewById(R.id.discrete7);
            seekval7 = (TextView)view.findViewById(R.id.perval7);
            discreteSeekBar7.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval7.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar8 = (DiscreteSeekBar) view.findViewById(R.id.discrete8);
            seekval8 = (TextView)view.findViewById(R.id.perval8);
            discreteSeekBar8.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval8.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar9 = (DiscreteSeekBar) view.findViewById(R.id.discrete9);
            seekval9 = (TextView)view.findViewById(R.id.perval9);
            discreteSeekBar9.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval9.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar10 = (DiscreteSeekBar) view.findViewById(R.id.discrete10);
            seekval10 = (TextView)view.findViewById(R.id.perval10);
            discreteSeekBar10.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval10.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar11 = (DiscreteSeekBar) view.findViewById(R.id.discrete11);
            seekval11 = (TextView)view.findViewById(R.id.perval11);
            discreteSeekBar11.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval11.setText(value*10+" %");
                    return value * 10;
                }
            });
            discreteSeekBar12 = (DiscreteSeekBar) view.findViewById(R.id.discrete12);
            seekval12 = (TextView)view.findViewById(R.id.perval12);
            discreteSeekBar12.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int value) {
                    seekval12.setText(value*10+" %");
                    return value * 10;
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

}

