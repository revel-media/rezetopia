package app.reze1.ahmed.reze1.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import app.reze1.ahmed.reze1.fragments.Home;
import app.reze1.ahmed.reze1.fragments.Notification;
import app.reze1.ahmed.reze1.helper.MainPagerAdapter;
import app.reze1.ahmed.reze1.fragments.Profile;
import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.fragments.Requests;
import app.reze1.ahmed.reze1.model.pojo.search.SearchItem;
import app.reze1.ahmed.reze1.model.pojo.search.SearchResponse;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.ResizeWidthAnimation;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import app.reze1.ahmed.reze1.model.pojo.user.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
      //  Toast.makeText(getBaseContext(),SocketConnect.socket+"",Toast.LENGTH_LONG).show();
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        userId = getBaseContext().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
        if (mAuth.getCurrentUser() != null) {
            User user = new User();

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(String.valueOf(user.getId()));

        }
        savedBundle = savedInstanceState;
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(AppConfig.LOGGED_IN_FIRE_ID_SHARED,"fireAuthed").apply();
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        reqView = LayoutInflater.from(this).inflate(R.layout.request_tab_icon, null);
        mCustomView = mInflater.inflate(R.layout.action_bar, null);
        searchBox = mCustomView.findViewById(R.id.searchbox);
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

    public void active(View view){
        ResizeWidthAnimation anim = new ResizeWidthAnimation(view, 400);
        anim.setDuration(20);
        view.startAnimation(anim);
        setContentView(R.layout.search_view);
        view.setFocusable(true);
        backView.setVisibility(View.VISIBLE);
        inflateSearchView();
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
    }

    private void inflateMainView(int current){
        if (current > 0){
            setContentView(R.layout.activity_main);
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_tab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notification_tab));
        tabLayout.addTab(tabLayout.newTab().setCustomView(reqView));
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
                if (tab.getPosition() == 2){
                    ImageView imageView = tab.getCustomView().findViewById(R.id.icon_in);
                    TextView textView = tab.getCustomView().findViewById(R.id.req_count);
                    imageView.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    textView.setVisibility(View.GONE);
                    textView.setText("0");
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

                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

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

    private void inflateSearchView(){
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0 && !s.toString().contentEquals("0")){
                    q = s.toString();
                    performSearch(q);
                } else {
                    searchGroups = false;
                    searchUsers = false;
                    searchEvent = false;
                    searchVendor = false;
                    searchTeam = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q != null){
                    performSearch(q);
                }
            }
        });
    }

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
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_search.php", SearchResponse.class,
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
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
           // sendToStart();
        } else {

            mUserRef.child("online").setValue("true");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, Login.class);
        startActivity(startIntent);
        finish();

    }

}

