package io.rezetopia.krito.rezetopiakrito.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

public class Splash extends AppCompatActivity {
 private ImageView imageView;
 int duration = 500;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView)findViewById(R.id.loading_bar);
         userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        Log.d("id_ooo",userId);

 //       AnimationDrawable animationDrawable = new AnimationDrawable();
 //       animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_1), duration);
 //       animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_2), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_3), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_4), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_5), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_6), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_7), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_8), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_9), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_10), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_11), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_12), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_13), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_14), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_15), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_16), duration);
//        animationDrawable.addFrame(getResources().getDrawable(R.drawable.ic_image2vector_17), duration);
//        animationDrawable.setOneShot(false);
//        imageView.setImageDrawable(animationDrawable);

//        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {

                                          if (!userId.contentEquals("0")){
                                              Intent myIntent = new Intent(Splash.this, MainActivity.class);
                                              //myIntent.putExtra("user_id",3101);
                                              startActivity(myIntent);
                                              finish();
                                          } else {
                                              Intent myIntent = new Intent(getApplicationContext(), Login.class);
                                              //myIntent.putExtra("user_id",3101);
                                              startActivity(myIntent);
                                              finish();
                                          }

                                      }
                                  }
                , 3000);
    }
}

