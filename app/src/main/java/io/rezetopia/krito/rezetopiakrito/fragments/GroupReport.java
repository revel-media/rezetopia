package io.rezetopia.krito.rezetopiakrito.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.rezetopia.krito.rezetopiakrito.R;

/**
 * Created by Mona Abdallh on 4/30/2018.
 */

public class GroupReport extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_report, container, false);
        return view;
    }
}
