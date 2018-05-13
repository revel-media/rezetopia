package app.reze1.ahmed.reze1.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.app.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tifaa on 15/04/18.
 */


public class RequestsAdapter extends ArrayAdapter<JSONObject> {
    Context context;
    int resource;
    ArrayList<JSONObject> vals;
    JSONObject jsonObject;
    RequestQueue requestQueue;
    String userId;
    TextView id;
    View rowView;
    Button button;
    public RequestsAdapter(@NonNull Context context, int resource, ArrayList<JSONObject> vals) {
        super(context, resource, vals);
        this.context = context;
        this.resource = resource;
        this.vals = vals;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        userId = context.getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        rowView = inflater.inflate(R.layout.request_row, null, true);
        TextView textView = (TextView)rowView.findViewById(R.id.sugName);
        //button = (Button)rowView.findViewById(R.id.accept);
        //jsonObject = new JSONObject(vals.get(position));
        try {
            textView.setText((vals.get(position)).get("name").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
