package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rezetopia.krito.rezetopiakrito.activities.BuildProfile;
import io.rezetopia.krito.rezetopiakrito.activities.NetworkList;
import io.rezetopia.krito.rezetopiakrito.activities.UserImageActivity;
import io.rezetopia.krito.rezetopiakrito.helper.ProfilePagerAdapter;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.isDebugEnabled;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {
    private static final String USER_ID = "user_id";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ViewPager viewPager;
    ViewPager viewPager2;
    TabLayout tabLayout;
    private ProfilePagerAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //private User user;
    private TextView playerNameTv;
    private TextView playerCityTv;
    private TextView playerPositionTv;
    private TextView playerMatchesTv;
    private TextView playerPointsTv;
    private TextView textFriendSize;
    private TextView playerLevelsTv;
    private TextView overview;
    private TextView posts;
    private TextView videos;
    private TextView photos;
    private ImageView settingView;
    private CircleImageView playerImg;
    public RequestQueue requestQueue;
    public static PopupMenu popupMenu;
    public Button btnNetwork, btninvite, btnChallenge;
    public String userId;
    public ScrollView topScroll;
    public LinearLayout wraper;
    private RelativeLayout probar;
    LinearLayout profileHeaderReze;

    ImageView cover;

    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance

        }
        // Toast.makeText(getContext(),"first",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        //Save the fragment's state here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.pager);
        settingView = v.findViewById(R.id.settingView);

        playerImg = v.findViewById(R.id.imageView2);
        cover = v.findViewById(R.id.imageView);
        profileHeaderReze = v.findViewById(R.id.profileHeaderReze);


        View profile_menu = v.findViewById(R.id.profile_menu);
        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        Log.i("profile_user_id", "onCreateView: " + userId);
        profile_menu.setOnClickListener(new optionProfile(getContext()));
        //getUser(userId);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        playerNameTv = (TextView) v.findViewById(R.id.userNameTv);
        playerCityTv = (TextView) v.findViewById(R.id.playerCityTv);
        textFriendSize = v.findViewById(R.id.txt_friends_size);
        playerPositionTv = (TextView) v.findViewById(R.id.playerPositionTv);
        playerMatchesTv = (TextView) v.findViewById(R.id.matchesNumbersTv);
        playerPointsTv = (TextView) v.findViewById(R.id.pointsNumbersTv);
        playerLevelsTv = (TextView) v.findViewById(R.id.levelsNumbersTv);
        btnNetwork = (Button) v.findViewById(R.id.btn_friends);
        btninvite = (Button) v.findViewById(R.id.btninvite);
        btnChallenge = (Button) v.findViewById(R.id.btnchalng);
        topScroll = (ScrollView) v.findViewById(R.id.topScroll);
        wraper = (LinearLayout) v.findViewById(R.id.wraper);

        if (userId.equals("1")) {
            Log.i("here", "ifffffffffff");
            playerImg.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.rezetopia));
            //profileHeaderReze.setVisibility(View.GONE);
            cover.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.cover_1));
        }

        btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NetworkList.class);
//                startActivity(intent);

                AlertFragment fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
            }
        });
        btninvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NetworkList.class);
//                startActivity(intent);
                AlertFragment fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
            }
        });
        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NetworkList.class);
//                startActivity(intent);

                AlertFragment fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
            }
        });


        textFriendSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.putExtra("userID", userId);
                startActivity(intent);
            }
        });


        // Toast.makeText(getContext(),wraper.getHeight()+"",Toast.LENGTH_LONG).show();
        //params.height = 200;
        // wraper.setLayoutParams(params);
        //topScroll.setMinimumHeight(500);
        // topScroll.smoothScrollTo(0,topScroll.getBottom());


//        overview=(TextView)v.findViewById(R.eventId.overview_tab);
//        posts=(TextView)v.findViewById(R.eventId.posts_tab);
//        videos=(TextView)v.findViewById(R.eventId.videos_tab);
//        photos=(TextView)v.findViewById(R.eventId.photos_tab);
//        overview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // do something when the corky2 is clicked
//                viewPager2.setCurrentItem(0);
//
//            }
//        });
//        posts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // do something when the corky2 is clicked
//                viewPager2.setCurrentItem(1);
//            }
//        });
//        videos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // do something when the corky2 is clicked
//                viewPager2.setCurrentItem(2);
//            }
//        });
//        photos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // do something when the corky2 is clicked
//                viewPager2.setCurrentItem(3);
//            }
//        });
        probar = (RelativeLayout) v.findViewById(R.id.loadingPanel);
        getUser(userId, requestQueue);
        getIDs(v);
        setEvents();
        addPage("overview");
        addPage("posts");
        addPage("videos");
        addPage("photos");
        viewPager2.setCurrentItem(0);
        //tabLayout.setTabTextColors(getResources().getColor(R.color.tabs),getResources().getColor(R.color.tabs));
        //Toast.makeText(getContext(),userId,Toast.LENGTH_LONG).show();

        return v;
        // Inflate the layout for this fragment
    }

    private void getIDs(View view) {
        viewPager2 = (ViewPager) view.findViewById(R.id.profile_perview);
        tabLayout = (TabLayout) view.findViewById(R.id.profile_tablayout);
        adapter = new ProfilePagerAdapter(getFragmentManager(), getActivity(), viewPager2, tabLayout);
        viewPager2.setAdapter(adapter);
        viewPager2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager2.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


    }

    int selectedTabPosition;

    private void setEvents() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager2) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager2.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager2.getCurrentItem();
                Log.d("Selected", "Selected " + tab.getPosition());

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }


    public void addPage(String pagename) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        OverviewProfile inner = new OverviewProfile();
        inner.setArguments(bundle);
        adapter.addFrag(inner, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager2);

        viewPager2.setCurrentItem(adapter.getCount() - 1);
        // setupTabLayout();
    }

//    public void setupTabLayout() {
//        selectedTabPosition = viewPager2.getCurrentItem();
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
//        }
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getUser(final String id, RequestQueue requestQueue) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/getInfo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                try {
                    final JSONObject jsonObject;
                    jsonObject = new JSONObject(response);
                    //Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_LONG).show();
                    if (jsonObject.getString("msg").equals("succ")) {

                        playerCityTv.setText(jsonObject.getString("city"));
                        playerPositionTv.setText(jsonObject.getString("position"));
                        playerNameTv.setText(jsonObject.getString("name"));
                        playerMatchesTv.setText("0");
                        playerLevelsTv.setText("0");
                        playerPointsTv.setText("0");
                        if (!userId.contentEquals("1")) {
                            Picasso.with(getApplicationContext())
                                    .load("https://rezetopia.dev-krito.com/images/profileImgs/" + jsonObject.getString("img") + ".JPG")
                                    .placeholder(R.drawable.circle).into(playerImg);
                        }
                        settingView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), BuildProfile.class);
                                try {
                                    intent.putExtra("fbname", jsonObject.getString("name"));
                                    intent.putExtra("fbpicurl", "https://rezetopia.dev-krito.com/images/profileImgs/" + jsonObject.getString("img") + ".JPG");
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        playerImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = null;
                                try {
                                    if (jsonObject.getString("img") != null && !jsonObject.getString("img").contentEquals("")) {
                                        url = "https://rezetopia.dev-krito.com/images/profileImgs/" + jsonObject.getString("img") + ".JPG";
                                        Intent intent = UserImageActivity.createIntent(url, getActivity());
                                        startActivityForResult(intent, 2002);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        probar.setVisibility(View.GONE);
                        // new DownloadImage(playerImg).execute("https://rezetopia.dev-krito.com/images/profileImgs/"+jsonObject.getString("img")+".JPG");
                    } else {

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
                Map<String, String> parameters = new HashMap<String, String>();

                parameters.put("id", id);
                parameters.put("getInfo", "");


                return parameters;
            }
        };
        requestQueue.add(request);

    }

    class optionProfile implements View.OnClickListener {
        private Profile profile;
        private Context mContext;

        public optionProfile(Context context) {
            mContext = context;
            profile = profile;
        }

        @Override
        public void onClick(View v) {
            // This is an android.support.v7.widget.PopupMenu;
            popupMenu = new PopupMenu(mContext, v) {
            };

            popupMenu.inflate(R.menu.profile_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_network:
                            // Toast.makeText(getApplicationContext(),menuItem.getTitle(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), NetworkList.class);
                            startActivity(intent);
                            return true;
                        default:
                            return false;
                        //return super.onMenuItemSelected(item);
                    }
                    //return false;
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2002) {
            getUser(userId, requestQueue);
        }
    }
}




