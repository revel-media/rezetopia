package app.reze1.ahmed.reze1.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.post.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.post.MediaResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PhotoResponse;
import app.reze1.ahmed.reze1.model.pojo.post.PostResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amr on 4/15/2018.
 */

public class FragmentPhotos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private WebView webview;
    String distfile = "";
    private ArrayList<MediaResponse> finalPhotos;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    int nextCursor = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPhotos.OnFragmentInteractionListener mListener;

    public FragmentPhotos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        View view = inflater.inflate(R.layout.photo_list_item, container, false);

        recyclerView = view.findViewById(R.id.photo_list_view);

        fetchPhotos();
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
        if (context instanceof FragmentPhotos.OnFragmentInteractionListener) {
            mListener = (FragmentPhotos.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class photoViewHolder extends RecyclerView.ViewHolder{

        ImageView photoImageView;

        public photoViewHolder(final View itemView) {
            super(itemView);

            photoImageView = itemView.findViewById(R.id.user_image);

        }

        public void bind(final PhotoResponse photo){
          //  Glide.with(context).load(my_data.get(position).getImage_link()).into(holder.imageView);
        }
    }

    private class photoRecyclerAdapter extends RecyclerView.Adapter<FragmentPhotos.photoViewHolder>{

        private PostResponse[] postArray;

        public photoRecyclerAdapter(PostResponse[] postArray) {
            this.postArray = postArray;
        }

        @NonNull
        @Override
        public photoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.photo_list_item, parent, false);
            return new FragmentPhotos.photoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull photoViewHolder holder, int position) {

        }



        @Override
        public int getItemCount() {
            return postArray.length;
        }
    }

    private void fetchPhotos(){
        // hna hytm t3del el url 3la 7asb el api elly htt3ml
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php", ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        Log.i("volley response", "onResponse: " + response.getPosts()[0].getUserId());
                        if (response.getPosts() != null && response.getPosts().length > 0){
                            finalPhotos = new ArrayList<>();
                            for (int i = 0; i < response.getPosts().length; i++) {
                                MediaResponse[] images =  response.getPosts()[i].getAttachment().getImages();
                                finalPhotos.addAll(new ArrayList<>(Arrays.asList(images)));
                            }
                        }
                        nextCursor = response.getNextCursor();
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

                map.put("method", "get_news_feed");
                map.put("cursor", "0");

                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


    }

