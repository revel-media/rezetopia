package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.PostActivity;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.rezetopia.krito.rezetopiakrito.model.pojo.notification.ApiResponse;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notification extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue requestQueue;
    private ArrayList<io.rezetopia.krito.rezetopiakrito.model.pojo.notification.Notification> notifications;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Notification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        recyclerView = view.findViewById(R.id.notificationRecView);
        fetchNotifications();
        swipeRefreshLayout = view.findViewById(R.id.Refresh_notification);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNotifications();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateUi() {
        if (adapter == null) {
            adapter = new NotificationRecyclerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView usernameView;
        TextView createdAtView;
        TextView detailsView;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            usernameView = itemView.findViewById(R.id.notificationUsernameView);
            detailsView = itemView.findViewById(R.id.notificationMsgView);
            createdAtView = itemView.findViewById(R.id.notificationCreatedAtView);
        }

        public void bind(io.rezetopia.krito.rezetopiakrito.model.pojo.notification.Notification notification) {
            usernameView.setText(notification.getUsername());
            detailsView.setText(notification.getMessage());
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(notification.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = date.getTime();
            Date date1=new Date(milliseconds);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd, hh:mm aa");
            // long millisecondsFromNow = milliseconds - now;
            createdAtView.setText(String.valueOf(simpleDateFormat.format(date1)));
        }
    }

    private class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.notification_card, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position) {
            holder.bind(notifications.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("post-eventId", String.valueOf(notifications.get(position).getPostId()));
                    Intent intent = PostActivity.createIntent(notifications.get(position).getPostId(), getActivity());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }
    }


    private void fetchNotifications() {
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/push_notification.php",
                ApiResponse.class,
                new Response.Listener<ApiResponse>() {

                    @Override
                    public void onResponse(ApiResponse response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.getNotifications() != null) {
                                Log.i("volley response", "onResponse: " + response.getNotifications().get(0).getCreatedAt());
                                notifications = response.getNotifications();
                                Collections.reverse(notifications);
                                updateUi();
                            }
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

                String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");
                Log.i("user-eventId", "user-eventId" + userId);
                map.put("method", "get_notification");
                map.put("user_id", userId);

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}
