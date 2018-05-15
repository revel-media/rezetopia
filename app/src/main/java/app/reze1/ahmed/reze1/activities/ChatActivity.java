package app.reze1.ahmed.reze1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.fragments.ChatFragment;
import app.reze1.ahmed.reze1.utils.Constants;


public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    String name;

    public static void startActivity(Context context,
                                     String name,
                                     String receiverUid,
                                     String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);

       //String mChatUser = getIntent().getStringExtra("user_id");
        intent.putExtra(name,Constants.ARG_RECEIVER);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        //setSupportActionBar(mToolbar);

        // set toolbar title
        //mToolbar.setTitle(getIntent().getExtras().getString(name));

        // set the register screen fragment
        String userName = getIntent().getStringExtra("user_name");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

}
