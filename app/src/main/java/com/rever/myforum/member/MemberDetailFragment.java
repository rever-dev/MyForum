package com.rever.myforum.member;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.bean.Member;
import com.rever.myforum.model.MemberBase;

public class MemberDetailFragment extends Fragment {

    private Activity activity;
    private Button buttonSignOut;
    private SharedPreferences shp;

    private Member member;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shp = MainActivity.getShp(activity);
        MemberBase.getMemberDetail(activity, shp.getString("account", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.memberDetail_buttonsignOut).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.memberFragment);
            shp.edit().clear().apply();
        });
    }
}