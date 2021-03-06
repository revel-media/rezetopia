package io.rezetopia.krito.rezetopiakrito.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.AttachmentResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.MediaResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog dialog = null;
    private List<Image> selectedImages;

    private ArrayList<String> encodedImages;

    EditText postTextView;
    ImageView imageView;
    TextView postButton;
    String userId;
    LinearLayout newPostHeader;
    String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        postTextView = findViewById(R.id.new_post_desc);
        imageView = findViewById(R.id.new_post_image);
        postButton =  findViewById(R.id.post_btn);
        newPostHeader = findViewById(R.id.newPostHeader);

        userId = getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);


        imageView.setOnClickListener(this);
        postButton.setOnClickListener(this);
        newPostHeader.setOnClickListener(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your post");
    }


    private void openImagePicker() {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ImagePicker.create(CreatePostActivity.this)
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_btn:
                if (validPost()) {
                    performUserUpload();
                }
                break;
            case R.id.new_post_image:
                openImagePicker();
                break;
            case R.id.newPostHeader:
                if (validPost()) {
                    performUserUpload();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            selectedImages = ImagePicker.getImages(data);
            ((ImageView)findViewById(R.id.postImage)).setImageBitmap(BitmapFactory.decodeFile(selectedImages.get(0).getPath()));
            //Bitmap per_img = ((BitmapDrawable) ((ImageView)findViewById(R.id.postImage)).getDrawable()).getBitmap();
            ((ImageView)findViewById(R.id.postImage)).setAdjustViewBounds(true);
            ((ImageView)findViewById(R.id.postImage)).setMaxHeight(500);
            /*if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                Uri imgSelectedUri = data.getData();
                ((ImageView)findViewById(R.id.postImage)).setImageURI(imgSelectedUri);
                Bitmap per_img = ((BitmapDrawable) ((ImageView)findViewById(R.id.postImage)).getDrawable()).getBitmap();
                ((ImageView)findViewById(R.id.postImage)).setAdjustViewBounds(true);
                ((ImageView)findViewById(R.id.postImage)).setMaxHeight(200);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                per_img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                encodedImage = android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT);
            }*/
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validPost() {

        if (postTextView.getText().toString().length() > 0 || (selectedImages != null && selectedImages.size() > 0)) {
            return true;
        }

        return false;
    }

    private void performUserUpload() {
        dialog.show();
        encodedImages = new ArrayList<>();
        if (selectedImages != null) {
            if (selectedImages.size() > 0) {
                for (Image image : selectedImages) {
                    Bitmap bm = null;
                    bm = BitmapFactory.decodeFile(image.getPath());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    encodedImages.add(encodedImage);
                }

//                HashMap<String, ArrayList<String>> jsonMap = new HashMap<>();
//                jsonMap.put("imageList", encodedImages);
            }
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_post.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("create_post_response", "onResponse: " + response);
                        PostResponse postResponse = new PostResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(response);



                            if (jsonObject.getInt("post_id") > 0){
                                postResponse.setUserId(userId);
                                postResponse.setPostId(jsonObject.getInt("post_id"));
                                postResponse.setUsername(jsonObject.getString("username"));
                                postResponse.setCreatedAt(jsonObject.getString("createdAt"));
                                if (jsonObject.getString("text") != null && !jsonObject.getString("text").isEmpty()){
                                    postResponse.setText(jsonObject.getString("text"));
                                }

                                if (jsonObject.getJSONArray("urls") != null && jsonObject.getJSONArray("urls").length() > 0){
                                    JSONArray urls = jsonObject.getJSONArray("urls");
                                    MediaResponse media = new MediaResponse();
                                    media.setPath(urls.getString(0));
                                    AttachmentResponse attachmentResponse = new AttachmentResponse();
                                    attachmentResponse.setImages(new MediaResponse[]{media});
                                    postResponse.setAttachment(attachmentResponse);
                                }
                            } else {
                                dialog.dismiss();
                            }

                        } catch (JSONException e) {
                            Log.i("create_post_response", "onResponse: " + e.toString());
                            e.printStackTrace();
                        }

                        if (postResponse.getPostId() > 0){
                            Intent intent = new Intent();
                            intent.putExtra("post", postResponse);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                if (encodedImages != null && encodedImages.size() > 0) {
                    for (String value : encodedImages) {
                        map.put(String.valueOf(encodedImages.indexOf(value)), value);
                    }
                    map.put("images_size", String.valueOf(encodedImages.size()));
                }

                map.put("method", "create_post");
                map.put("userId", userId);
                if (postTextView.getText().toString().length() > 0) {
                    map.put("post_text", postTextView.getText().toString());
                }

                return map;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
