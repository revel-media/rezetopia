package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.ApiResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.PostResponse;
import io.rezetopia.krito.rezetopiakrito.helper.VolleyCustomRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amr on 4/15/2018.
 */

public class UserPostsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private WebView webview;
    String distfile = "";
    private PostResponse[] posts;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    int nextCursor = 0;
    long now;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserPostsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        recyclerView = view.findViewById(R.id.userPostsRecyclerView);

        fetchPosts();
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
        if (context instanceof UserPostsFragment.OnFragmentInteractionListener) {
            mListener = (UserPostsFragment.OnFragmentInteractionListener) context;
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

    private class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTextView;
        Button likeButton;
        Button commentButton;

        public PostViewHolder(final View itemView) {
            super(itemView);

            postTextView = itemView.findViewById(R.id.postTextView);
            likeButton = itemView.findViewById(R.id.postLikeButton);
            commentButton = itemView.findViewById(R.id.postCommentButton);
        }

        public void bind(final PostResponse post){
            postTextView.setText(post.getText());

            if (post.getLikes() != null && post.getLikes().length > 0){
                //likeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                likeButton.setText(post.getLikes().length + " Like");
            }

            if (post.getComments() != null && post.getComments().length > 0){
                //commentButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_star_holo_green,  0, 0, 0);
                commentButton.setText(post.getComments().length + " Comment");
            }

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*ArrayList<CommentResponse> comments = new ArrayList<>(Arrays.asList(post.getComments()));
                    Intent intent = CommentActivity.createIntent(comments, post.getLikes(), post.getPostId(), now,
                            Integer.parseInt(post.getUserId()), getActivity());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);*/
                }
            });
        }
    }

    private class PostRecyclerAdapter extends RecyclerView.Adapter<UserPostsFragment.PostViewHolder>{

        private PostResponse[] postArray;

        public PostRecyclerAdapter(PostResponse[] postArray) {
            this.postArray = postArray;
        }

        @NonNull
        @Override
        public UserPostsFragment.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.post_card, parent, false);
            return new UserPostsFragment.PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserPostsFragment.PostViewHolder holder, int position) {
            holder.bind(postArray[position]);
        }

        @Override
        public int getItemCount() {
            return postArray.length;
        }
    }

    private void fetchPosts(){
        VolleyCustomRequest stringRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.dev-krito.com/app/reze/user_post.php", ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        Log.i("volley response", "onResponse: " + response.getPosts()[0].getCreatedAt());
                        posts = response.getPosts();
                        nextCursor = response.getNextCursor();
                        now = response.getNow();
                        updateUi(posts);

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

    private void updateUi(PostResponse[] finalPosts){
        if (adapter == null){
            adapter = new PostRecyclerAdapter(finalPosts);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        } else {

        }
    }
}
