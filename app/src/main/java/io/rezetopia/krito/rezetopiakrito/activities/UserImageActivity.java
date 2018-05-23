package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

public class UserImageActivity extends AppCompatActivity {

    private static final String URL_EXTRA = "url_extra";

    ImageView uploadImageView;
    ImageView image_view;

    private ProgressDialog dialog = null;
    Image selectedImage;
    String url;
    String encodedImage;

    public static Intent createIntent(String url, Context context){
        Intent intent = new Intent(context, UserImageActivity.class);
        intent.putExtra(URL_EXTRA, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image);

        uploadImageView = findViewById(R.id.uploadImageView);
        image_view = findViewById(R.id.image_view);

        url = getIntent().getStringExtra(URL_EXTRA);
        if (url != null){
            Picasso.with(this).load(url).into(image_view);
        }

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("uploading your profile picture");
        dialog.setCancelable(false);
    }


    private void openImagePicker(){
        Dexter.withActivity(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ImagePicker.create(UserImageActivity.this)
                                .folderMode(true) // folder mode (false by default)
                                .toolbarFolderTitle("Folder") // folder selection title
                                .toolbarImageTitle("Tap to select")
                                .limit(1)
                                .start();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private class UploadImage extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
//            Bitmap bm = null;
//            bm = BitmapFactory.decodeFile(selectedImage.getPath());
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            final String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            Log.i("encodedImage", "doInBackground: " + encodedImage);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/upload.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("upload_pp", "onResponse: " + response);
                            if (response.contentEquals("updated")){
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("url", response);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                    dialog.dismiss();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();



                    String userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                            .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                    //map.put("method", "update_pp");
                    map.put("image", encodedImage);
                    map.put("img_name", String.valueOf(System.currentTimeMillis()/100));
                    map.put("id", userId);

                    return map;
                }
            };

            Volley.newRequestQueue(UserImageActivity.this).add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private void performUpload(){
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/upload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("upload_pp", "onResponse: " + response);
                        if (response.contentEquals("updated")){
                            Intent intent = new Intent();
                            intent.putExtra("url", response);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                Bitmap bm = null;
                bm = BitmapFactory.decodeFile(selectedImage.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                String userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                map.put("method", "update_pp");
                map.put("image", encodedImage);
                map.put("img_name", String.valueOf(System.currentTimeMillis()/100));
                map.put("id", userId);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            selectedImage = ImagePicker.getFirstImageOrNull(data);
            if (selectedImage != null){
                Bitmap bm = null;
                bm = BitmapFactory.decodeFile(selectedImage.getPath());
                image_view.setImageBitmap(bm);
                Bitmap bitmap = ((BitmapDrawable) image_view.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                dialog.show();
                new UploadImage().execute();
            }

                //performUpload();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
