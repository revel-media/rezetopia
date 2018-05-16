package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import io.rezetopia.krito.rezetopiakrito.R;


public class Language extends AppCompatActivity {
    Locale myLocale;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        Button  English =(Button)findViewById(R.id.En);
        Button Arabic =(Button)findViewById(R.id.Ar);
        progressBar = new ProgressDialog(Language.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(R.style.DialogStyle);
       // progressBar.show();

    }
    public void onClickTextView(View view){

        switch (view.getId()) {
            case R.id.En:
                setLocal("en");
                break;
            case R.id.Ar:
                setLocal("ar");
                break;
            default:
                setLocal("en");
                break;
        }
    }
    public void setLocal(String language) {
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        progressBar.dismiss();
        startActivity(intent);
        finish();
    }
}
