package io.rezetopia.krito.rezetopiakrito.fragments;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;

/**
 * Created by Ahmed Ali on 5/15/2018.
 */

public class Products extends Fragment {

    ArrayList<ProductResponse> products;

    private static final int HEADER_TYPE = 0;
    private static final int PRODUCT_TYPE = 1;

    RecyclerView productRecView;
    ProgressBar progressBar;
    RecyclerView.Adapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        productRecView = view.findViewById(R.id.productRecView);
        progressBar = view.findViewById(R.id.productProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        new FetchProductsTask().execute();

        return view;
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView productTitleView;
        TextView productDetailView;
        TextView priceView;
        ImageView productImageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            productDetailView = itemView.findViewById(R.id.productDetailView);
            productTitleView = itemView.findViewById(R.id.productTitleView);
            priceView = itemView.findViewById(R.id.priceView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }

        public void bind(ProductResponse response){
            productTitleView.setText(response.getTitle());
            priceView.setText(String.valueOf(response.getPrice()));
            productDetailView.setText(String.valueOf(response.getDescription()));
        }
    }


    private class ProductRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEADER_TYPE){
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.store_header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.product_card, parent, false);
                return new ProductViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof  ProductViewHolder){
                ProductViewHolder nHolder = (ProductViewHolder) holder;
                nHolder.bind(products.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position)){
                return HEADER_TYPE;
            } else {
                return PRODUCT_TYPE;
            }
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(final Void... voids) {
            String url = "http://rezetopia.dev-krito.com/app/reze/user_store.php";

            VolleyCustomRequest post = new VolleyCustomRequest(Request.Method.POST, url,
                    io.rezetopia.krito.rezetopiakrito.model.pojo.product.ApiResponse.class, new Response.Listener<io.rezetopia.krito.rezetopiakrito.model.pojo.product.ApiResponse>() {
                @Override
                public void onResponse(io.rezetopia.krito.rezetopiakrito.model.pojo.product.ApiResponse response) {
                    if (!response.isError()){
                        Log.i("Products", "onResponse: " + response.getProducts()[0].getName());
                        products = new ArrayList<>(Arrays.asList(response.getProducts()));
                        updateUi();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.i("Products", "onResponse: " + response.isError());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "error volley", Toast.LENGTH_LONG).show();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();

                    params.put("method", "get_all_products");
                    params.put("cursor", "0");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }
    }

    private void updateUi(){
        if (adapter == null){
            adapter = new ProductRecyclerAdapter();
            productRecView.setAdapter(adapter);
            productRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {

        }
    }
}
