package io.rezetopia.krito.rezetopiakrito.fragments;

        import android.support.v4.app.Fragment;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.TextView;

        import io.rezetopia.krito.rezetopiakrito.R;

/**
 * Created by ahmed on 4/23/2018.
 */

public class VideosProfile extends Fragment {
    String childname;
    TextView textViewChildName;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos_profile, container, false);
        Bundle bundle = getArguments();
        //childname = bundle.getString("data");
        return view;
    }

}

