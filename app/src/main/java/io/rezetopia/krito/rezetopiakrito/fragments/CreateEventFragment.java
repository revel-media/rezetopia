package io.rezetopia.krito.rezetopiakrito.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import io.rezetopia.krito.rezetopiakrito.views.CustomButton;
import io.rezetopia.krito.rezetopiakrito.views.CustomEditText;
import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class CreateEventFragment extends DialogFragment {

    CustomEditText eventOccurDateView;

    int day;
    int month;
    int year;
    String name;
    String description;
    CustomEditText eventNameView;
    CustomEditText eventDescriptionView;
    CustomButton createProductButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event, container, false);
        eventOccurDateView = view.findViewById(R.id.eventOccurDateView);
        eventNameView = view.findViewById(R.id.eventNameView);
        eventDescriptionView = view.findViewById(R.id.eventDescriptionView);
        createProductButton = view.findViewById(R.id.createProductButton);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        eventOccurDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year_, int monthOfYear, int dayOfMonth) {
                        day = dayOfMonth;
                        month = monthOfYear;
                        year = year_;
                        eventOccurDateView.setText(String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));
                    }
                }, 2018, 05, 06).show();
            }
        });

        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day > 0 && isValid()){
                    performCreateEvent();
                }
            }
        });

        return view;
    }

    private boolean isValid(){
        name = eventNameView.getText().toString();
        description = eventDescriptionView.getText().toString();
        if (!name.contentEquals("") && !name.contentEquals("")){
            return true;
        }

        return false;
    }

    private void performCreateEvent(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/reze/user_event.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null){
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (!jsonObject.getBoolean("error")){
                                    dismiss();
                                } else {
                                  //  Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                map.put("method", "create_event");
                String userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, null);
                map.put("userId", userId);
                map.put("name", name);
                map.put("description", description);
                map.put("occur_date", String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));

                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
