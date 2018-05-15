package app.reze1.ahmed.reze1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.views.CustomButton;
import app.reze1.ahmed.reze1.views.CustomEditText;
import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.product.ProductResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String STORE_ID_EXTRA = "store_id_extra";

    ImageView productImageView;
    CustomEditText productTitleView;
    CustomEditText productDescriptionView;
    CustomEditText productPriceView;
    CustomEditText productDiscountView;
    CustomEditText productAmountView;
    CustomButton createProductButton;

    String storeId;
    RequestQueue requestQueue;

    private Image selectedImage;
    String encodedImage;
    private ProgressDialog dialog = null;

    public static Intent createIntent(String id, Context context){
        Intent intent = new Intent(context, CreateProductActivity.class);
        intent.putExtra(STORE_ID_EXTRA, id);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        productImageView = findViewById(R.id.productImageView);
        productTitleView = findViewById(R.id.productTitleView);
        productDescriptionView = findViewById(R.id.productDescriptionView);
        productPriceView = findViewById(R.id.productPriceView);
        productAmountView = findViewById(R.id.productAmountView);
        productDiscountView = findViewById(R.id.productDiscountView);
        createProductButton = findViewById(R.id.createProductButton);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your store");

        createProductButton.setOnClickListener(this);
        productImageView.setOnClickListener(this);

        //storeId = getIntent().getStringExtra(STORE_ID_EXTRA);
        requestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createProductButton:
                if (validProduct()){
                    new CreateProductAsync().execute();
//                    createProduct();
                }
                break;
            case R.id.productImageView:
                openImagePicker();
                break;
        }
    }

    private void openImagePicker(){
        Dexter.withActivity(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ImagePicker.create(CreateProductActivity.this)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            selectedImage = ImagePicker.getFirstImageOrNull(data);
            productImageView.setImageBitmap(BitmapFactory.decodeFile(selectedImage.getPath()));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class CreateProductAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            dialog.show();

            VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_store.php",
                    ProductResponse.class,
                    new Response.Listener<ProductResponse>() {
                        @Override
                        public void onResponse(ProductResponse response) {
                            Log.i("product_response", "onResponse: " + response.getCreatedAt());
                        /*Intent intent = new Intent();
                        intent.putExtra("product", response);
                        setResult(1005, intent);*/
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("volley error", "onErrorResponse: " + error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();

                    String title = productTitleView.getText().toString();
                    String price = productPriceView.getText().toString();
                    String amount = productAmountView.getText().toString();
                    String description = productDescriptionView.getText().toString();
                    String sale = productDiscountView.getText().toString();

                    if (selectedImage != null) {
                        Bitmap bm = null;
                        bm = BitmapFactory.decodeFile(selectedImage.getPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                        map.put("image", encodedImage);
                    }

                    String userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                            .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                    map.put("method", "create_product");
                    map.put("user_id", userId);
                    map.put("title", title);
                    map.put("price", price);
                    map.put("amount", amount);
                    map.put("description", description);
                    map.put("sale", sale);


                    return map;
                }
            };

            requestQueue.add(stringRequest);
            return null;
        }
    }

    private void createProduct(){
        dialog.show();

        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_store.php",
                ProductResponse.class,
                new Response.Listener<ProductResponse>() {
                    @Override
                    public void onResponse(ProductResponse response) {
                        Log.i("product_response", "onResponse: " + response.getCreatedAt());
                        /*Intent intent = new Intent();
                        intent.putExtra("product", response);
                        setResult(1005, intent);*/
                        onBackPressed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                String title = productTitleView.getText().toString();
                String price = productPriceView.getText().toString();
                String amount = productAmountView.getText().toString();
                String description = productDescriptionView.getText().toString();
                String sale = productDiscountView.getText().toString();

                if (selectedImage != null) {
                    Bitmap bm = null;
                    bm = BitmapFactory.decodeFile(selectedImage.getPath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    map.put("image", encodedImage);
                }

                map.put("method", "create_product");
                map.put("store_id", storeId);
                map.put("title", title);
                map.put("price", price);
                map.put("amount", amount);
                map.put("description", description);
                map.put("sale", sale);


                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean validProduct(){
        String title = productTitleView.getText().toString();
        String price = productPriceView.getText().toString();
        String amount = productAmountView.getText().toString();

        if (!title.contentEquals("") && !price.contentEquals("") && !amount.contentEquals("")){
            return true;
        }

        return false;
    }
}
