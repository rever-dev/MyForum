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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.bean.Member;
import com.rever.myforum.model.MemberBase;

public class MemberDetailFragment extends Fragment {

    private Activity activity;
    private Button buttonEditMemberDetail, buttonMyPost, buttonMyFav, buttonSignOut;
    private TextView textViewNickname;
    private ImageView imageViewAvatar;
    private SharedPreferences shp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shp = MainActivity.getShp(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonEditMemberDetail = view.findViewById(R.id.memberDetail_buttonEditMemberDetail);
        buttonMyPost = view.findViewById(R.id.memberDetail_buttonMyPost);
        buttonMyFav = view.findViewById(R.id.memberDetail_buttonMyFav);
        buttonSignOut = view.findViewById(R.id.memberDetail_buttonsignOut);
        textViewNickname = view.findViewById(R.id.memberDetail_textViewMemberNickname);
        imageViewAvatar = view.findViewById(R.id.memberDetail_imageViewAvatar);

        buttonEditMemberDetail.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editMemberDetailFragment);
        });
        buttonSignOut.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.forumFragment);
            shp.edit().clear().apply();
            MemberBase.signOut();
            Toast.makeText(activity, R.string.toast_signOutSuccess, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MemberBase.getMemberDetail(activity, MemberBase.getMember().getAccount());
        textViewNickname.setText(MemberBase.getMember().getNickname());
        if (MemberBase.getMemberAvatar() == null) {
            imageViewAvatar.setImageResource(R.drawable.account_default_image);
        } else {
            Glide.with(this)
                    .applyDefaultRequestOptions(new RequestOptions().override(320, 320))
                    .load(MemberBase.getMemberAvatar())
                    .transform(new CircleCrop())
                    .into(imageViewAvatar);
        }
    }
}