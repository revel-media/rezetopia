package app.reze1.ahmed.reze1.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.reze1.ahmed.reze1.R;
import app.reze1.ahmed.reze1.app.AppConfig;

import java.util.ArrayList;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview_profile, container, false);
        userId = getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, MODE_PRIVATE)
                .getString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0");

        return view;
    }


}
