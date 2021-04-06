package com.rever.myforum.forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rever.myforum.MainActivity;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.model.PostBase;

public class PostViewFragment extends Fragment {

    private Activity activity;
    private Bundle bundle;
    private CheckBox checkBoxLike, checkBoxFav;
    private ImageView imageViewUserAvatar;
    private RecyclerView recyclerView;
    private TextView textViewTitle, textViewUserNickname, textViewDatetime, textViewContent;
    private FloatingActionButton floatingActionButton;

    private SharedPreferences shp;
    private Post post;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bundle = getArguments();
        shp = MainActivity.getShp(activity);
        PostBase.queryPost(activity, bundle.getInt("postId"));
        post = PostBase.getPost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkBoxLike = view.findViewById(R.id.postView_checkBoxLike);
        checkBoxFav = view.findViewById(R.id.postView_checkBoxFav);
        imageViewUserAvatar = view.findViewById(R.id.postView_imageViewUserAvatar);
        recyclerView = view.findViewById(R.id.postView_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        textViewTitle = view.findViewById(R.id.postView_textViewTitle);
        textViewUserNickname = view.findViewById(R.id.postView_textViewUserNickname);
        textViewDatetime = view.findViewById(R.id.postView_textViewDatetime);
        textViewContent = view.findViewById(R.id.postView_textViewContent);
        floatingActionButton = view.findViewById(R.id.postView_floatingActionButton);
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewTitle.setText(post.getTitle());
        textViewUserNickname.setText(post.getMemberNickname());
        textViewDatetime.setText(post.getDatetime());
        textViewContent.setText(post.getContent());
        checkBoxLike.setText(String.valueOf(post.getLikeCount()));
        checkBoxLike.setChecked(PostBase.checkedLikeStatus(MemberBase.getMember().getId()));
        checkBoxFav.setChecked(PostBase.checkedFavStatus(MemberBase.getMember().getId()));
        setListener();
    }

    private void setListener() {
        /*
         * 按讚監聽設置
         * */
        if (!shp.getBoolean("signIn", false)) {
            checkBoxLike.setClickable(false);
        }
        checkBoxLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                post.setLikeCount(post.getLikeCount() + 1);
                checkBoxLike.setText(String.valueOf(post.getLikeCount()));
                PostBase.addPostLike(activity, post.getId(), post.getLikeCount());
            } else {
                post.setLikeCount(post.getLikeCount() - 1);
                checkBoxLike.setText(String.valueOf(post.getLikeCount()));
                PostBase.deletePostLike(activity, post.getId(), post.getLikeCount());
            }
        });
        /*
         * 收藏監聽設置
         * */
        if (!shp.getBoolean("signIn", false)) {
            checkBoxFav.setClickable(false);
        }
        checkBoxFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PostBase.addPostFav(activity, post.getId());
            } else {
                PostBase.deletePostFav(activity, post.getId());
            }
        });
    }
}