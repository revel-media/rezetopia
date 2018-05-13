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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.model.pojo.team.ApiResponse;
import app.reze1.ahmed.reze1.model.pojo.team.TeamResponse;
import app.reze1.ahmed.reze1.app.AppConfig;
import app.reze1.ahmed.reze1.helper.VolleyCustomRequest;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Mona Abdallh on 5/3/2018.
 */

public class TeamFragment extends Fragment {

    private static final String TYPE_EXTRA = "type";

    RecyclerView teamsRecView;
    RequestQueue requestQueue;
    TeamResponse[] teams;
    TeamsRecyclerAdapter adapter;

    String type;

    public static TeamFragment createFragment(String type){
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_EXTRA, type);
        TeamFragment fragment = new TeamFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);

        teamsRecView = view.findViewById(R.id.teamsRecView);
        type = getArguments().getString(TYPE_EXTRA);

        requestQueue = Volley.newRequestQueue(getActivity());

        if (type.contentEquals("owner")){
            performHostingTeams();
        } else if (type.contentEquals("member")){

        }

        return view;
    }

    private class TeamsViewHolder extends RecyclerView.ViewHolder{

        TextView teamNameView;

        public TeamsViewHolder(View itemView) {
            super(itemView);
            teamNameView = itemView.findViewById(R.id.teamNameView);

        }

        public void bind(final TeamResponse team){
            teamNameView.setText(team.getName());
        }
    }

    private class TeamsRecyclerAdapter extends RecyclerView.Adapter<TeamsViewHolder>{

        @NonNull
        @Override
        public TeamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.team_name_card,parent,false);
            //todo
            return new TeamsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamsViewHolder holder, int position) {
            holder.bind(teams[position]);
        }

        @Override
        public int getItemCount() {
            return teams.length;
        }
    }

    private void performHostingTeams(){
        VolleyCustomRequest customRequest = new VolleyCustomRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_team.php",
                ApiResponse.class,
                new Response.Listener<ApiResponse>() {
                    @Override
                    public void onResponse(ApiResponse response) {
                        //Log.i("hosting_teams", "onResponse: " + response.getTeams()[0].getName());
                        if (!response.getError()) {
                            teams = response.getTeams();
                            adapter = new TeamsRecyclerAdapter();
                            teamsRecView.setAdapter(adapter);
                            teamsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("is_member_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                map.put("method", "get_hosting_teams");
                map.put("ownerId", userId);

                return map;
            }
        };

        requestQueue.add(customRequest);
    }
}
