package app.reze1.ahmed.reze1.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.reze1.ahmed.reze1.views.CustomButton;
import app.reze1.ahmed.reze1.views.CustomEditText;
import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.app.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Mona Abdallh on 5/3/2018.
 */

public class CreateTeam extends DialogFragment {

    CustomEditText teamTitleView;
    CustomButton createTeamButton;

    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_team, container, false);

        teamTitleView = view.findViewById(R.id.teamTitleView);
        createTeamButton = view.findViewById(R.id.createTeamButton);

        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validTeam()){
                    performCreateTeam();
                }
            }
        });

        requestQueue = Volley.newRequestQueue(getActivity());
        return view;
    }

    private boolean validTeam(){
        if (!teamTitleView.getText().toString().contentEquals("")){
            return true;
        }

        return false;
    }

    private void performCreateTeam(){
        StringRequest customRequest = new StringRequest(Request.Method.POST, "https://rezetopia.com/app/reze/user_team.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.i("hosting_teams", "onResponse: " + response.getTeams()[0].getName());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                dismiss();
                            } else {
                               // Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("is_member_error", "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);

                map.put("method", "create_team");
                map.put("userId", userId);
                map.put("name", teamTitleView.getText().toString());

                return map;
            }
        };

        requestQueue.add(customRequest);
    }
}
