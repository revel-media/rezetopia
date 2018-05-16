package io.rezetopia.krito.rezetopiakrito.fragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import io.rezetopia.krito.rezetopiakrito.views.CustomButton;
import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class CreateStoreFragment extends DialogFragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_store, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        storeImageView = view.findViewById(R.id.storeImageView);
        storeNameView = view.findViewById(R.id.storeNameView);
        storeDescriptionView = view.findViewById(R.id.storeDescriptionView);
        storeAddressView = view.findViewById(R.id.storeAddressView);
        createStoreButton = view.findViewById(R.id.createStoreButton);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Creating your store");

        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
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


        return view;
    }

    private void openImagePicker(){
        Dexter.withActivity(getActivity()).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ImagePicker.create(getActivity())
                                .folderMode(true) // folder mode (false by default)
                                .toolbarFolderTitle("Folder") // folder selection title
                                .toolbarImageTitle("Tap to select")
                                .start(IMAGE_REQUEST_CODE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                selectedImage = ImagePicker.getFirstImageOrNull(data);
                storeImageView.setImageBitmap(BitmapFactory.decodeFile(selectedImage.getPath()));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performCreateStore(){
        dialog.show();
        Bitmap bm = null;
        bm = BitmapFactory.decodeFile(selectedImage.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_store.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("volley response", "onResponse: " + response);
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

                map.put("image_url", encodedImage);

                map.put("method", "create_store");
                map.put("userId", userId);
                map.put("name", storeNameView.getText().toString());
                map.put("address", storeAddressView.getText().toString());
                map.put("description", storeDescriptionView.getText().toString());

                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
