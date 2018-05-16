package io.rezetopia.krito.rezetopiakrito.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import io.rezetopia.krito.rezetopiakrito.views.CustomButton;
import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.store.StoreResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateStoreActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1003;
    ImageView storeImageView;
    CustomEditText storeNameView;
    CustomEditText storeDescriptionView;
    CustomEditText storeAddressView;
    CustomButton createStoreButton;

    private Image selectedImage;
    String encodedImage;
    private ProgressDialog dialog = null;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        storeImageView = findViewById(R.id.storeImageView);
        storeNameView = findViewById(R.id.storeNameView);
        storeDescriptionView = findViewById(R.id.storeDescriptionView);
        storeAddressView = findViewById(R.id.storeAddressView);
        createStoreButton = findViewById(R.id.createStoreButton);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your store");

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

        storeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        createStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validStore()){
                    performCreateStore();
                }
            }
        });

    }

    private void openImagePicker(){
        Dexter.withActivity(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ImagePicker.create(CreateStoreActivity.this)
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

    private boolean validStore(){

        if (storeNameView.getText().toString().length() > 0 && storeDescriptionView.getText().toString().length() > 0
                && storeAddressView.getText().toString().length() > 0){
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            selectedImage = ImagePicker.getFirstImageOrNull(data);
            storeImageView.setImageBitmap(BitmapFactory.decodeFile(selectedImage.getPath()));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performCreateStore(){
        dialog.show();

        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_store.php",
                StoreResponse.class,
                new Response.Listener<StoreResponse>() {
                    @Override
                    public void onResponse(StoreResponse response) {
                        //Log.i("volley response", "onResponse: " + response);
                        dialog.dismiss();
                        onBackPressed();
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

                if (selectedImage != null) {
                    Bitmap bm = null;
                    bm = BitmapFactory.decodeFile(selectedImage.getPath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    map.put("image", encodedImage);
                }

                map.put("method", "create_store");
                map.put("userId", userId);
                map.put("name", storeNameView.getText().toString());
                map.put("address", storeAddressView.getText().toString());
                map.put("description", storeDescriptionView.getText().toString());
                map.put("location_lang", String.valueOf("null"));
                map.put("location_attid", String.valueOf("null"));

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
