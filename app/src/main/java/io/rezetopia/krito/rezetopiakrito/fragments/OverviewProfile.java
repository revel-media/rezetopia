package io.rezetopia.krito.rezetopiakrito.fragments;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ahmed on 4/23/2018.
 */

public class OverviewProfile extends Fragment {

    private RecyclerView recyclerView;
    String userId;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<String> mDataset = new ArrayList<String>();
    LinearLayout overviewLayout;
    public TextView seekval1;
    public TextView seekval2;
    public TextView seekval3;
    public TextView seekval4;
    public TextView seekval5;
    public TextView seekval6;
    public TextView seekval7;
    public TextView seekval8;
    public TextView seekval9;
    public TextView seekval10;
    public TextView seekval11;
    public TextView seekval12;
    public DiscreteSeekBar discreteSeekBar1;
    public DiscreteSeekBar discreteSeekBar2;
    public DiscreteSeekBar discreteSeekBar3;
    public DiscreteSeekBar discreteSeekBar4;
    public DiscreteSeekBar discreteSeekBar5;
    public DiscreteSeekBar discreteSeekBar6;
    public DiscreteSeekBar discreteSeekBar7;
    public DiscreteSeekBar discreteSeekBar8;
    public DiscreteSeekBar discreteSeekBar9;
    public DiscreteSeekBar discreteSeekBar10;
    public DiscreteSeekBar discreteSeekBar11;
    public DiscreteSeekBar discreteSeekBar12;

    ProgressBar overviewProgress ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview_profile, container, false);
        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        overviewLayout = view.findViewById(R.id.overviewLayout);
        overviewLayout.setVisibility(View.GONE);

        discreteSeekBar1 = (DiscreteSeekBar) view.findViewById(R.id.discrete1);
        seekval1 = (TextView)view.findViewById(R.id.perval1);

        discreteSeekBar2 = (DiscreteSeekBar) view.findViewById(R.id.discrete2);
        seekval2 = (TextView)view.findViewById(R.id.perval2);

        discreteSeekBar3 = (DiscreteSeekBar) view.findViewById(R.id.discrete3);
        seekval3 = (TextView)view.findViewById(R.id.perval3);

        discreteSeekBar4 = (DiscreteSeekBar) view.findViewById(R.id.discrete4);
        seekval4 = (TextView)view.findViewById(R.id.perval4);

        discreteSeekBar5 = (DiscreteSeekBar) view.findViewById(R.id.discrete5);
        seekval5 = (TextView)view.findViewById(R.id.perval5);

        discreteSeekBar6 = (DiscreteSeekBar) view.findViewById(R.id.discrete6);
        seekval6 = (TextView)view.findViewById(R.id.perval6);

        discreteSeekBar7 = (DiscreteSeekBar) view.findViewById(R.id.discrete7);
        seekval7 = (TextView)view.findViewById(R.id.perval7);

        discreteSeekBar8 = (DiscreteSeekBar) view.findViewById(R.id.discrete8);
        seekval8 = (TextView)view.findViewById(R.id.perval8);

        discreteSeekBar9 = (DiscreteSeekBar) view.findViewById(R.id.discrete9);
        seekval9 = (TextView)view.findViewById(R.id.perval9);

        discreteSeekBar10 = (DiscreteSeekBar) view.findViewById(R.id.discrete10);
        seekval10 = (TextView)view.findViewById(R.id.perval10);

        discreteSeekBar11 = (DiscreteSeekBar) view.findViewById(R.id.discrete11);
        seekval11 = (TextView)view.findViewById(R.id.perval11);

        discreteSeekBar12 = (DiscreteSeekBar) view.findViewById(R.id.discrete12);
        seekval12 = (TextView)view.findViewById(R.id.perval12);


        overviewProgress = view.findViewById(R.id.overviewProgress);
        overviewProgress.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        new SkillsTask().execute();

        return view;
    }

    private class SkillsTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(final String... strings) {


            StringRequest post = new StringRequest(Request.Method.POST, "http://rezetopia.dev-krito.com/app/get_skills.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        Log.i("skills", "onResponse: " + response);
                        JSONObject jsonObject = json.getJSONObject("user");
                        if (!json.getBoolean("error")) {
                            overviewProgress.setVisibility(View.GONE);
                            overviewLayout.setVisibility(View.VISIBLE);
                            if (jsonObject.getInt("attack") > 0) {
                                discreteSeekBar1.setProgress(jsonObject.getInt("attack") / 10);
                                seekval1.setText(jsonObject.getInt("attack") + " %");
//                                discreteSeekBar1.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval1.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("defence") > 0) {
                                discreteSeekBar2.setProgress(jsonObject.getInt("defence") / 10);
                                seekval2.setText(jsonObject.getInt("defence") +" %");
//                                discreteSeekBar2.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval2.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("stamina") > 0) {
                                discreteSeekBar3.setProgress(jsonObject.getInt("stamina") / 10);
                                seekval3.setText(jsonObject.getInt("stamina") +" %");
//                                discreteSeekBar3.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval3.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("speed")>0) {
                                discreteSeekBar4.setProgress(jsonObject.getInt("speed") / 10);
                                seekval4.setText(jsonObject.getInt("speed") +" %");
//                                discreteSeekBar4.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval4.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("ball_control")>0) {
                                discreteSeekBar5.setProgress(jsonObject.getInt("ball_control") / 10);
                                seekval5.setText(jsonObject.getInt("ball_control") +" %");
//                                discreteSeekBar5.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval5.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("low_pass") >0) {
                                discreteSeekBar6.setProgress(jsonObject.getInt("low_pass") / 10);
                                seekval6.setText(jsonObject.getInt("low_pass") + " %");
//                                discreteSeekBar6.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval6.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("lofted_pass") >0) {
                                discreteSeekBar7.setProgress(jsonObject.getInt("lofted_pass") / 10);
                                seekval7.setText(jsonObject.getInt("lofted_pass") +" %");
//                                discreteSeekBar7.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval7.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("shoot_accuracy") >0) {
                                discreteSeekBar8.setProgress(jsonObject.getInt("shoot_accuracy") / 10);
                                seekval8.setText(jsonObject.getInt("shoot_accuracy") +" %");
//                                discreteSeekBar8.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval8.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("shoot_power") >0) {
                                discreteSeekBar9.setProgress(jsonObject.getInt("shoot_power") / 10);
                                seekval9.setText(jsonObject.getInt("shoot_power") +" %");
//                                discreteSeekBar9.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval9.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("free_kicks") >0) {
                                discreteSeekBar10.setProgress(jsonObject.getInt("free_kicks") / 10);
                                seekval10.setText(jsonObject.getInt("free_kicks") +" %");
//                                discreteSeekBar10.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval10.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("header") > 0) {
                                discreteSeekBar11.setProgress(jsonObject.getInt("header") / 10);
                                seekval11.setText(jsonObject.getInt("header") +" %");
//                                discreteSeekBar11.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval11.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }

                            if (jsonObject.getInt("jump") >0) {
                                discreteSeekBar12.setProgress(jsonObject.getInt("jump") / 10);
                                seekval12.setText(jsonObject.getInt("jump") + " %");
//                                discreteSeekBar12.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
//                                    @Override
//                                    public int transform(int value) {
//                                        seekval12.setText(value*10+" %");
//                                        return value * 10;
//                                    }
//                                });
                            }
                        } else {
                            overviewLayout.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("id", userId);
                    params.put("method", "get_skills");
                    return params;
                }
            };
            Volley.newRequestQueue(getActivity()).add(post);
            return null;
        }
    }


}
