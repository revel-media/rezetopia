package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.rezetopia.krito.rezetopiakrito.fragments.Home;
import io.rezetopia.krito.rezetopiakrito.fragments.Notification;
import io.rezetopia.krito.rezetopiakrito.helper.MainPagerAdapter;
import io.rezetopia.krito.rezetopiakrito.fragments.Profile;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchItem;
import io.rezetopia.krito.rezetopiakrito.model.pojo.search.SearchResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Home.OnCallback,Notification.OnFragmentInteractionListener,Profile.OnFragmentInteractionListener {
    private static final int VIEW_HEADER = 1;
    private static final int VIEW_ITEM = 2;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    EditText searchBox;
    ImageView backView;
    Bundle savedBundle;
    ArrayList<SearchItem> searchItems;
    View mCustomView;
    ImageButton searchIcon;
    boolean searchUsers = false;
    boolean searchGroups = false;
    boolean searchEvent = false;
    boolean searchVendor = false;
    boolean searchTeam = false;
    public View reqView;
    public RequestQueue requestQueue;
    public static ArrayList<JSONObject> values = new ArrayList<>();
    String q;
    int searchBoxWidth = 300;
    int currentTab = 0;
    ImageView chatButton;
    String userType;
    String userId;
    public int requestsNumber;
    Firebase reference1;
    Firebase reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit()
                .putInt(AppConfig.REQUEST_NOTIFICATIONS_NUMBER, 0).apply();
        //mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userId = getBaseContext().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        new getTask().execute();

        FrameLayout fab = (FrameLayout) findViewById(R.id.fab);
        Firebase.setAndroidContext(this);
//        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//
//
//        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
//
//                if (snapshot.hasChild("friends_pending_"+userId)) {
//                    Toast.makeText(getBaseContext(),"1",Toast.LENGTH_LONG).show();
//                    Log.d("check_child","exsist child");
//                }
//                else{
//                    Log.d("check_child","not exsist child");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        reference1 = new Firebase("https://rezetopiachat.firebaseio.com/noteall");
        reference2 = new Firebase("https://rezetopiachat.firebaseio.com/requests/friends_pending_"+userId);
        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                new getTask().execute();
                Toast.makeText(MainActivity.this, "add", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                new getTask().execute();
                Toast.makeText(MainActivity.this, "remove", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NetworkList.class);
                startActivity(intent);
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                        getApplicationContext()).setAutoCancel(true)
//                        .setContentTitle("رزيتوبيا")
//                        .setSmallIcon(R.drawable.rezetopia)
//                        .setContentText("رزيتوبيا");
//                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//                bigText.bigText(dataSnapshot.getValue().toString());
//                bigText.setBigContentTitle("رزيتوبيا");
//                bigText.setSummaryText("المدير التنفيذي");
//                mBuilder.setStyle(bigText);
//                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//                Intent resultIntent = new Intent(getApplicationContext(),
//                        MainActivity.class);
//                TaskStackBuilder stackBuilder = TaskStackBuilder
//                        .create(getApplicationContext());
//                stackBuilder.addParentStack(MainActivity.class);
//                stackBuilder.addNextIntent(resultIntent);
//                PendingIntent resultPendingIntent = stackBuilder
//                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                mBuilder.setContentIntent(resultPendingIntent);
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify(100, mBuilder.build());
                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(MainActivity.this);
                //popupBuilder.setIcon(R.drawable.rezetopia);
                popupBuilder.setTitle("RezeTopia");
                TextView myMsg = new TextView(MainActivity.this);
                myMsg.setText(dataSnapshot.getValue().toString());
                myMsg.setPadding(10,10,10,10);
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                popupBuilder.setPositiveButton("ok",null);
                popupBuilder.setCancelable(false);
                popupBuilder.setView(myMsg);
                popupBuilder.show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                        getApplicationContext()).setAutoCancel(true)
//                        .setContentTitle("رزيتوبيا")
//                        .setSmallIcon(R.drawable.rezetopia)
//                        .setContentText("رزيتوبيا");
//                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//                bigText.bigText(dataSnapshot.getValue().toString());
//                bigText.setBigContentTitle("رزيتوبيا");
//                bigText.setSummaryText("المدير التنفيذي");
//                mBuilder.setStyle(bigText);
//                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//                Intent resultIntent = new Intent(getApplicationContext(),
//                        MainActivity.class);
//                TaskStackBuilder stackBuilder = TaskStackBuilder
//                        .create(getApplicationContext());
//                stackBuilder.addParentStack(MainActivity.class);
//                stackBuilder.addNextIntent(resultIntent);
//                PendingIntent resultPendingIntent = stackBuilder
//                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                mBuilder.setContentIntent(resultPendingIntent);
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify(100, mBuilder.build());
                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(MainActivity.this);
                //popupBuilder.setIcon(R.drawable.rezetopia);
                popupBuilder.setTitle("RezeTopia");
                TextView myMsg = new TextView(MainActivity.this);
                myMsg.setText(dataSnapshot.getValue().toString());
                myMsg.setPadding(10,10,10,10);
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                popupBuilder.setPositiveButton("ok",null);
                popupBuilder.setCancelable(false);
                popupBuilder.setView(myMsg);
                popupBuilder.show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
      //  Toast.makeText(getBaseContext(),SocketConnect.socket+"",Toast.LENGTH_LONG).show();
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

//        if (mAuth.getCurrentUser() != null) {
//            User user = new User();
//
//            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(user.getId()));
//
//        }
        savedBundle = savedInstanceState;
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(AppConfig.LOGGED_IN_FIRE_ID_SHARED,"fireAuthed").apply();
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        reqView = LayoutInflater.from(this).inflate(R.layout.request_tab_icon, null);
        TextView textView = reqView.findViewById(R.id.req_count);
//        if (requestsNumber != 0){
//            textView.setText(String.valueOf(requestsNumber));
//            textView.setVisibility(View.VISIBLE);
//        }
        mCustomView = mInflater.inflate(R.layout.action_bar, null);
        searchBox = mCustomView.findViewById(R.id.searchbox);
//        final Intent emptyIntent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, NOT_USED, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,SearchActivity.class), 1002);
            }
        });

        searchBox.setFocusable(false);
        searchIcon = mCustomView.findViewById(R.id.searchIcon);
        chatButton = mCustomView.findViewById(R.id.imageButton);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mActionBar.setCustomView(mCustomView,layout);
        mActionBar.setDisplayShowCustomEnabled(true);
        backView = mCustomView.findViewById(R.id.searchBackView);
        userType = "user";

        inflateMainView(currentTab);




        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //  Intent chatIntent=new Intent(MainActivity.this,FriendsActivity.class);
           //  startActivity(chatIntent);
           //  finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Toast.makeText(this, "resume main ac", Toast.LENGTH_SHORT).show();
        FrameLayout fab = (FrameLayout) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NetworkList.class);
                startActivity(intent);
            }
        });
    }

    public void active(View view){
//        ResizeWidthAnimation anim = new ResizeWidthAnimation(view, 400);
//        anim.setDuration(20);
//        view.startAnimation(anim);
//        setContentView(R.layout.search_view);
//        view.setFocusable(true);
//        backView.setVisibility(View.VISIBLE);
//        inflateSearchView();
    }

    public void backClick(View view){
        ViewGroup.LayoutParams lp =  searchBox.getLayoutParams();
        lp.width = searchBoxWidth;
        searchBox.setLayoutParams(lp);
        setContentView(R.layout.activity_main);
        view.setVisibility(View.GONE);
        searchBox.setText("");
        searchBox.setFocusable(false);
        inflateMainView(currentTab);
    }


    @Override
    public void onProfile() {
        inflateMainView(3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1002){
            inflateMainView(3);
        }
    }

    private void inflateMainView(int current){
        if (current > 0){
            setContentView(R.layout.activity_main);
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notification_tab));
        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_store));
        tabLayout.addTab(tabLayout.newTab().setCustomView(reqView));
        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_requests_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_profile_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_side_menu));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark),PorterDuff.Mode.SRC_IN);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(), userType);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark);
               // tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                if (tab.getPosition() == 2){
                    ImageView imageView = tab.getCustomView().findViewById(R.id.icon_in);
                    TextView textView = tab.getCustomView().findViewById(R.id.req_count);
                    imageView.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    textView.setVisibility(View.GONE);
                    //textView.setText("0");
                    tab.setIcon(R.drawable.ic_requests_tab);
                }else{
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }
                viewPager.setCurrentItem(tab.getPosition());
                currentTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.tabs);
                if (tab.getPosition() == 2){
                    ImageView imageView = tab.getCustomView().findViewById(R.id.icon_in);
                    TextView textView = tab.getCustomView().findViewById(R.id.req_count);
                    imageView.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    tab.setIcon(R.drawable.ic_requests_tab);
                }else{
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab tab = tabLayout.getTabAt(current);
        tab.select();
        viewPager.setCurrentItem(current);
        currentTab = current;
    }

//    private void inflateSearchView(){
//        searchBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s != null && s.length() > 0 && !s.toString().contentEquals("0")){
//                    q = s.toString();
//                    performSearch(q);
//                } else {
//                    searchGroups = false;
//                    searchUsers = false;
//                    searchEvent = false;
//                    searchVendor = false;
//                    searchTeam = false;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        searchIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (q != null){
//                    performSearch(q);
//                }
//            }
//        });
//    }

    private void updateSearchView(ArrayList<SearchItem> items){
        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        RecyclerView.Adapter recyclerAdapter = new SearchRecyclerAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        Button userSearchFilter;
        Button groupSearchFilter;
        Button vendorSearchFilter;
        Button teamSearchFilter;
        Button eventSearchFilter;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            userSearchFilter = itemView.findViewById(R.id.userSearchFilter);
            groupSearchFilter = itemView.findViewById(R.id.groupSearchFilter);
            vendorSearchFilter = itemView.findViewById(R.id.vendorSearchFilter);
            teamSearchFilter = itemView.findViewById(R.id.teamSearchFilter);
            eventSearchFilter = itemView.findViewById(R.id.eventSearchFilter);

            /*eventSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchVendor = false;
                        searchGroups = false;
                        searchUsers = false;
                        searchEvent = true;
                        searchTeam = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("event")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchView(searchItems);
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });*/

            /*teamSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchVendor = false;
                        searchGroups = false;
                        searchUsers = false;
                        searchEvent = false;
                        searchTeam = true;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("team")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchView(searchItems);
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });*/

            /*vendorSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchVendor = true;
                        searchGroups = false;
                        searchUsers = false;
                        searchEvent = false;
                        searchTeam = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("vendor")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchView(searchItems);
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });*/

            userSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchVendor = false;
                        searchGroups = false;
                        searchUsers = true;
                        searchEvent = false;
                        searchTeam = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("user")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchView(searchItems);
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            groupSearchFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItems != null && searchItems.size() > 0){
                        searchVendor = false;
                        searchGroups = true;
                        searchUsers = false;
                        searchEvent = false;
                        searchTeam = false;
                        if (q != null){
                            ArrayList<SearchItem> items = new ArrayList<>();
                            for (SearchItem item:searchItems) {
                                if (item.getType().contentEquals("group")){
                                    items.add(item);
                                }
                            }
                            if (items.size() > 0){
                                searchItems = items;
                                updateSearchView(searchItems);
                            } else {
                                performSearch(q);
                                //Toast.makeText(MainActivity.this, R.string.search_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView searchUsername;
        TextView detailsView;

        public SearchViewHolder(View itemView) {
            super(itemView);

            searchUsername = itemView.findViewById(R.id.searchUserName);
            detailsView = itemView.findViewById(R.id.detailsView);
        }

        public void bind(final SearchItem item){
            searchUsername.setText(item.getName());
            if (item.getDescription() != null && !item.getDescription().contentEquals("")){
                detailsView.setText(item.getDescription());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = MainActivity.this.getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                            .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");


                    if (item.getType().contentEquals("user")) {
                        if (userId.contentEquals(String.valueOf(item.getId()))){
                            currentTab = 3;
                            backClick(backView);
                        } else {
                            Intent intent = OtherProfileActivity.createIntent(
                                    String.valueOf(item.getId()),
                                    item.getName(),
                                    null,
                                    MainActivity.this);

                            startActivity(intent);
                        }
                    } else if(item.getType().contentEquals("group")){
                            Intent intent = GroupActivity.createIntent(item.getId(), item.getName(), MainActivity.this);
                            startActivity(intent);
                    }
                }
            });
        }
    }

    private class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<SearchItem> items;

        public SearchRecyclerAdapter(ArrayList<SearchItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_HEADER){
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_card, parent, false);
                return new SearchViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof SearchViewHolder) {
                SearchViewHolder pHolder = (SearchViewHolder) holder;
                pHolder.bind(searchItems.get(position-1));
            }
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)){
                return VIEW_HEADER;
            }

            return VIEW_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    }

    private void performSearch(final String query){
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_search.php", SearchResponse.class,
                new Response.Listener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse response) {
                        searchItems = new ArrayList<>();
                        if (searchUsers){
                            if (response.getUsers() != null && response.getUsers().length > 0){
                                Log.i("volley_response_search", "onResponse: " + response.getUsers()[0].getUsername());

                                for (int i = 0; i < response.getUsers().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getUsers()[i].getUserId());
                                    item.setName(response.getUsers()[i].getUsername());
                                    item.setType("user");
                                    searchItems.add(item);
                                }
                            }
                        } else if (searchGroups){
                            if (response.getGroups() != null && response.getGroups().length > 0){
                                for (int i = 0; i < response.getGroups().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getGroups()[i].getGroupId());
                                    item.setName(response.getGroups()[i].getGroup_name());
                                    if (response.getGroups()[i].getGroup_description() != null && !response.getGroups()[i].getGroup_description().contentEquals("")){
                                        item.setDescription(response.getGroups()[i].getGroup_description());
                                    }
                                    item.setType("group");
                                    searchItems.add(item);
                                }
                            }
                        } else if (searchVendor){
                            if (response.getVendors() != null && response.getVendors().length > 0){
                                for (int i = 0; i < response.getVendors().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getVendors()[i].getVendorId());
                                    item.setName(response.getVendors()[i].getVendorName());
                                    if (response.getVendors()[i].getVendorDescription() != null && !response.getVendors()[i].getVendorDescription().contentEquals("")){
                                        item.setDescription(response.getVendors()[i].getVendorDescription());
                                    }
                                    item.setType("vendor");
                                    searchItems.add(item);
                                }
                            }
                        } else if (searchTeam){
                            if (response.getTeams() != null && response.getTeams().length > 0){
                                for (int i = 0; i < response.getTeams().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getTeams()[i].getTeamId());
                                    item.setName(response.getTeams()[i].getTeamName());
                                    item.setType("team");
                                    searchItems.add(item);
                                }
                            }
                        } else if (searchEvent){
                            if (response.getEvents() != null && response.getEvents().length > 0){
                                for (int i = 0; i < response.getEvents().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getEvents()[i].getEventId());
                                    item.setName(response.getEvents()[i].getEventName());
                                    if (response.getEvents()[i].getEventDescription() != null && !response.getEvents()[i].getEventDescription().contentEquals("")){
                                        item.setDescription(response.getEvents()[i].getEventDescription());
                                    }
                                    item.setType("event");
                                    searchItems.add(item);
                                }
                            }
                        }

                        else {
                            if (response.getUsers() != null && response.getUsers().length > 0){
                                Log.i("volley response", "onResponse: " + response.getUsers()[0].getUsername());

                                for (int i = 0; i < response.getUsers().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getUsers()[i].getUserId());
                                    item.setName(response.getUsers()[i].getUsername());
                                    item.setType("user");
                                    searchItems.add(item);
                                }
                            }

                            if (response.getGroups() != null && response.getGroups().length > 0){
                                for (int i = 0; i < response.getGroups().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getGroups()[i].getGroupId());
                                    item.setName(response.getGroups()[i].getGroup_name());
                                    if (response.getGroups()[i].getGroup_description() != null && !response.getGroups()[i].getGroup_description().contentEquals("")){
                                        item.setDescription(response.getGroups()[i].getGroup_description());
                                    }
                                    item.setType("group");
                                    searchItems.add(item);
                                }
                            }

                            if (response.getVendors() != null && response.getVendors().length > 0){
                                for (int i = 0; i < response.getVendors().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getVendors()[i].getVendorId());
                                    item.setName(response.getVendors()[i].getVendorName());
                                    if (response.getVendors()[i].getVendorDescription() != null && !response.getVendors()[i].getVendorDescription().contentEquals("")){
                                        item.setDescription(response.getVendors()[i].getVendorDescription());
                                    }
                                    item.setType("vendor");
                                    searchItems.add(item);
                                }
                            }

                            if (response.getTeams() != null && response.getTeams().length > 0){
                                for (int i = 0; i < response.getTeams().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getTeams()[i].getTeamId());
                                    item.setName(response.getTeams()[i].getTeamName());
                                    item.setType("team");
                                    searchItems.add(item);
                                }
                            }

                            if (response.getEvents() != null && response.getEvents().length > 0){
                                for (int i = 0; i < response.getEvents().length; i++) {
                                    SearchItem item = new SearchItem();
                                    item.setId(response.getEvents()[i].getEventId());
                                    item.setName(response.getEvents()[i].getEventName());
                                    if (response.getEvents()[i].getEventDescription() != null && !response.getEvents()[i].getEventDescription().contentEquals("")){
                                        item.setDescription(response.getEvents()[i].getEventDescription());
                                    }
                                    item.setType("event");
                                    searchItems.add(item);
                                }
                            }
                        }


                        if (searchItems.size() > 0){
                            updateSearchView(searchItems);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley_error_search", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("query", query);
                map.put("cursor", "0");
                //map.put("cursor", "0");

                return map;
            }
        };

        Volley.newRequestQueue(this).add(customRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if(currentUser == null){
//           // sendToStart();
//        } else {
//
//            mUserRef.child("online").setValue("true");
//
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
       // Toast.makeText(getBaseContext(),userId,Toast.LENGTH_LONG).show();
    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, Login.class);
        startActivity(startIntent);
        finish();

    }
    private class getTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String url2 = "https://rezetopiachat.firebaseio.com/requests.json";
            StringRequest request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {

                    Firebase reference = new Firebase("https://rezetopiachat.firebaseio.com/requests");
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject obj2 = new JSONObject(obj.getString("friends_pending_"+userId));
                        Log.d("check_count",obj2.getString("count"));
                        Toast.makeText(MainActivity.this, obj2.getString("count"), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "500500", Toast.LENGTH_SHORT).show();
                        if (obj2.getString("count").equals("0")){
                            TextView textView = reqView.findViewById(R.id.req_count);
                            textView.setVisibility(View.GONE);
                        }else{
                            TextView textView = reqView.findViewById(R.id.req_count);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(obj2.getString("count"));
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );
                }
            });

            RequestQueue rQueue1 = Volley.newRequestQueue(MainActivity.this);
            rQueue1.add(request2);
            return null;
        }
    }

}

