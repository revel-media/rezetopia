package app.reze1.ahmed.reze1.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.reze1.ahmed.reze1.R;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class EventDescriptionFragment extends Fragment {

    private static final String DESCRIPTION_EXTRA = "description_extra";

    TextView desView;

    public static EventDescriptionFragment createFragment(String description){
        Bundle bundle = new Bundle();
        bundle.putString(DESCRIPTION_EXTRA, description);
        EventDescriptionFragment fragment = new EventDescriptionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_description, container, false);

        desView = view.findViewById(R.id.desView);
        desView.setText(getArguments().getString(DESCRIPTION_EXTRA));
        return view;
    }
}
