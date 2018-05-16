package io.rezetopia.krito.rezetopiakrito.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import io.rezetopia.krito.rezetopiakrito.R;
import io.rezetopia.krito.rezetopiakrito.activities.Login;
import io.rezetopia.krito.rezetopiakrito.activities.WebActivity;
import io.rezetopia.krito.rezetopiakrito.app.AppConfig;

/**
 * Created by Mona Abdallh on 5/3/2018.
 */

public class SideMenuFragment extends Fragment implements View.OnClickListener {

    TextView myTeamView;
    TextView sideEventsView;
    TextView sideSavedPostsView;
    TextView sideStoreView;
    TextView policy;
    TextView logoutView;
    TextView groupTxt;
    TextView txt_report;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu, container, false);

        myTeamView = view.findViewById(R.id.myTeamView);
        sideEventsView = view.findViewById(R.id.sideEventsView);
        sideSavedPostsView = view.findViewById(R.id.sideSavedPostsView);
        sideStoreView = view.findViewById(R.id.sideStoreView);
        policy = view.findViewById(R.id.policy);
        logoutView = view.findViewById(R.id.logoutView);
        groupTxt = view.findViewById(R.id.txt_groups);
        txt_report = view.findViewById(R.id.txt_report);

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                startActivity(intent);
            }
        });


        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSharedPreferences(AppConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
                        .putString(AppConfig.LOGGED_IN_USER_ID_SHARED, "0").apply();

                if (LoginManager.getInstance() != null)
                    LoginManager.getInstance().logOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        myTeamView.setOnClickListener(this);
        sideEventsView.setOnClickListener(this);
        sideSavedPostsView.setOnClickListener(this);
        sideStoreView.setOnClickListener(this);
        policy.setOnClickListener(this);
        //logoutView.setOnClickListener(this);
        groupTxt.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        AlertFragment fragment;
        switch (v.getId()){
            case R.id.myTeamView:
                /*intent = new Intent(getActivity(), ViewTeamsActivity.class);
                startActivity(intent);*/
                fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
                break;

            case R.id.sideEventsView:
                /*intent = new Intent(getActivity(), MyEventsActivity.class);
                startActivity(intent);*/
                fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
                break;

            case R.id.sideSavedPostsView:
                fragment = AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(), null);
                break;

            case R.id.sideStoreView:
                fragment=AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(),null);
//                intent = new Intent(getActivity(), StoresListActivity.class);
//                startActivity(intent);

                break;
            case R.id.txt_groups:
                fragment=AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(),null);
                break;
            case R.id.txt_setting:
                fragment=AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(),null);
                break;
            case R.id.txt_report:
                fragment=AlertFragment.createFragment("قريبا في النسخة القادمة");
                fragment.show(getActivity().getFragmentManager(),null);
            break;

            default:
                break;
        }
    }
}
