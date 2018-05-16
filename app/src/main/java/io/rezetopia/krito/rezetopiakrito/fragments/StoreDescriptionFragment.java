package io.rezetopia.krito.rezetopiakrito.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.rezetopia.krito.rezetopiakrito.R;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class StoreDescriptionFragment extends Fragment {

    private static final String STORE_ID_EXTRA = "store_id";
    private static final String STORE_DESCRIPTION_EXTRA = "store_id";

    int storeId;
    String description;
    TextView descriptionView;

    public static StoreDescriptionFragment createFragment(int id, String des){
        Bundle bundle = new Bundle();
        bundle.putInt(STORE_ID_EXTRA, id);
        bundle.putString(STORE_DESCRIPTION_EXTRA, des);
        StoreDescriptionFragment fragment = new StoreDescriptionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_product, container, false);

        descriptionView = view.findViewById(R.id.descriptionView);


        if (getArguments() != null) {
            if (getArguments().getInt(STORE_ID_EXTRA) > 0) {
                storeId = getArguments().getInt(STORE_ID_EXTRA);
                description = getArguments().getString(STORE_DESCRIPTION_EXTRA, null);
                descriptionView.setText(description);
            }
        }

        return view;
    }
}
