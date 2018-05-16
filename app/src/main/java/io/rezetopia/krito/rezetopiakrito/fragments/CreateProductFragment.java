package io.rezetopia.krito.rezetopiakrito.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import io.rezetopia.krito.rezetopiakrito.views.CustomButton;
import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 4/28/2018.
 */

public class CreateProductFragment extends DialogFragment implements View.OnClickListener{

    private static final String VENDOR_ID_EXTRA = "vendor_id_extra";

    ImageView productImageView;
    CustomEditText productTitleView;
    CustomEditText productDescriptionView;
    CustomEditText productPriceView;
    CustomEditText productDiscountView;
    CustomEditText productAmountView;
    CustomButton createProductButton;

    String vendorId;
    RequestQueue requestQueue;

    public static CreateProductFragment createFragment(String vendorId){
        CreateProductFragment fragment = new CreateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VENDOR_ID_EXTRA, vendorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_product, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(android.R.color.transparent)));
        productImageView = v.findViewById(R.id.productImageView);
        productTitleView = v.findViewById(R.id.productTitleView);
        productDescriptionView = v.findViewById(R.id.productDescriptionView);
        productPriceView = v.findViewById(R.id.productPriceView);
        productAmountView = v.findViewById(R.id.productAmountView);
        productDiscountView = v.findViewById(R.id.productDiscountView);
        createProductButton = v.findViewById(R.id.createProductButton);

        createProductButton.setOnClickListener(this);

        vendorId = getArguments().getString(VENDOR_ID_EXTRA);
        requestQueue = Volley.newRequestQueue(getActivity());

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createProductButton:
                if (validProduct()){
                    createProduct();
                }
                break;
        }
    }

    private void createProduct(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/vendor_operation.php",
                ProductResponse.class,
                new Response.Listener<ProductResponse>() {
                    @Override
                    public void onResponse(ProductResponse response) {
                        Log.i("product_response", "onResponse: " + response.getCreatedAt());
                        Intent intent = new Intent();
                        intent.putExtra("product", response);
                        getActivity().setResult(1005, intent);
                        dismiss();
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

                map.put("method", "create_product");
                map.put("vendor_id", vendorId);
                map.put("image_url", "no image");
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
