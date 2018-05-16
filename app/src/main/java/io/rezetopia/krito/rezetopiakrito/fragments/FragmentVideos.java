package io.rezetopia.krito.rezetopiakrito.fragments;

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
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.AttachmentResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.MediaResponse;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amr on 4/15/2018.
 */

public class FragmentVideos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private WebView webview;
    String distfile = "";
    private ArrayList<MediaResponse> finalVideos;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    int nextCursor = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPhotos.OnFragmentInteractionListener mListener;

    public FragmentVideos() {
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
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        recyclerView = view.findViewById(R.id.video_recycler_view);

        fetchVideos();
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

    private class videoViewHolder extends RecyclerView.ViewHolder{

       VideoView videoView;


        public videoViewHolder(final View itemView) {
            super(itemView);

            videoView = (VideoView)itemView.findViewById(R.id.videoView);

        }

        public void bind(final AttachmentResponse videos){
            //  Glide.with(context).load(my_data.get(position).getImage_link()).into(holder.imageView);
        }
    }

    private class videoRecyclerAdapter extends RecyclerView.Adapter<FragmentVideos.videoViewHolder>{

        private AttachmentResponse[] videoArray;

        public videoRecyclerAdapter(AttachmentResponse[] videoArray) {
            this.videoArray = videoArray;
        }

        @NonNull
        @Override
        public FragmentVideos.videoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.video_row, parent, false);
            return new FragmentVideos.videoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FragmentVideos.videoViewHolder holder, int position) {

        }



        @Override
        public int getItemCount() {
            return videoArray.length;
        }
    }

    private void fetchVideos(){
        // hna hytm t3del el url 3la 7asb el api elly htt3ml
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_post.php", ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        Log.i("volley response", "onResponse: " + response.getPosts()[0].getUserId());
                        if (response.getPosts() != null && response.getPosts().length > 0){
                            finalVideos = new ArrayList<>();
                            for (int i = 0; i < response.getPosts().length; i++) {
                                MediaResponse[] videos =  response.getPosts()[i].getAttachment().getVideos();
                                finalVideos.addAll(new ArrayList<>(Arrays.asList(videos)));
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

