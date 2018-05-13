package app.reze1.ahmed.reze1.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.group.GroupResponse;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 4/30/2018.
 */

public class GroupDescription extends Fragment {

    String groupId;
    TextView desView;

    public static GroupDescription createFragment(String group_id){
        Bundle bundle = new Bundle();
        bundle.putString("group_id", group_id);
        GroupDescription fragment = new GroupDescription();
        fragment.setArguments(bundle);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_description, container, false);

        desView = view.findViewById(R.id.desView);
        groupId = getArguments().getString("group_id");

        performGetGroup();
        return view;
    }

    private void performGetGroup(){
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_group.php",
                GroupResponse.class,
                new Response.Listener<GroupResponse>() {
                    @Override
                    public void onResponse(GroupResponse response) {
                        Log.i("get_group", "onResponse: " + response.getDescription());
                        desView.setText(response.getDescription());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("get_group_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("method", "get_group");
                map.put("group_id", String.valueOf(groupId));

                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(customRequest);
    }
}
