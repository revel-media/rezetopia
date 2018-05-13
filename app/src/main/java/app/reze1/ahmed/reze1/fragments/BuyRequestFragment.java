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
import app.reze1.ahmed.reze1.model.pojo.store.RequestApiResponse;
import app.reze1.ahmed.reze1.model.pojo.store.RequestResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class BuyRequestFragment extends Fragment {

    private static final String STORE_ID_EXTRA = "store_id";

    RecyclerView recyclerView;

    RequestQueue requestQueue;
    int storeId;
    RequestResponse[] requests;
    RecyclerView.Adapter adapter;

    public static BuyRequestFragment createFragment(int id){
        Bundle bundle = new Bundle();
        bundle.putInt(STORE_ID_EXTRA, id);
        BuyRequestFragment fragment = new BuyRequestFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_product, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        recyclerView = view.findViewById(R.id.productRecView);

        if (getArguments() != null) {
            if (getArguments().getInt(STORE_ID_EXTRA) > 0) {
                storeId = getArguments().getInt(STORE_ID_EXTRA);
                //Log.i("vendor_id_arg_string", "onCreate: " + vendorId);
            }
        }

        fetchRequests();
        return view;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView notificationPPView;
        TextView notificationUsernameView;
        TextView notificationMsgView;
        TextView notificationCreatedAtView;
        TextView notificationMobileView;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            notificationPPView = itemView.findViewById(R.id.notificationPPView);
            notificationUsernameView = itemView.findViewById(R.id.notificationUsernameView);
            notificationMsgView = itemView.findViewById(R.id.notificationMsgView);
            notificationCreatedAtView = itemView.findViewById(R.id.notificationCreatedAtView);
            notificationMobileView = itemView.findViewById(R.id.notificationMobileView);
        }

        public void bind(RequestResponse request){
            if (request.getImageUrl() != null){
                Picasso.with(getActivity()).load(request.getImageUrl()).into(notificationPPView);
            }

            notificationUsernameView.setText(request.getUsername());
            String message = "wants to buy " + request.getAmount() + " of " + request.getTitle();
            notificationMsgView.setText(message);
            notificationCreatedAtView.setText(request.getCreatedAt());
            notificationMobileView.setText(request.getPhoneNumber());
        }
    }


    class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationViewHolder>{

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.notification_card, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            holder.bind(requests[position]);
        }

        @Override
        public int getItemCount() {
            return requests.length;
        }
    }


    private void fetchRequests(){
        //todo
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_store.php",
                RequestApiResponse.class,
                new Response.Listener<RequestApiResponse>() {
                    @Override
                    public void onResponse(RequestApiResponse response) {
                        if (!response.isError()) {
                            Log.i("request_response", "onResponse: " + response.getRequests()[0].getTitle());
                            requests = response.getRequests();
                            adapter = new NotificationRecyclerAdapter();
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

                map.put("method", "get_notification");
                map.put("store_id", String.valueOf(storeId));

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}
