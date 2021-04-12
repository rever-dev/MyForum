package com.rever.myforum.member;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.rever.myforum.PostAdapter;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.model.PostBase;
import com.rever.myforum.model.PostList;
import com.rever.myforum.util.mAlertDialog;

import java.util.List;

public class MyPostFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        PostList.queryMyPost(activity, MemberBase.getMember().getId());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.myPost_recyclerView);
        toolbar = view.findViewById(R.id.myPost_toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new PostAdapter(activity, PostList.getPostList()));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int postId = ((PostAdapter) recyclerView.getAdapter()).getPostId();
        int position = ((PostAdapter) recyclerView.getAdapter()).getPosition();
        if (item.getItemId() == R.id.postMenu_edit) {
            Bundle bundle = new Bundle();
            bundle.putInt("postId", postId);
            Navigation.findNavController(getView()).navigate(R.id.editPostFragment, bundle);
        } else if (item.getItemId() == R.id.postMenu_delete) {
            mAlertDialog.createAlertDialog(activity, "確定刪除文章？", "確定", "再想想", (dialog, which) -> {
                PostBase.deletePost(activity, postId);
                PostList.getPostList().remove(position);
                setRecyclerView(PostList.getPostList());
            }, (dialog, which) -> dialog.dismiss());
        } else if (item.getItemId() == R.id.postMenu_fav) {
            PostBase.addPostFav(activity, postId);
            PostList.queryMyPost(activity, MemberBase.getMember().getId());
            setRecyclerView(PostList.getPostList());
        } else if (item.getItemId() == R.id.postMenu_unFav) {
            PostBase.deletePostFav(activity, postId);
            PostList.queryMyPost(activity, MemberBase.getMember().getId());
            setRecyclerView(PostList.getPostList());
        }
        return super.onContextItemSelected(item);
    }

    /*
     * 設置recyclerView
     * */
    private void setRecyclerView(List<Post> postList) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new PostAdapter(activity, postList));
        } else {
            ((PostAdapter) recyclerView.getAdapter()).update(postList);
        }
    }
}