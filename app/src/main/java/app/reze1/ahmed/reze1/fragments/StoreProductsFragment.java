package app.reze1.ahmed.reze1.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.product.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.product.ProductResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class StoreProductsFragment extends Fragment {

    private static final String STORE_ID_EXTRA = "store_id";

    RecyclerView recyclerView;

    RequestQueue requestQueue;
    int storeId;
    ProductResponse[] products;
    RecyclerView.Adapter adapter;

    public static StoreProductsFragment createFragment(int id){
        Bundle bundle = new Bundle();
        bundle.putInt(STORE_ID_EXTRA, id);
        StoreProductsFragment fragment = new StoreProductsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_product, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        recyclerView = view.findViewById(R.id.productRecView);

        if (getArguments() != null) {
            if (getArguments().getInt(STORE_ID_EXTRA) > 0) {
                storeId = getArguments().getInt(STORE_ID_EXTRA);
                //Log.i("vendor_id_arg_string", "onCreate: " + vendorId);
            }
        }

        fetchProducts();

        return view;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView productImageView;
        TextView productTitleView;
        TextView productDetailView;
        TextView priceView;
        TextView avilView;
        ImageView productContextMenuView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.productImageView);
            productTitleView = itemView.findViewById(R.id.productTitleView);
            productDetailView = itemView.findViewById(R.id.productDetailView);
            priceView = itemView.findViewById(R.id.priceView);
            avilView = itemView.findViewById(R.id.avilView);
            productContextMenuView = itemView.findViewById(R.id.productContextMenuView);
        }

        public void bind(ProductResponse product){
            productTitleView.setText(product.getTitle());
            productDetailView.setText(product.getDescription());

            String point = getActivity().getResources().getString(R.string.pound);
            priceView.setText(String.valueOf(product.getPrice()) + " " + point);

            if (product.getAmount() > product.getSoldAmount()){
                avilView.setText(R.string.available);
            } else {
                avilView.setText(R.string.unavailable);
            }

            if (product.getImageUrl() != null){
                Picasso.with(getActivity()).load(product.getImageUrl()).into(productImageView);
            }
        }
    }


    class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder>{

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.product_card, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.bind(products[position]);
        }

        @Override
        public int getItemCount() {
            return products.length;
        }
    }


    private void fetchProducts(){
        //todo
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_store.php",
                ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        if (!response.isError()) {
                            Log.i("product_response", "onResponse: " + response.getProducts()[0].getTitle());
                            products = response.getProducts();
                            adapter = new ProductRecyclerAdapter();
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            //updateUi();
                        }
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

                map.put("method", "get_my_products");
                map.put("store_id", String.valueOf(storeId));
                map.put("cursor", String.valueOf(0));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new ProductRecyclerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
